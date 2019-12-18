package Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import Interface.ArduinoInterface;
import Map.BtnRight;
import Map.Cell;
import Map.MapApp;
import Map.MapLeft;
import Network.NetworkManager;
import Robot.Robot;

public class FastestPathStepByStep {
	
	protected ArrayList<Cell> nodeList1 = new ArrayList<Cell>();
	protected ArrayList<Cell> expandedList1 = new ArrayList<Cell>();
	protected ArrayList<Cell> exploredList1 = new ArrayList<Cell>();
	
	protected ArrayList<Cell> nodeList2 = new ArrayList<Cell>();
	protected ArrayList<Cell> expandedList2 = new ArrayList<Cell>();
	protected ArrayList<Cell> exploredList2 = new ArrayList<Cell>();
	
	protected ArrayList<Integer> iValues = new ArrayList<Integer>();
	protected ArrayList<Integer> jValues = new ArrayList<Integer>();
	protected ArrayList<Integer> iValues2 = new ArrayList<Integer>();
	protected ArrayList<Integer> jValues2 = new ArrayList<Integer>();
	protected ArrayList<Integer> finalI = new ArrayList<Integer>();
	protected ArrayList<Integer> finalJ = new ArrayList<Integer>();
	
	protected int straightDist = 50;
	protected int turningDist =  90;
	
	public static Cell goalNode1 = MapLeft.cellArray[MapLeft.ROWS - BtnRight.corY - 1][BtnRight.corX]; //waypoint
	public static Cell goalNode2 = MapLeft.cellArray[1][13]; //goal zone
	public static Cell startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
			[Robot.getX() / MapLeft.GRID_SIZE]; //starting point within start zone
	
	protected static boolean isPastWaypoint = false; 
	protected static int indexTraverse = -1; 
	protected static int countIndex;
	protected static int countIndex1;
	protected static int countIndex2;
	protected static int counterSample = 0;
	public static boolean fastestPath = false; //colour map for fastest path route
	public static boolean isExploration = false; //return to start zone after manual termination
	
	
	ArrayList<Integer> indexArray = new ArrayList<Integer>();
	ArrayList<Integer> indexArray2 = new ArrayList<Integer>();
	
	
	public FastestPathStepByStep() throws InterruptedException{
		//if (ExplorationReal.startExploration){
			nodeList1.clear();
			exploredList1.clear();
			expandedList1.clear();
			nodeList2.clear();
			exploredList2.clear();
			expandedList2.clear();
			
			initNodes();
			initGVal();
			boolean isFound = findPath();
			
			if (isFound){
				System.out.println("Path to waypoint has been found");
				countIndex = calculateNumNodes();
				countIndex1 = countIndex;
				indexTraverse = 0;
				printPath();
				
			}
			
			startNextPath();
			
			printPathValuesIJ();
			
			checkDirection();
		//}
		
	}
	
	public void startNextPath(){
		for ( int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				MapLeft.cellArray[i][j].setParent(null);
			}
		}
		nodeList1.clear();
		exploredList1.clear();
		expandedList1.clear();
		nodeList2.clear();
		exploredList2.clear();
		expandedList2.clear();
		
