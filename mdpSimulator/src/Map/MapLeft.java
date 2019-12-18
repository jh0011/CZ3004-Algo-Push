package Map;

import javax.swing.*;
import Algorithm.Exploration;
import Algorithm.ExplorationReal;
import Algorithm.FastestPath;
import Network.NetworkManager;
import Robot.Robot;
import Robot.Robot.ROBOT_DIR;
import Sensor.Sensor;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MapLeft extends JPanel{
	
	public static final int ROWS = 20;
	public static final int COLS = 15;
	public static final int GRID_SIZE = 50;
	
	public static boolean real_bot = false;
	
	protected static int fontSize = 25;
	
	//to know about individual cells
	public static Cell[][] cellArray = new Cell[ROWS][COLS];
	public static boolean readFile = true;
	public static boolean hideAll = false;
	
	protected static Color colourGray = Color.DARK_GRAY;
	protected static Color colourGreen = Color.GREEN;
	protected static Color colourYellow = Color.YELLOW;
	protected static Color colourBlack = Color.BLACK;
	protected static Color colourLightgray = Color.LIGHT_GRAY;
	protected static Color colourWhite = Color.WHITE;
	protected static Color colourPink = Color.PINK;
	protected static Color colourOrange = Color.ORANGE; 
	protected static Color colourLightblue = new Color(101, 177, 243);
	protected static Color colourDarkgreen = new Color(16,94,98);
	protected static Color colourLightgreen = new Color(107,197,210);
	protected static Color colourLightestgreen = new Color(210,250,251);
	protected static Color colourMaroon = new Color(181,82,92);
	protected static Color colourPurple = new Color(112,65,109);
	
	public static String mapContentP1;
	public static String mapContentP2;
	
	public void drawing(){
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		if (real_bot == false){
		
			//read from file
			if (readFile){
			
				try{
					Path filePathP1 = Paths.get("C:/Users/dell/workspace/mdpSimulator", "P1arena.txt");
				    List<String> fileContentP1;
				    fileContentP1 = Files.readAllLines(filePathP1);
					mapContentP1 = fileContentP1.get(0).substring(2, 302); //string
					String mapFileP1 = fileContentP1.get(0).substring(0); //string
					
					Path filePathP2 = Paths.get("C:/Users/dell/workspace/mdpSimulator", "P2arena.txt");
				    List<String> fileContentP2;
				    fileContentP2 = Files.readAllLines(filePathP2);
				    int length = fileContentP2.get(0).length();
					mapContentP2 = fileContentP2.get(0).substring(0, length); //string
					
					///////////////////////RPI////////////////////////////////////////
					if (Exploration.startExploration == false){
						String map1Hex = genHex(mapFileP1); //convert string map P1 into hexa 
						String map2Hex = genHex(mapContentP2); //convert string map P2 into hexa
						String finalMessage = map1Hex + " " + map2Hex;
						MapApp.nm.sendMessage(finalMessage, "MAP");
					}
					////////////////////////////////////////////////////////////////////
					
					
				} catch (IOException e){
					e.printStackTrace();
					System.out.println("Something wrong");
				}
				
				char[][] gridArrayP1 = new char[MapLeft.ROWS][MapLeft.COLS];
				char[][] gridArray = new char[MapLeft.ROWS][MapLeft.COLS];
				
				//if exploration has not started
				if (Exploration.startExploration == false){
					if (hideAll == false){
					    int count=0;
					    for (int i=MapLeft.ROWS - 1; i>=0; i--){
					    	for (int j=0; j<MapLeft.COLS; j++){
					    		gridArrayP1[i][j] = mapContentP1.charAt(count);
					    		count++;
					    		//System.out.print(gridArray[i][j]);
					    	}
					    	//System.out.println();
					    }
					    int counter=0;
					    for (int i=MapLeft.ROWS - 1; i>=0; i--){
					    	for (int j=0; j<MapLeft.COLS; j++){
					    		if (gridArrayP1[i][j] == '1'){ //if explored
					    			if (mapContentP2.charAt(counter) == '1'){
					    				gridArray[i][j] = '1';
					    			}
					    			else{
					    				gridArray[i][j] = '0';
					    			}
					    			counter++;
					    		}
					    		else{ //if not explored
					    			gridArray[i][j] = '0';
					    		}
					    		
					    	}
					    }
					    Color colourCell = colourWhite;
					    for (int i=0; i<MapLeft.ROWS; i++){
					    	for (int j=0; j<MapLeft.COLS; j++){
					    		//empty grid
					    		if (gridArray[i][j]=='0'){
					    			colourCell = colourMaroon;
									cellArray[i][j] = new Cell(false, false, i, j);
					    		}
					    		//obstacle grid
					    		else{
					    			colourCell = colourLightgray;
									cellArray[i][j] = new Cell(false, true, i, j);
					    		}
					    		
					    		cellArray[i][j].setIsPath(false);
					    		g.setColor(colourCell);
								g.fillRect(50*j, 50*i, 50, 50);
								g.setColor(colourGray);
								g.drawRect(50*j, 50*i, 50, 50);
					    	}
					    }
					}
					
					//hideAll = true
					else{
						for (int i=0; i<ROWS; i++) {
							for (int j=0; j<COLS; j++) {
								Color colourCell = colourBlack;
								if (gridArray[i][j] == 1){
									colourCell = colourLightblue;
								}
									
								g.setColor(colourCell);
								g.fillRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
								g.setColor(colourGray);
								g.drawRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
							}
						}
	
					}
					    
					    
					
				}
				
				//if exploration has started
				else{
					if (hideAll == false){
						//load up gridArrayP1
						int counter=0;
					    for (int i=MapLeft.ROWS - 1; i>=0; i--){
					    	for (int j=0; j<MapLeft.COLS; j++){
					    		gridArrayP1[i][j] = mapContentP1.charAt(counter);
					    		counter++;
					    	}
					    }
					    
					    //load up gridArray
						int count=0;
						for (int i=MapLeft.ROWS - 1; i>=0; i--){
							for (int j=0; j<COLS; j++){
								if (gridArrayP1[i][j] == '1'){ //if explored
					    			if (mapContentP2.charAt(count) == '1'){
					    				gridArray[i][j] = '1';
					    			}
					    			else{
					    				gridArray[i][j] = '0';
					    			}
					    			count++;
					    		}
					    		else{ //if not explored
					    			gridArray[i][j] = '0';
					    		}
								
								Color colourCell;
								
								//Empty grid
								if (gridArray[i][j] == '0'){
									colourCell = colourMaroon;
								}
								
								//obstacle grid
								else {
									colourCell = colourLightgray;
								}
								
								g.setColor(colourCell);
								g.fillRect(50*j, 50*i, 50, 50);
								g.setColor(colourGray);
								g.drawRect(50*j, 50*i, 50, 50);
								
								//if fastest path before the fully explored map
								if (cellArray[i][j].getIsPath() && FastestPath.fastestPath && !FastestPath.isExploration){
									//System.out.println("Entered here purple");
									colourCell = colourPurple;
									g.setColor(colourCell);
									g.fillRect(50*j, 50*i, 50, 50);
									g.setColor(colourBlack);
									g.drawRect(50*j, 50*i, 50, 50);
								}
								
								//if really explored
								if (cellArray[i][j].getIsExplored()){
									//explored obstacle
									if (cellArray[i][j].getIsObstacle()){
										colourCell = colourBlack;
										g.setColor(colourCell);
										g.fillRect(50*j, 50*i, 50, 50);
										g.setColor(colourGray);
										g.drawRect(50*j, 50*i, 50, 50);
									}
									
									
									/*//if fastest path after the fully explored map
									else if (cellArray[i][j].getIsPath() && FastestPath.fastestPath && !FastestPath.isExploration){
										//System.out.println("Entered here pink");
										colourCell = colourPurple;
										g.setColor(colourCell);
										g.fillRect(50*j, 50*i, 50, 50);
										g.setColor(colourBlack);
										g.drawRect(50*j, 50*i, 50, 50);
									}*/
									
									//explored empty grids
									else{
										colourCell = colourLightgreen;
										g.setColor(colourCell);
										g.fillRect(50*j, 50*i, 50, 50);
										g.setColor(colourGray);
										g.drawRect(50*j, 50*i, 50, 50);
									}
								}
								
								
							}
						}
					}
					//FastestPath.isExploration = false;
					
					//hideAll = true
					else{
						for (int i=0; i<ROWS; i++){
							for (int j=0; j<COLS; j++){
								Color colourCell = colourWhite;
									
								//start and goal zones
								if (gridArray[i][j] == 1){
									colourCell = colourLightblue;
								}
									
									
								//obstacle grid
								else {
									colourCell = colourBlack;
								}
									
								g.setColor(colourCell);
								g.fillRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
								g.setColor(colourGray);
								g.drawRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
									
								if (cellArray[i][j].getIsExplored()){
									if (cellArray[i][j].getIsObstacle()){
										colourCell = colourMaroon; //colourBlack
										g.setColor(colourCell);
										g.fillRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
										g.setColor(colourGray);
										g.drawRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
									}
									else{
										
										colourCell = colourLightgreen; //colourWhite
										g.setColor(colourCell);
										g.fillRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
										g.setColor(colourGray);
										g.drawRect(GRID_SIZE*j, GRID_SIZE*i, GRID_SIZE, GRID_SIZE);
										
										
									}
										
								}
							}
						}
	
					}
					
				}
				
			}
			
			//if not readFile
			else{
				for (int m=0; m<ROWS; m++){
		            for (int n=0; n<COLS; n++) {
		            Color colourCell = colourWhite;
		            /*if (m==0&&n==14 || m==0&&n==13 || m==0&&n==12 ||
		                m==1&&n==14 || m==1&&n==13 || m==1&&n==12 ||
		                m==2&&n==14 || m==2&&n==13 || m==2&&n==12 ||
		                m==17&&n==0 || m==17&&n==1 || m==17&&n==2 ||
		                m==18&&n==0 || m==18&&n==1 || m==18&&n==2 ||
		                m==19&&n==0 || m==19&&n==1 || m==19&&n==2){
		            		colourCell = colourGreen;
		            		cellArray[m][n] = new Cell(true, false, n, m);
		            }*/
		            
		            g.setColor(colourCell);
		            g.fillRect(50*n, 50*m, 50, 50);//50
		            g.setColor(colourBlack);
		            g.drawRect(50*n, 50*m, 50, 50);//50
		            }
		        }
	
			}
		}
		
		///////////////////////////////if real robot/////////////////////////////
		else{
			//initially all unexplored and unknown
			//ExplorationReal.startExploration = false;
			if (ExplorationReal.startExploration == false){
				Color colourCell = colourWhite;
				for (int i=0; i<ROWS; i++){
		            for (int j=0; j<COLS; j++) {
		            	//initialise cell array
		            	cellArray[i][j] = new Cell(false, false, i, j);
		            	
		            	//colour the initial map
		            	//System.out.println("Colouring the map");
			            colourCell = colourWhite;
			            g.setColor(colourCell);
			            g.fillRect(50*j, 50*i, 50, 50);//50
			            g.setColor(colourBlack);
			            g.drawRect(50*j, 50*i, 50, 50);//50
		            }
		        }
			    
			}
			
			//ExplorationReal.startExplortion == true
			else{
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						Color colourCell;
						colourCell = colourWhite;
						g.setColor(colourCell);
						g.fillRect(50*j, 50*i, 50, 50);
						g.setColor(colourBlack);
						g.drawRect(50*j, 50*i, 50, 50);
						
						if (MapLeft.cellArray[i][j].getIsExplored() == true){
							
							//explored obstacle 
							if (MapLeft.cellArray[i][j].getIsObstacle()){
								if (MapLeft.cellArray[i][j].getIsPhysicallyVisited() == true){
									MapLeft.cellArray[i][j].setIsObstacle(false);
									colourCell = colourLightgreen;
									g.setColor(colourCell);
									g.fillRect(50*j, 50*i, 50, 50);
									g.setColor(colourGray);
									g.drawRect(50*j, 50*i, 50, 50);
								}
								else{
									colourCell = colourBlack;
									g.setColor(colourCell);
									g.fillRect(50*j, 50*i, 50, 50);
									g.setColor(colourGray);
									g.drawRect(50*j, 50*i, 50, 50);
									
								}
								
							}
							//explored empty grids
							else{
								colourCell = colourLightgreen;
								g.setColor(colourCell);
								g.fillRect(50*j, 50*i, 50, 50);
								g.setColor(colourGray);
								g.drawRect(50*j, 50*i, 50, 50);
							}
						}
					}
				}
			}
		}
		
		//////////////////////////////////PAINT WAYPOINT///////////////////////////////////////////////
		//if simulator
		if (real_bot==false){
			//if waypoint counter is > 0, paint the waypoint in
			if (BtnRight.waypointCount > 0){
				g.setColor(Color.MAGENTA);
				g.fillRect(BtnRight.corX * GRID_SIZE, (ROWS - BtnRight.corY - 1) * GRID_SIZE, GRID_SIZE, GRID_SIZE);
				g.setColor(colourBlack);
				g.drawRect(BtnRight.corX * GRID_SIZE, (ROWS - BtnRight.corY - 1) * GRID_SIZE, GRID_SIZE, GRID_SIZE);
			}
		}
		
		//if real robot
		else{
			if (BtnRight.waypointCount>0){
		    	g.setColor(Color.MAGENTA);
				g.fillRect(BtnRight.corX * GRID_SIZE, (ROWS - BtnRight.corY - 1) * GRID_SIZE, GRID_SIZE, GRID_SIZE);
				g.setColor(colourBlack);
				g.drawRect(BtnRight.corX * GRID_SIZE, (ROWS - BtnRight.corY - 1) * GRID_SIZE, GRID_SIZE, GRID_SIZE);
		    }
		}
		
	
		
		//Robot virtual contour
		g.setColor(MapLeft.colourDarkgreen);
		g.fillRect(Robot.getX() - 75, Robot.getY() - 75, Robot.ROBOT_CONTOUR, Robot.ROBOT_CONTOUR);
		g.setColor(MapLeft.colourBlack);
		for (int i=0; i<3; i++){
			for (int j=0; j<3; j++){
				g.drawRect((i*50) + (Robot.getX() - 75), (j*50) + (Robot.getY() - 75), MapLeft.GRID_SIZE, MapLeft.GRID_SIZE);
			}
		}
		
		//Circular robot
		g.setColor(MapLeft.colourLightgreen);
		g.fillOval(Robot.getX() - 50, Robot.getY() - 50, Robot.ROBOT_SIZE, Robot.ROBOT_SIZE);
		
		
		//Robot's direction pointer
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			g.setColor(Color.RED);
			g.fillOval(Robot.getX() - 10, Robot.getY() - 50, Robot.ROBOT_DIR_SIZE, Robot.ROBOT_DIR_SIZE);
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			g.setColor(Color.RED);
			g.fillOval(Robot.getX() + 25, Robot.getY() - 10, Robot.ROBOT_DIR_SIZE, Robot.ROBOT_DIR_SIZE);
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			g.setColor(Color.RED);
			g.fillOval(Robot.getX() - 10, Robot.getY() + 25, Robot.ROBOT_DIR_SIZE, Robot.ROBOT_DIR_SIZE);
		}
		else{
			g.setColor(Color.RED);
			g.fillOval(Robot.getX() - 50, Robot.getY() - 10, Robot.ROBOT_DIR_SIZE, Robot.ROBOT_DIR_SIZE);
		}
	
	}
	
	public static void setReadFile(boolean read){
		readFile = read;
	}
	
	public static String genHex(String p1){
		//p1 hexadecimal
		int sum=0;
		String result = "";
		String finalResult = "";
		for (int k=0; k<p1.length(); k=k+4){
			if (k >=p1.length() || k+1 >=p1.length() || k+2 >=p1.length() || k+3 >=p1.length()){
				break;
			}
			if (p1.charAt(k)=='1'){
				sum+=8;
			}
			if (p1.charAt(k+1)=='1'){
				sum+=4;
			}
			if (p1.charAt(k+2)=='1'){
				sum+=2;
			}
			if (p1.charAt(k+3)=='1'){
				sum+=1;
			}
			
			result = Integer.toHexString(sum); //convert to hexadecimal
			finalResult = finalResult.concat(result);
			sum=0; //set the sum to zero for the next iteration
		}
		return finalResult;
	}
	
	public static String decodeMessageType(String message){ //no 1 char header here //PREFIX DATA1 DATA2
		String[] messageArray = message.split(" "); //split the message into components
		String messageType = messageArray[0];
		return messageType;		
	}
	
	
	//for coordinates in int array
	public static int[] decodeIndex(String message){ //PREFIX DATA1 DATA2
		String[] locArray = message.split(" ");
		int iVal = Integer.parseInt(locArray[1]);
		int jVal = Integer.parseInt(locArray[2]);
		int[] locationArray = {iVal, jVal};
		return locationArray;
	}
	
	public static ROBOT_DIR getDirectionBot(String message){ //PREFIX DATA1 DATA2 DIRECTION
		String[] messageArray = message.split(" "); //split the message into components
		String direction = messageArray[3]; //direction appended with location
		if (direction.toUpperCase().equals("UP")){
			return Robot.ROBOT_DIR.NORTH;
		}
		else if (direction.toUpperCase().equals("RIGHT")){
			return Robot.ROBOT_DIR.EAST;
		}
		else if (direction.toUpperCase().equals("DOWN")){
			return Robot.ROBOT_DIR.SOUTH;
		}
		else if (direction.toUpperCase().equals("LEFT")){
			return Robot.ROBOT_DIR.WEST;
		}
		else{
			System.out.println("Invalid direction set.");
			return Robot.ROBOT_DIR.NORTH;
		}
	}
	
	public static double[] getSensorData(String message){
		String[] messageArray = message.split(" ");
		double[] dataArray = new double[5];
		for (int i=1; i<6; i++){
			double dataVal = Double.parseDouble(messageArray[i]);
			dataArray[i - 1] = dataVal;
		}
		return dataArray;
	}

}
