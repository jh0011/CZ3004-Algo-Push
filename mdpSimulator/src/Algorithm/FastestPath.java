package Algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import Map.BtnRight;
import Map.Cell;
import Map.MapApp;
import Map.MapLeft;
import Network.NetworkManager;
import Robot.Robot;
import Sensor.SensorReal;

public class FastestPath {
	
	protected ArrayList<Cell> nodeList1 = new ArrayList<Cell>();
	protected ArrayList<Cell> expandedList1 = new ArrayList<Cell>();
	protected ArrayList<Cell> exploredList1 = new ArrayList<Cell>();
	
	protected ArrayList<Cell> nodeList2 = new ArrayList<Cell>();
	protected ArrayList<Cell> expandedList2 = new ArrayList<Cell>();
	protected ArrayList<Cell> exploredList2 = new ArrayList<Cell>();
	
	protected ArrayList<Integer> iValues = new ArrayList<Integer>();
	protected ArrayList<Integer> jValues = new ArrayList<Integer>();
	
	protected int straightDist = 50;
	protected int turningDist =  90;
	
	public static Cell goalNode1 = MapLeft.cellArray[MapLeft.ROWS - BtnRight.corY - 1][BtnRight.corX]; //waypoint
	public static Cell goalNode2 = MapLeft.cellArray[1][13]; //goal zone
	public static Cell startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
			[Robot.getX() / MapLeft.GRID_SIZE]; //starting point within start zone
	
	protected static boolean isPastWaypoint = false; 
	protected static int indexTraverse = -1; 
	protected static int countIndex;
	public static boolean fastestPath = false; //colour map for fastest path route
	public static boolean isExploration = false; //return to start zone after manual termination
	
	
	ArrayList<Integer> indexArray = new ArrayList<Integer>();
	ArrayList<Integer> indexArray2 = new ArrayList<Integer>();
	
	
	Timer t = new Timer();
	Timer t2 = new Timer();
	TimerTask task = new TimerTask(){
		public void run(){
			//print the path out
			indexTraverse++;
			
			printPath();
			
			
			if (indexTraverse >= countIndex - 1){
				t.cancel();
				//if (Exploration.doingUnexploredFP == false){
					startNextPath();
				//}
				
			}
		}	
	};
	
	TimerTask task2 = new TimerTask(){
		public void run(){
			//print the path out
			indexTraverse++;
			printPath();

		}	
	};
	
	
	public void startTimer(){
		t.scheduleAtFixedRate(task, 200, Exploration.speed);
	}
	
	public void startTimer2(){
		t2.scheduleAtFixedRate(task2, 200, Exploration.speed);
	}
	
	
	