		isPastWaypoint = true;
		initNodes();
		initGVal();
		boolean isFound2 = findPath();
		if (isFound2){
			System.out.println("Path to goal zone has been found");
			countIndex = calculateNumNodes();
			countIndex2 = countIndex;
			indexTraverse = 0;
			counterSample = 0;
			printPath();
		}
	}
	
	public void initNodes(){
		int index = 0;
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (!isPastWaypoint){
					nodeList1.add(MapLeft.cellArray[i][j]); //add ALL the grid cells
					initHVal(nodeList1.get(index), goalNode1);
				}
				else{
					nodeList2.add(MapLeft.cellArray[i][j]);
					initHVal(nodeList2.get(index), goalNode2);
				}
				index++;
			}
		}
		if (!isPastWaypoint){
			if (nodeList1.contains(goalNode1) == false){
				nodeList1.add(goalNode1);
			}
		}
		
		else{
			if (!nodeList2.contains(goalNode2)){
				nodeList2.add(goalNode2);
			}
		}
		
	}
	
	
	public void initHVal(Cell node1, Cell node2){
		int x1 = node1.getX() * MapLeft.GRID_SIZE;
		int y1 = node1.getY() * MapLeft.GRID_SIZE;
		int x2 = node2.getX() * MapLeft.GRID_SIZE;
		int y2 = node2.getY() * MapLeft.GRID_SIZE;
		int xDist = Math.abs(x2 - x1);
		int yDist = Math.abs(y2 - y1);
		node1.setH(xDist + yDist);
	}
	
	
	public void initGVal(){
		
		//from start zone to waypoint
		if (!isPastWaypoint){
			Cell tempCell = startNode; //starting node
			int i = 18; //Robot.getY() / MapLeft.GRID_SIZE
			int j = 1; //Robot.getX() / MapLeft.GRID_SIZE
			expandCell(i, j); //calculates the g and f values
			
			expandedList1.remove(tempCell);
			nodeList1.remove(tempCell);
			exploredList1.add(tempCell);
			
			Collections.sort(expandedList1, new Comparator<Cell>(){ 
				public int compare(Cell node1, Cell node2){
					return Double.valueOf(node1.getF()).compareTo(node2.getF());
				}
			});
		}
		
		//from waypoint to goal zone
		else{
			Cell tempCell = goalNode1; //starting node
			int i = goalNode1.getX(); //vertical from top left
			int j = goalNode1.getY(); //horizontal from top left
			expandCell(i, j); //calculates the g and f values
			
			expandedList2.remove(tempCell);
			nodeList2.remove(tempCell);
			exploredList2.add(tempCell); //first node is goalNode1
			
			Collections.sort(expandedList2, new Comparator<Cell>(){ 
				public int compare(Cell node1, Cell node2){
					return Double.valueOf(node1.getF()).compareTo(node2.getF());
				}
			});
		}
		
	}
	
	//expand cell to find the children
	public void expandCell(int i, int j){
		Cell parentCell = MapLeft.cellArray[i][j];
		
		if (haveNorth(i, j)){
			Cell tempCell = MapLeft.cellArray[i-1][j]; //north child node found
			
			tempCell.setParent(parentCell);
			
			if (Robot.dir == Robot.ROBOT_DIR.NORTH){
				tempCell.setG(straightDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell != startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell != goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			else{
				tempCell.setG(turningDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			if (!isPastWaypoint){
				if (expandedList1.contains(tempCell)){
					int index = expandedList1.indexOf(tempCell);
					if (expandedList1.get(index).getF() > tempCell.getF()){
						expandedList1.remove(index);
					}
				}
				expandedList1.add(tempCell);
			}
			
			else{
				if (expandedList2.contains(tempCell)){
					int index = expandedList2.indexOf(tempCell);
					if (expandedList2.get(index).getF() > tempCell.getF()){
						expandedList2.remove(index);
					}
				}
				expandedList2.add(tempCell);
			}
			
		}
		
		if (haveEast(i, j)){
			Cell tempCell = MapLeft.cellArray[i][j+1];
			
			tempCell.setParent(parentCell);
			
			if (Robot.dir == Robot.ROBOT_DIR.EAST){
				tempCell.setG(straightDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			else{
				tempCell.setG(turningDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if(!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
				
			}
			
			if (!isPastWaypoint){
				if (expandedList1.contains(tempCell)){
					int index = expandedList1.indexOf(tempCell);
					if (expandedList1.get(index).getF() > tempCell.getF()){
						expandedList1.remove(index);
					}
				}
				expandedList1.add(tempCell);
			}
			else{
				if (expandedList2.contains(tempCell)){
					int index = expandedList2.indexOf(tempCell);
					if (expandedList2.get(index).getF() > tempCell.getF()){
						expandedList2.remove(index);
					}
				}
				expandedList2.add(tempCell);
			}
		}
		
		if (haveSouth(i, j)){
			Cell tempCell = MapLeft.cellArray[i+1][j];
			
			tempCell.setParent(parentCell);
			
			if (Robot.dir == Robot.ROBOT_DIR.SOUTH){
				tempCell.setG(straightDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			else{
				tempCell.setG(turningDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
						if (aTempCell.getParent() == null){
							break;
						}
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
						if (aTempCell.getParent() == null){
							break;
						}
					}
				}
				
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			if (!isPastWaypoint){
				if (expandedList1.contains(tempCell)){
					int index = expandedList1.indexOf(tempCell);
					if (expandedList1.get(index).getF() > tempCell.getF()){
						expandedList1.remove(index);
					}
				}
				expandedList1.add(tempCell);
			}
			else{
				if (expandedList2.contains(tempCell)){
					int index = expandedList2.indexOf(tempCell);
					if (expandedList2.get(index).getF() > tempCell.getF()){
						expandedList2.remove(index);
					}
				}
				expandedList2.add(tempCell);
			}
		}
		
		if (haveWest(i, j)){
			Cell tempCell = MapLeft.cellArray[i][j-1];
			
			tempCell.setParent(parentCell);
			
			if (Robot.dir == Robot.ROBOT_DIR.WEST){
				tempCell.setG(straightDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			else{
				tempCell.setG(turningDist);
				double totalGVal = 0;
				Cell aTempCell = tempCell;
				if (!isPastWaypoint){
					while (aTempCell!=startNode){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				else{
					while (aTempCell!=goalNode1){
						totalGVal = totalGVal + aTempCell.getG();
						aTempCell = aTempCell.getParent();
					}
				}
				tempCell.setG(totalGVal);
				tempCell.setF(totalGVal + tempCell.getH());
			}
			
			if (!isPastWaypoint){
				if (expandedList1.contains(tempCell)){
					int index = expandedList1.indexOf(tempCell);
					if (expandedList1.get(index).getF() > tempCell.getF()){
						expandedList1.remove(index);
					}
				}
				expandedList1.add(tempCell);
			}
			else{
				if (expandedList2.contains(tempCell)){
					int index = expandedList2.indexOf(tempCell);
					if (expandedList2.get(index).getF() > tempCell.getF()){
						expandedList2.remove(index);
					}
				}
				expandedList2.add(tempCell);
			}
		}
		
		//no child nodes
//		else{
//			System.out.println("NO CHILD NODES FOUND!!!!!");
//		}
	}
	
	public boolean haveNorth(int i, int j){
		if (i-2 < 0){
			return false;
		}
		/*if (MapLeft.cellArray[i-2][j-1].getIsExplored() || MapLeft.cellArray[i-2][j].getIsExplored()
				|| MapLeft.cellArray[i-2][j+1].getIsExplored()){
			return false;
		}*/
		if (MapLeft.cellArray[i-2][j-1].getIsObstacle() || MapLeft.cellArray[i-2][j].getIsObstacle()
				|| MapLeft.cellArray[i-2][j+1].getIsObstacle()){
			return false;
		}
		if (exploredList1.contains(MapLeft.cellArray[i-1][j].getParent()) ||
				exploredList2.contains(MapLeft.cellArray[i-1][j].getParent())){
			return false;
		}
		
		
		
		return true;
	}
	
	public boolean haveEast(int i, int j){
		if (j+2 > 14){
			return false;
		}
		/*if (MapLeft.cellArray[i][j+2].getIsExplored() || MapLeft.cellArray[i-1][j+2].getIsExplored()
				|| MapLeft.cellArray[i+1][j+2].getIsExplored()){
			return false;
		}*/
		if (MapLeft.cellArray[i][j+2].getIsObstacle() || MapLeft.cellArray[i-1][j+2].getIsObstacle()
				|| MapLeft.cellArray[i+1][j+2].getIsObstacle()){
			return false;
		}
		if (exploredList1.contains(MapLeft.cellArray[i][j+1].getParent()) ||
				exploredList2.contains(MapLeft.cellArray[i][j+1].getParent())){
			return false;
		}
		
		
		
		return true;
	}
	
	public boolean haveSouth(int i, int j){
		if (i+2 > 19){
			return false;
		}
		/*if (MapLeft.cellArray[i+2][j].getIsExplored() || MapLeft.cellArray[i+2][j-1].getIsExplored()
				|| MapLeft.cellArray[i+2][j+1].getIsExplored()){
			return false;
		}*/
		if (MapLeft.cellArray[i+2][j].getIsObstacle() || MapLeft.cellArray[i+2][j-1].getIsObstacle()
				|| MapLeft.cellArray[i+2][j+1].getIsObstacle()){
			return false;
		}
		if (exploredList1.contains(MapLeft.cellArray[i+1][j].getParent()) ||
				exploredList2.contains(MapLeft.cellArray[i+1][j].getParent())){
			return false;
		}
		
		
		return true;
	}
	
	public boolean haveWest(int i, int j){
		if (j-2 < 0){
			return false;
		}
		/*if (MapLeft.cellArray[i][j-2].getIsExplored() || MapLeft.cellArray[i-1][j-2].getIsExplored()
				|| MapLeft.cellArray[i+1][j-2].getIsExplored()){
			return false;
		}*/
		if (MapLeft.cellArray[i][j-2].getIsObstacle() || MapLeft.cellArray[i-1][j-2].getIsObstacle()
				|| MapLeft.cellArray[i+1][j-2].getIsObstacle()){
			return false;
		}
		if (exploredList1.contains(MapLeft.cellArray[i][j-1].getParent()) ||
				exploredList2.contains(MapLeft.cellArray[i][j-1].getParent())){
			return false;
		}
		
		
		return true;
	}
	
	
	public boolean findPath(){
		
		if (!isPastWaypoint){
			Cell tempCell = expandedList1.get(0);
			//continue search until goalNode1 is at the head of the queue(expanded list)
			while (tempCell!=goalNode1){
				int i = tempCell.getX();
				int j = tempCell.getY();
				//System.out.println("i value: "+ i + " j value: "+ j);
				expandCell(i, j); //compute f and g values
				expandedList1.remove(tempCell);
				nodeList1.remove(tempCell);
				exploredList1.add(tempCell);
				
				Collections.sort(expandedList1, new Comparator<Cell>(){ 
					public int compare(Cell node1, Cell node2){
						return Double.valueOf(node1.getF()).compareTo(node2.getF());
					}
				});
				if (expandedList1.size()==0){
					System.out.println("No path found");
					return false;
				}
				
				tempCell = expandedList1.get(0);
			}
			exploredList1.add(goalNode1);
			return true;
			
		}
		
		else{
			Cell tempCell = expandedList2.get(0);
			while (tempCell!=goalNode2){
				int i = tempCell.getX();
				int j = tempCell.getY();
				//System.out.println("i value: "+ i + " j value: "+ j);
				expandCell(i, j); //add in the child nodes to expandedList
				expandedList2.remove(tempCell);
				nodeList2.remove(tempCell);
				exploredList2.add(tempCell);
				
				Collections.sort(expandedList2, new Comparator<Cell>(){ 
					public int compare(Cell node1, Cell node2){
						return Double.valueOf(node1.getF()).compareTo(node2.getF());
					}
				});
				tempCell = expandedList2.get(0);
			}
			exploredList2.add(goalNode2);
			return true;
		}
		
		
	}
	
	public void printPath(){
		while (indexTraverse < countIndex){
			//System.out.println("indexTraverse: "+indexTraverse);
			//System.out.println("countIndex: "+countIndex);
			//System.out.println(indexArray);
		
			if (!isPastWaypoint){
				if(MapLeft.real_bot==false) {
					int newIndex = indexArray.get(indexTraverse);
					int x = exploredList1.get(newIndex).getX();
					int y = exploredList1.get(newIndex).getY();
					//int result = checkNextDir(indexTraverse);
					Robot.setX((y * MapLeft.GRID_SIZE) + 25);
					Robot.setY((x * MapLeft.GRID_SIZE) + 25);
					
					
					//MapApp.panelLeft.drawing();
				}
				
				//if real robot
				else {
					int newIndex = indexArray.get(indexTraverse);
					int x = exploredList1.get(newIndex).getX();
					int y = exploredList1.get(newIndex).getY();
					//int result = checkNextDir(indexTraverse);
					Robot.setX((y * MapLeft.GRID_SIZE) + 25);
					Robot.setY((x * MapLeft.GRID_SIZE) + 25);
					MapApp.panelLeft.drawing();
					
				}
				indexTraverse++;
				
			}
			
			//is past waypoint/goalNode1
			else{
				if (indexTraverse < countIndex){
					if(MapLeft.real_bot==false) {
						int newIndex = indexArray2.get(indexTraverse);
						int x = exploredList2.get(newIndex).getX();
						int y = exploredList2.get(newIndex).getY();
						//int result = checkNextDir(indexTraverse);
						
						Robot.setX((y * MapLeft.GRID_SIZE) +25); //send to rpi
						Robot.setY((x * MapLeft.GRID_SIZE) +25); //send to rpi
						
						
					}
					
					//if real robot
					else {
						int newIndex = indexArray2.get(indexTraverse);
						int x = exploredList2.get(newIndex).getX();
						int y = exploredList2.get(newIndex).getY();
						//int result = checkNextDir(indexTraverse);
						
						Robot.setX((y * MapLeft.GRID_SIZE) +25); //send to rpi
						Robot.setY((x * MapLeft.GRID_SIZE) +25); //send to rpi
											
					}
					indexTraverse++;
					
				}
				
				//move extra 1 step to goal
				else{
					if(MapLeft.real_bot==false) {
						//move east
						
						if (Robot.getX() == 625 && Robot.getY() == 75){
							Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
							Robot.setY(Robot.getY());
							Robot.setDirection(Robot.ROBOT_DIR.EAST);
						}
						
						//move north
						else if (Robot.getX() == 675 && Robot.getY() == 125){
							Robot.setX(Robot.getX());
							Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
							Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						}
						//move west
						else if (Robot.getX()==125 && Robot.getY()==925){
							Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
							Robot.setY(Robot.getY());
							Robot.setDirection(Robot.ROBOT_DIR.WEST);
						}
						
						//move south
						else if(Robot.getX()==75 && Robot.getY()==875){
							Robot.setX(Robot.getX());
							Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
							Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						}
					}
					//if real robot
					else {
						
						//move east
						if (Robot.getX() == 625 && Robot.getY() == 75){
							//set robot location
							Robot.setI(Robot.getI() + 1);
							Robot.setJ(Robot.getJ());
							Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
							Robot.setY(Robot.getY());
							Robot.setDirection(Robot.ROBOT_DIR.EAST);
						}
						//move north
						else if (Robot.getX() == 675 && Robot.getY() == 125){
							//set robot location
							Robot.setI(Robot.getI());
							Robot.setJ(Robot.getJ() + 1);
							Robot.setX(Robot.getX());
							Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
							Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						}
						//move west
						else if (Robot.getX()==125 && Robot.getY()==925){
							//set robot location
							Robot.setI(Robot.getI() - 1);
							Robot.setJ(Robot.getJ());
							Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
							Robot.setY(Robot.getY());
							Robot.setDirection(Robot.ROBOT_DIR.WEST);
						}
						//move south
						else if(Robot.getX()==75 && Robot.getY()==875){
							//set robot location
							Robot.setI(Robot.getI());
							Robot.setJ(Robot.getJ() - 1);
							Robot.setX(Robot.getX());
							Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
							Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						}
					}
					
					
				}
				
			}
		
			//colour the path that the robot is moving
			Exploration.startExploration = true;
			fastestPath = true;
			if (MapLeft.real_bot == false){
				
				int yy = Robot.getX() / MapLeft.GRID_SIZE; //i (yy) [send to arduino]
				int ii = Robot.getY() / MapLeft.GRID_SIZE; //j (19 - ii) [send to arduino]
				
				//////////////////////////////////////////////////////////////////////////////////////////////////
				if (!isPastWaypoint){
					iValues.add(yy);
					jValues.add(19 - ii);
					//System.out.println("i = "+yy+ " j = "+ (19-ii) );
					//System.out.println("iValuez: "+iValues.get(counterSample) + " jValuez: "+jValues.get(counterSample));
					counterSample++;
					if (counterSample>=countIndex){
						counterSample = 0;
						break;
					}
				}
				else{
					iValues2.add(yy);
					jValues2.add(19 - ii);
					//System.out.println("i = "+yy+ " j = "+ (19-ii) );
					//System.out.println("iValuezz: "+iValues2.get(counterSample) + " jValuezz: "+jValues2.get(counterSample));
					counterSample++;
					if (counterSample>=countIndex){
						counterSample = 0;
						break;
					}
				}
				
				
				/*Robot.setX(Robot.getX());
				Robot.setY(Robot.getY());
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				MapLeft.cellArray[ii][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii][yy+1].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy+1].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy+1].setIsExplored(true, true);
				
		
				MapApp.panelLeft.drawing();*/	
			}
			
			//if real robot
			else if (MapLeft.real_bot == true && ExplorationReal.startExploration == true){
				int yy = (int)(Robot.getX() / MapLeft.GRID_SIZE); //i (yy) [send to arduino]
				int ii = (int)(Robot.getY() / MapLeft.GRID_SIZE); //j (19 - ii) [send to arduino]
				
				if (!isPastWaypoint){
				//////////////////////////////////////////////////////////////////////////////////////////////////
					iValues.add(yy);
					jValues.add(19 - ii);
					System.out.println("i = "+yy+ " j = "+ (19-ii) );
					//System.out.println("iValue: "+iValues.get(indexTraverse) + " jValue: "+jValues.get(indexTraverse));
					Robot.setX(Robot.getX());
					Robot.setY(Robot.getY());
					//MapApp.panelLeft.drawing();
				}
				else{
					iValues2.add(yy);
					jValues2.add(19 - ii);
					//System.out.println("i = "+yy+ " j = "+ (19-ii) );
					//System.out.println("iValuezz: "+iValues2.get(counterSample) + " jValuezz: "+jValues2.get(counterSample));
					counterSample++;
					if (counterSample>=countIndex){
						counterSample = 0;
						break;
					}
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////
				
				

				
				MapLeft.cellArray[ii][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy].setIsExplored(true, true);
				MapLeft.cellArray[ii][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy-1].setIsExplored(true, true);
				MapLeft.cellArray[ii][yy+1].setIsExplored(true, true);
				MapLeft.cellArray[ii-1][yy+1].setIsExplored(true, true);
				MapLeft.cellArray[ii+1][yy+1].setIsExplored(true, true);
				
				MapApp.panelLeft.drawing();	
				
			}
		}
		
	}
		
	

	
	public int calculateNumNodes(){
		if (!isPastWaypoint){
			int lastIndex = exploredList1.indexOf(goalNode1);
			int index1 = lastIndex;
			int count = 0;
			
			//index1 = 0 (AKA the starting node)
			while (index1 != 0){ //number of cells to move
				count++;
				Cell tempCell = exploredList1.get(index1).getParent();
				index1 = exploredList1.indexOf(tempCell);
				indexArray.add(0, index1); 
			}
			indexArray.add(0, 0); //add starting node index to the beginning
			count++;
			return count;
		}
		else{
			int lastIndex = exploredList2.indexOf(goalNode2);
			int index1 = lastIndex;
			int count = 0;
			
			while (index1 != 0){ //number of cells to move
				count++;
				Cell tempCell = exploredList2.get(index1).getParent();
				index1 = exploredList2.indexOf(tempCell);
				indexArray2.add(0, index1);
			}
			indexArray2.add(0, 0);
			count++;
			return count;
		}
	}
	

	
	public void printPathValuesIJ(){
		int count = 1;
		while (count<countIndex1){
			//System.out.println("iVal1: "+iValues.get(count) + " jVal1: "+jValues.get(count));
			finalI.add(iValues.get(count));
			finalJ.add(jValues.get(count));
			count++;
		}
		
		count=0;
		while(count<countIndex2){
			//System.out.println("iVal2: "+iValues2.get(count) + " jVal2: "+jValues2.get(count));
			finalI.add(iValues2.get(count));
			finalJ.add(jValues2.get(count));
			count++;
		}
		
		//move extra step west, right
		if (finalI.get(finalI.size() - 1) == 2 && finalJ.get(finalJ.size() - 1) == 1){
			finalI.add(1);
			finalJ.add(1);
			//finalI.add(1);
			//finalJ.add(0);
		}
		
		//move extra step south, down
		else if (finalI.get(finalI.size() - 1) == 1 && finalJ.get(finalJ.size() - 1) == 2){
			finalI.add(1);
			finalJ.add(1);
			//finalI.add(2);
			//finalJ.add(1);
		}
		
		//move extra step east, left
		else if (finalI.get(finalI.size() - 1) == 12 && finalJ.get(finalJ.size() - 1) == 18){
			finalI.add(13);
			finalJ.add(18);
			//finalI.add(13);
			//finalJ.add(19);
		}
		
		//move extra step north, up
		else if (finalI.get(finalI.size() - 1) == 13 && finalJ.get(finalJ.size() - 1) == 17){
			finalI.add(13);
			finalJ.add(18);
			//finalI.add(14);
			//finalJ.add(18);
		}
		
		
		
		
		for (int i=0; i<finalI.size(); i++){
			System.out.println("Final i: "+finalI.get(i) + " Final j: "+finalJ.get(i) + " Direction: "+Robot.getDirection());
		}
	}
	
	public void checkDirection() throws InterruptedException{
		for (int i=0; i<finalI.size() - 1; i++){
			//String message = MapApp.nm.receiveMessage();
			String message;
			do{
				ArduinoInterface.returnSensorData();
				message = ArduinoInterface.returnMessage("SENDATA");
			}while(message == null);
			String messageType = MapLeft.decodeMessageType(message);
			
			
			if (messageType.equals(NetworkManager.SENSOR_DATA)){
				
				System.out.println("ENTERING HERERERERER");
			
				//move west, right
				if (finalI.get(i) > finalI.get(i+1)){
					if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
						//MapApp.nm.sendMessage("1~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"WEST", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
						//MapApp.nm.sendMessage("4~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft180();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"WEST", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
						//MapApp.nm.sendMessage("2~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnRight();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"WEST", NetworkManager.BOT_POS);
					}
					
					Robot.setDirection(Robot.ROBOT_DIR.WEST);
					Sensor.SensorReal.goForwardRealSensor1(message);
					//MapApp.nm.sendMessage("0~", NetworkManager.INSTRUCTIONS);
					
					String botMessage;
					do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage("BOTDONE");
					}while(botMessage == null);
					
					Robot.setI(finalI.get(i+1));
					Robot.setJ(finalJ.get(i+1));
					System.out.println("i = "+Robot.getI());
					System.out.println("j = "+ Robot.getJ());
					System.out.println("Final i: "+finalI.get(i+1));
					System.out.println("Final j: "+finalJ.get(i+1));
					Robot.setX((Robot.getI() * MapLeft.GRID_SIZE) + 25);
					Robot.setY((19 - Robot.getJ()*MapLeft.GRID_SIZE) + 25);
					
					String roboti = Integer.toString(Robot.getI());
					String robotj = Integer.toString(Robot.getJ());
					MapApp.nm.sendMessage(roboti + " " + robotj + " "+ "WEST", NetworkManager.BOT_POS);
					Thread.sleep(ExplorationReal.timeSleepAndroid);
					ExplorationReal.genMapFileEvent();
					
					MapApp.panelLeft.drawing();
				}
				
				//move east, right
				else if (finalI.get(i) < finalI.get(i+1)){
					if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
						//MapApp.nm.sendMessage("2~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnRight();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"EAST", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
						//MapApp.nm.sendMessage("4~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft180();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"EAST", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
						//MapApp.nm.sendMessage("1~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"EAST", NetworkManager.BOT_POS);
					}
					Robot.setDirection(Robot.ROBOT_DIR.EAST);
					Sensor.SensorReal.goForwardRealSensor1(message);
					//MapApp.nm.sendMessage("0~", NetworkManager.INSTRUCTIONS);
					String botMessage;
					do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage("BOTDONE");
					}while(botMessage == null);
					Robot.setI(finalI.get(i+1));
					Robot.setJ(finalJ.get(i+1));
					Robot.setX((Robot.getI() * MapLeft.GRID_SIZE) + 25);
					Robot.setY((19 - Robot.getJ()*MapLeft.GRID_SIZE) + 25);
					
					String roboti = Integer.toString(Robot.getI());
					String robotj = Integer.toString(Robot.getJ());
					MapApp.nm.sendMessage(roboti + " " + robotj + " "+ "EAST", NetworkManager.BOT_POS);
					ExplorationReal.genMapFileEvent();
					
					MapApp.panelLeft.drawing();
				}
				
				//move north, up
				else if (finalJ.get(i) < finalJ.get(i+1)){
					if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
						//MapApp.nm.sendMessage("1~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"NORTH", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
						//MapApp.nm.sendMessage("2~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnRight();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"NORTH", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
						//MapApp.nm.sendMessage("4~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft180();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"NORTH", NetworkManager.BOT_POS);
					}
					Robot.setDirection(Robot.ROBOT_DIR.NORTH);
					Sensor.SensorReal.goForwardRealSensor1(message);
					//MapApp.nm.sendMessage("0~", NetworkManager.INSTRUCTIONS);
					String botMessage;
					do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage("BOTDONE");
					}while(botMessage == null);
					Robot.setI(finalI.get(i+1));
					Robot.setJ(finalJ.get(i+1));
					Robot.setX((Robot.getI() * MapLeft.GRID_SIZE) + 25);
					Robot.setY((19 - Robot.getJ()*MapLeft.GRID_SIZE) + 25);
					////////////////////////////////////////////////////////////////////////////
					String roboti = Integer.toString(Robot.getI());
					String robotj = Integer.toString(Robot.getJ());
					MapApp.nm.sendMessage(roboti + " " + robotj + " "+ "NORTH", NetworkManager.BOT_POS);
					ExplorationReal.genMapFileEvent();
					
					MapApp.panelLeft.drawing();
				}
				
				//move south, down
				else if (finalJ.get(i) > finalJ.get(i+1)){
					if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
						//MapApp.nm.sendMessage("2~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnRight();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"SOUTH", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
						//MapApp.nm.sendMessage("1~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"SOUTH", NetworkManager.BOT_POS);
					}
					else if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
						//MapApp.nm.sendMessage("4~", NetworkManager.INSTRUCTIONS);
						String botMessage;
						do{
							ArduinoInterface.turnLeft180();
							botMessage = ArduinoInterface.returnMessage("BOTDONE");
						}while(botMessage == null);
						MapApp.nm.sendMessage(Robot.getI() + " "+ Robot.getJ() + " "+"SOUTH", NetworkManager.BOT_POS);
					}
					Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
					Sensor.SensorReal.goForwardRealSensor1(message);
					//MapApp.nm.sendMessage("0~", NetworkManager.INSTRUCTIONS);
					
					String botMessage;
					do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage("BOTDONE");
					}while(botMessage == null);
					
					Robot.setI(finalI.get(i+1));
					Robot.setJ(finalJ.get(i+1));
					Robot.setX((Robot.getI() * MapLeft.GRID_SIZE) + 25);
					Robot.setY((19 - Robot.getJ()*MapLeft.GRID_SIZE) + 25);
					////////////////////////////////////////////////////////////////////////////
					String roboti = Integer.toString(Robot.getI());
					String robotj = Integer.toString(Robot.getJ());
					MapApp.nm.sendMessage(roboti + " " + robotj + " "+ "SOUTH", NetworkManager.BOT_POS);
					ExplorationReal.genMapFileEvent();
					
					MapApp.panelLeft.drawing();
				}
			}
			
			//not sensor data
			else{
				System.out.println("Not sensor data that is received");
			}
			
		}
	}

}
