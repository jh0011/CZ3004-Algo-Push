package Map;

import java.awt.Color;

import java.awt.GridLayout;

import javax.swing.*;

import Algorithm.ExplorationReal;
import Algorithm.FPReal;
import Algorithm.FastestPathStepByStep;
import Interface.AndroidInterface;
import Interface.ArduinoInterface;
import Network.NetworkManager;
import Robot.Robot;



public class MapApp {
	
	//instantiate the JFrame
	public static JFrame jf = new JFrame("Algo Simulator");
	
	public static boolean isRunning = true;
	
	//instantiate the JPanel (panelLeft)
	public static MapLeft panelLeft = new MapLeft();
	public static NetworkManager nm = new NetworkManager();

	public static void main(String[] args) throws InterruptedException {
		jf.setSize(1170, 1080); //width by height //cell is 50x50
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//set up connection with rpi
		nm.openConnection();
		
		//draw the panelLeft
		panelLeft.drawing();
				
				
		//add in the panelRight
		BtnRight panelRight = new BtnRight();
		panelRight.addButtons();
		panelRight.setLayout(new GridLayout(20, 1, 3, 3));
		panelRight.add(BtnRight.loadMap);
		panelRight.add(BtnRight.exportMap);
		panelRight.add(BtnRight.uncoverAll);
		panelRight.add(BtnRight.hideAll);
		panelRight.add(BtnRight.rotateRobot);
		panelRight.add(BtnRight.exploration);
		panelRight.add(BtnRight.fastestPath);
		panelRight.add(BtnRight.manualMove);
		panelRight.add(BtnRight.genMapFile);
		panelRight.add(BtnRight.reset);
		panelRight.add(BtnRight.manualTerminate);
		panelRight.add(BtnRight.userSpeed);
		panelRight.add(BtnRight.userCoverage);
		panelRight.add(BtnRight.userTime);
		panelRight.add(BtnRight.waypointLocation);
		panelRight.add(BtnRight.robotLocation);
		
		
		
		//add a splitpane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeft, panelRight);
		splitPane.setDividerLocation(750);
		
		//add split pane to frame
		jf.add(splitPane);
		jf.setVisible(true);
		
		
		if (MapLeft.real_bot == true){
			while (isRunning){
				String message = AndroidInterface.returnMessage();
				//String message = "EXPLORATION";
				if (message!=null){
					String messageType = MapLeft.decodeMessageType(message);
					
					if (messageType.equals(NetworkManager.EX_START) || messageType.equals(NetworkManager.FP_START)
							|| messageType.equals(NetworkManager.BOT_LOC) || messageType.equals(NetworkManager.WP_INDEX)){
						
						
						if (messageType.equals(NetworkManager.EX_START)){
							//nm.sendMessage("~", NetworkManager.BOT_START);
							//ArduinoInterface.checkReady();
							
							String botMessage; 
							do{
								ArduinoInterface.checkReady();
								botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_READY);
							}while(botMessage == null);
							
							Thread.sleep(100);
							
							
							do{
								ArduinoInterface.returnSensorData();
								botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
							}while(botMessage == null);
							
							message = botMessage;
							
							
							ExplorationReal.startExploration = true;
							ExplorationReal exp = new ExplorationReal();
							ExplorationReal.startTime = System.currentTimeMillis();
							ExplorationReal.exploration(message); 
						}
						
						/////////////////////NEED TO CHANGE THIS////////////////////
						else if (messageType.equals(NetworkManager.FP_START)){
							ExplorationReal.startExploration = false;
							String botMessage; 
							
							
							nm.sendMessage("14~", NetworkManager.INSTRUCTIONS);
							Thread.sleep(500);
							ExplorationReal.startExploration = true;
							FPReal fp = new FPReal();
						}
						//////////////////////////////////////////////////////////////
						
						
						else if (messageType.equals(NetworkManager.WP_INDEX)){
							int[] cellIndex = MapLeft.decodeIndex(message);
							BtnRight.corX = cellIndex[0]; //horizontal from bottom left
							BtnRight.corY = cellIndex[1]; //vertical from bottom left
							int yy = cellIndex[0];
							int ii = 19 - cellIndex[1];
							MapLeft.cellArray[ii-1][yy].setIsObstacle(false);
							MapLeft.cellArray[ii+1][yy].setIsObstacle(false);
							MapLeft.cellArray[ii][yy-1].setIsObstacle(false);
							MapLeft.cellArray[ii-1][yy-1].setIsObstacle(false);
							MapLeft.cellArray[ii+1][yy-1].setIsObstacle(false);
							MapLeft.cellArray[ii][yy+1].setIsObstacle(false);
							MapLeft.cellArray[ii-1][yy+1].setIsObstacle(false);
							MapLeft.cellArray[ii+1][yy+1].setIsObstacle(false);
							
							BtnRight.waypointCount = 1;
							panelLeft.drawing();
						}
						
						else if (messageType.equals(NetworkManager.BOT_LOC)){
							int[] cellIndex = MapLeft.decodeIndex(message);
							System.out.println("CellIndex[0]: "+cellIndex[0]);
							System.out.println("Cellindex[1]: "+cellIndex[1]);
							
							//System.out.println("Robot location is setting");
							//set robot location
					    	
					    	Robot.setX((cellIndex[0] * MapLeft.GRID_SIZE) + 25);
					    	Robot.setY(((MapLeft.ROWS - cellIndex[1]) * MapLeft.GRID_SIZE) - 25);
					    	Robot.INIT_BOT_X = (cellIndex[0] * MapLeft.GRID_SIZE) + 25;
					    	Robot.INIT_BOT_Y = ((MapLeft.ROWS - cellIndex[1]) * MapLeft.GRID_SIZE) - 25;
					    	Robot.setI(cellIndex[0]);
					    	Robot.setJ(cellIndex[1]);
					    	
					    	
					    	Robot.setDirection(MapLeft.getDirectionBot(message));
					    	
					    	
					    	//set sensor locations
					    	if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
						    	Robot.realNorth1.setI(cellIndex[0] - 1);
						    	Robot.realNorth1.setJ(cellIndex[1] + 1);
								Robot.realNorth2.setI(cellIndex[0]);
								Robot.realNorth2.setJ(cellIndex[1] + 1);
								Robot.realNorth3.setI(cellIndex[0] + 1);
								Robot.realNorth3.setJ(cellIndex[1] + 1);
								Robot.realWest4.setI(cellIndex[0] - 1);
								Robot.realWest4.setJ(cellIndex[1] + 1);
								Robot.realWest5.setI(cellIndex[0] - 1);
								Robot.realWest5.setJ(cellIndex[1] - 1);
								Robot.realEast6.setI(cellIndex[0] - 1);
								Robot.realEast6.setJ(cellIndex[1]);
					    	}
							
							
					    	else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
								Robot.realNorth1.setI(cellIndex[0] + 1);
						    	Robot.realNorth1.setJ(cellIndex[1] + 1);
								Robot.realNorth2.setI(cellIndex[0] + 1);
								Robot.realNorth2.setJ(cellIndex[1]);
								Robot.realNorth3.setI(cellIndex[0] + 1);
								Robot.realNorth3.setJ(cellIndex[1] - 1);
								Robot.realWest4.setI(cellIndex[0] + 1);
								Robot.realWest4.setJ(cellIndex[1] + 1);
								Robot.realWest5.setI(cellIndex[0] - 1);
								Robot.realWest5.setJ(cellIndex[1] + 1);
								Robot.realEast6.setI(cellIndex[0]);
								Robot.realEast6.setJ(cellIndex[1] + 1);
					    	}
							
							
					    	else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
					    		Robot.realNorth1.setI(cellIndex[0] + 1);
						    	Robot.realNorth1.setJ(cellIndex[1] - 1);
								Robot.realNorth2.setI(cellIndex[0]);
								Robot.realNorth2.setJ(cellIndex[1] - 1);
								Robot.realNorth3.setI(cellIndex[0] - 1);
								Robot.realNorth3.setJ(cellIndex[1] - 1);
								Robot.realWest4.setI(cellIndex[0] + 1);
								Robot.realWest4.setJ(cellIndex[1] - 1);
								Robot.realWest5.setI(cellIndex[0] + 1);
								Robot.realWest5.setJ(cellIndex[1] + 1);
								Robot.realEast6.setI(cellIndex[0] + 1);
								Robot.realEast6.setJ(cellIndex[1]);
					    	}
							
							
					    	else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
								Robot.realNorth1.setI(cellIndex[0] - 1);
						    	Robot.realNorth1.setJ(cellIndex[1] - 1);
								Robot.realNorth2.setI(cellIndex[0] - 1);
								Robot.realNorth2.setJ(cellIndex[1]);
								Robot.realNorth3.setI(cellIndex[0] - 1);
								Robot.realNorth3.setJ(cellIndex[1] + 1);
								Robot.realWest4.setI(cellIndex[0] - 1);
								Robot.realWest4.setJ(cellIndex[1] - 1);
								Robot.realWest5.setI(cellIndex[0] + 1);
								Robot.realWest5.setJ(cellIndex[1] - 1);
								Robot.realEast6.setI(cellIndex[0]);
								Robot.realEast6.setJ(cellIndex[1] - 1);
					    	}
							
							//repaint
							panelLeft.drawing();
						}
						
						
						
						else{
							System.out.println("Some other irrelevant message was sent");
							isRunning = true;
						}
						
					}
					
					//message from arduino
					else{
						System.out.println("NOT A MESSAGE FROM ANDROID");
					}
					
				}
				
				
			}
		}
		
		
	}
	


}