	public FastestPath(){
		//fastest path from start zone to goal zone
		if (isExploration == false){
			goalNode1 = MapLeft.cellArray[MapLeft.ROWS - BtnRight.corY - 1][BtnRight.corX];
			goalNode2 = MapLeft.cellArray[1][13];
			startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
					[Robot.getX() / MapLeft.GRID_SIZE]; //starting point within start zone
		}

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
			indexTraverse = -1;
			startTimer();
			
		}
	
		
	}
	
	public void startNextPath(){
		for ( int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				MapLeft.cellArray[i][j].setParent(null);
			}
		}
		isPastWaypoint = true;
		initNodes();
		initGVal();
		boolean isFound2 = findPath();
		if (isFound2){
			System.out.println("Path to goal zone has been found");
			countIndex = calculateNumNodes();
			indexTraverse = -1;
			startTimer2();
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
			int i = Robot.getY() / MapLeft.GRID_SIZE;
			int j = Robot.getX() / MapLeft.GRID_SIZE;
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
			int i = goalNode1.getX();
			int j = goalNode1.getY();
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
		else{
			
		}
	}
	
	public boolean haveNorth(int i, int j){
		if (i-2 < 0){
			return false;
		}
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
		Cell tempCell = expandedList1.get(0);
		if (!isPastWaypoint){
			/*if (expandedList1.contains(goalNode1)){
				System.out.println("Goal node 1 is in expanded list");
			}
			else{
				System.out.println("Goal node 1 not within expanded list");
			}*/
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
			tempCell = expandedList2.get(0);
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
		
		if (!isPastWaypoint){
			if(MapLeft.real_bot==false) {
				int newIndex = indexArray.get(indexTraverse);
				int x = exploredList1.get(newIndex).getX();
				int y = exploredList1.get(newIndex).getY();
				int result = checkNextDir(indexTraverse);
				Robot.setX((y * MapLeft.GRID_SIZE) + 25);
				Robot.setY((x * MapLeft.GRID_SIZE) + 25);
			}
			else {
				int newIndex = indexArray.get(indexTraverse);
				int x = exploredList1.get(newIndex).getX();
				int y = exploredList1.get(newIndex).getY();
				int result = checkNextDir(indexTraverse);
				Robot.setX((y * MapLeft.GRID_SIZE) + 25);
				Robot.setY((x * MapLeft.GRID_SIZE) + 25);
				
				Robot.setI(19-x);//19-x
				Robot.setJ(y);//y
				String newI = Integer.toString(Robot.getI());
				String newJ = Integer.toString(Robot.getJ());
				Robot.ROBOT_DIR dir = Robot.getDirection();
				String loc = newI + " " + newJ + " " + dir;
				
				//send message to arduino
				MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
				
				//send message to android
				MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
				
				
			}
			
		}
		
		//is past waypoint/goalNode1
		else{
			if (indexTraverse < countIndex){
				if(MapLeft.real_bot==false) {
					int newIndex = indexArray2.get(indexTraverse);
					int x = exploredList2.get(newIndex).getX();
					int y = exploredList2.get(newIndex).getY();
					int result = checkNextDir(indexTraverse);
					
					Robot.setX((y * MapLeft.GRID_SIZE) +25); //send to rpi
					Robot.setY((x * MapLeft.GRID_SIZE) +25); //send to rpi
				}
				else {
					int newIndex = indexArray2.get(indexTraverse);
					int x = exploredList2.get(newIndex).getX();
					int y = exploredList2.get(newIndex).getY();
					int result = checkNextDir(indexTraverse);
					
					Robot.setX((y * MapLeft.GRID_SIZE) +25); //send to rpi
					Robot.setY((x * MapLeft.GRID_SIZE) +25); //send to rpi
					
					Robot.setI(19-x);
					Robot.setJ(y);
					String newI = Integer.toString(Robot.getI());
					String newJ = Integer.toString(Robot.getJ());
					Robot.ROBOT_DIR dir = Robot.getDirection();
					String loc = newI + " " + newJ + " " + dir;
					
					//send message to arduino
					MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
					//send message to android
					MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
				}
				
			}
			else{
				if(MapLeft.real_bot==false) {
					t2.cancel();
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
				else {
					t2.cancel();
					
					//move east
					if (Robot.getX() == 625 && Robot.getY() == 75){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() + 1);
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					//move north
					else if (Robot.getX() == 675 && Robot.getY() == 125){
						//set robot location
						Robot.setI(Robot.getI() + 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					//move west
					else if (Robot.getX()==125 && Robot.getY()==925){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() - 1);
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);

						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					//move south
					else if(Robot.getX()==75 && Robot.getY()==875){
						//set robot location
						Robot.setI(Robot.getI() - 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);

						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
				}
				
				
			}
			
		}
		
		
		int yy = Robot.getX() / MapLeft.GRID_SIZE; //i (yy) [send to arduino]
		int ii = Robot.getY() / MapLeft.GRID_SIZE; //j (19 - ii) [send to arduino]

		///////////////////////////////////////////////////////////////////////////////////////////////////
		iValues.add(yy);
		jValues.add(19 - ii);
		System.out.println("iValue: "+iValues.get(indexTraverse) + " jValue: "+jValues.get(indexTraverse));
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		//colour the path that the robot is moving
		Exploration.startExploration = true;
		fastestPath = true;
		if (MapLeft.real_bot == false){
			/*MapLeft.cellArray[ii][yy].setIsPath(true);
			MapLeft.cellArray[ii-1][yy].setIsPath(true);
			MapLeft.cellArray[ii+1][yy].setIsPath(true);
			MapLeft.cellArray[ii][yy-1].setIsPath(true);
			MapLeft.cellArray[ii-1][yy-1].setIsPath(true);
			MapLeft.cellArray[ii+1][yy-1].setIsPath(true);
			MapLeft.cellArray[ii][yy+1].setIsPath(true);
			MapLeft.cellArray[ii-1][yy+1].setIsPath(true);
			MapLeft.cellArray[ii+1][yy+1].setIsPath(true);*/
			
			MapLeft.cellArray[ii][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy+1].setIsExplored(true, true);
			
			//Robot.setSensorLocations(yy, 19 - ii);
			//Sensor.Sensor.goForwardSensor1();
			
	
			MapApp.panelLeft.drawing();	
		}
		
		//real robot
		else if (MapLeft.real_bot == true && ExplorationReal.startExploration == true){
			while (true){
				String message = MapApp.nm.receiveMessage();
				String messageType = MapLeft.decodeMessageType(message);
				if (messageType.equals(NetworkManager.SENSOR_DATA)){
					Sensor.SensorReal.goForwardRealSensor1(message);
					MapApp.panelLeft.drawing();
					ExplorationReal.genMapFileEvent();
				}
				else{
					break;
				}
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
	
	public int checkNextDir(int index){
		if(MapLeft.real_bot == false) {
			if (!isPastWaypoint){
				//out of range
				if (index >= countIndex - 1){
					Robot.setDirection(Robot.ROBOT_DIR.EAST);
					return 0;
				}
				
				int newIndex = index + 1;
				Cell currentCell = exploredList1.get(indexArray.get(index));
				Cell newCell = exploredList1.get(indexArray.get(newIndex));
				
				//north
				if ((currentCell.getX() > newCell.getX()) && (currentCell.getY() == newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.NORTH);
					return 1;
				}
				
				//south
				else if ((currentCell.getX() < newCell.getX()) && (currentCell.getY() == newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
					return 2;
				}
				
				//east
				else if ((currentCell.getX() == newCell.getX()) && (currentCell.getY() < newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.EAST);
					return 3;
				}
				
				//west
				else{
					Robot.setDirection(Robot.ROBOT_DIR.WEST);
					return 4;
				}
			}
			
			else{
				//out of range
				if (index >= countIndex - 1){
					Robot.setDirection(Robot.ROBOT_DIR.EAST);
					return 0;
				}
				
				int newIndex = index + 1;
				Cell currentCell = exploredList2.get(indexArray2.get(index));
				Cell newCell = exploredList2.get(indexArray2.get(newIndex));
				
				//north
				if ((currentCell.getX() > newCell.getX()) && (currentCell.getY() == newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.NORTH);
					return 1;
				}
				
				//south
				else if ((currentCell.getX() < newCell.getX()) && (currentCell.getY() == newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
					return 2;
				}
				
				//east
				else if ((currentCell.getX() == newCell.getX()) && (currentCell.getY() < newCell.getY())){
					Robot.setDirection(Robot.ROBOT_DIR.EAST);
					return 3;
				}
				
				//west
				else{
					Robot.setDirection(Robot.ROBOT_DIR.WEST);
					return 4;
				}
			}
		}
		//if real robot
		else {
			if (!isPastWaypoint){
				//out of range
				//Robot.setDirection(Robot.ROBOT_DIR.EAST);
				if (index >= countIndex - 1){
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						
					}
					
					return 0;
				}
				
				int newIndex = index + 1;
				Cell currentCell = exploredList1.get(indexArray.get(index));
				Cell newCell = exploredList1.get(indexArray.get(newIndex));
				
				//north
				if ((currentCell.getX() > newCell.getX()) && (currentCell.getY() == newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.NORTH);
					if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST){
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 1;
				}
				
				//south
				else if ((currentCell.getX() < newCell.getX()) && (currentCell.getY() == newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", "NetworkManager.INSTRUCTIONS");
						//send message to android
						MapApp.nm.sendMessage(loc, "NetworkManager.BOT_POS"); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 2;
				}
				
				//east
				else if ((currentCell.getX() == newCell.getX()) && (currentCell.getY() < newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.EAST);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 3;
				}
				
				//west
				else{
					//Robot.setDirection(Robot.ROBOT_DIR.WEST);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 4;
				}
				
			}
			else {
				//out of range
				//Robot.setDirection(Robot.ROBOT_DIR.EAST);
				if (index >= countIndex - 1){
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						
					}
					
					return 0;
				}
				
				int newIndex = index + 1;
				Cell currentCell = exploredList2.get(indexArray2.get(index));
				Cell newCell = exploredList2.get(indexArray2.get(newIndex));
				
				//north
				if ((currentCell.getX() > newCell.getX()) && (currentCell.getY() == newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.NORTH);
					if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST){
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 1;
				}
				
				//south
				else if ((currentCell.getX() < newCell.getX()) && (currentCell.getY() == newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 2;
				}
				
				//east
				else if ((currentCell.getX() == newCell.getX()) && (currentCell.getY() < newCell.getY())){
					//Robot.setDirection(Robot.ROBOT_DIR.EAST);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST) {
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 3;
				}
				
				//west
				else{
					//Robot.setDirection(Robot.ROBOT_DIR.WEST);
					if(Robot.getDirection() == Robot.ROBOT_DIR.NORTH) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("4", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH) {
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
					}
					return 4;
				}
			}
		}
		
	}
	

	


}
