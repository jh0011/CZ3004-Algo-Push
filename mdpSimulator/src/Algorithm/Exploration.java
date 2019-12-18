package Algorithm;

/*
 * 		if (no wall in front) && (turned left previously){
			go forward;
		}
		
		else if (no wall at left){
			turn 90 deg left;
		}
		
		else if (no wall forward){
			go forward;
		}
		
		else {
			turn 90 deg right;
		}
			
			
		no wall forward = sensor reads the distance as > 10cm
		no wall at left = sensor reads the distance as > 10cm
		
		
		
		
		INSTR for arduino:
			0 - Move forward
			1 - Turn 90 deg left
			2 - Turn 90 deg right
			3 - Move backward
			4 - Turn 180 deg left
			5 - Turn 180 deg right
 * 
 */

import java.util.Timer;
import java.util.TimerTask;
import Map.*;
import Network.NetworkManager;
import Robot.*;
import Sensor.*;
public class Exploration {
	
	public static int speed = 200; //speed of robot
	public static boolean startExploration = false; //to colour the map without initialisation
	public static boolean turnedLeftPrev = false;
	public static boolean doingUnexploredFP = false;
	
	public static int coverageLimit = 10000000; 
	public static int timeLimit = 330; //5min 30sec [in sec]
	public static long startTime = 0; //timer's start time
	
	public static boolean hasReachedGoal = false;
	
	//original timer
	static Timer t = new Timer();
	static TimerTask task = new TimerTask(){
		public void run(){
			//do exploration algorithm
			int num = exploration();
			startExploration=true;
			
			if (System.currentTimeMillis() - startTime > timeLimit * 1000 || isFullyExplored()){
				//if (System.currentTimeMillis() - startTime > timeLimit * 1000){
				if (!isFullyExplored()){ //time limit is reached
					//do fastest path back to start zone / auto termination
					/*FastestPath.isExploration = true;
					FastestPath.startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
							[Robot.getX() / MapLeft.GRID_SIZE];
					FastestPath.goalNode1 = MapLeft.cellArray[18][2];
					FastestPath.goalNode2 = MapLeft.cellArray[18][1];
					FastestPath fp = new FastestPath();*/
					System.out.println("DOING FASTEST PATH HERE");
					FastestPathStepByStep.isPastWaypoint = false;
					FastestPathStepByStep.startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE][Robot.getX() / MapLeft.GRID_SIZE];
					FastestPathStepByStep.goalNode1 = MapLeft.cellArray[18][2];
					FastestPathStepByStep.goalNode2 = MapLeft.cellArray[18][1];
					try {
						FastestPathStepByStep fp = new FastestPathStepByStep();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				endTimer();
			}
			//&& hasReachedGoal
			if (hasReachedGoal && startExploration == true && ((Robot.getI()==1 && Robot.getJ()==1) ||
					(Robot.getX()==75 && Robot.getY()==925))){
				
				System.out.println("Doing fastest path");
				//check if fully explored
				while (true){
					int count = 0;
					for (int i=0; i<MapLeft.ROWS; i++){
						for (int j=0; j<MapLeft.COLS; j++){
							if (MapLeft.cellArray[i][j].getIsExplored() == false){
								count++;
								/*FastestPath.isExploration = true;
								FastestPath.isPastWaypoint = false;
								FastestPath.startNode = MapLeft.cellArray[18][2];
								FastestPath.goalNode1 = MapLeft.cellArray[i][j];
								FastestPath.goalNode2 = MapLeft.cellArray[18][2];
								FastestPath fp = new FastestPath();
								exploration();*/
								
								FastestPathStepByStep.isPastWaypoint = false;
								FastestPathStepByStep.startNode = MapLeft.cellArray[18][1];
								FastestPathStepByStep.goalNode1 = MapLeft.cellArray[i][j];
								FastestPathStepByStep.goalNode2 = MapLeft.cellArray[18][1];
								try {
									FastestPathStepByStep fp = new FastestPathStepByStep();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
							}
						}
					}
					if (count == 0){
						break;
					}
				}
				
				
			}
		}	
	};
	
	
	public static void startTimer(){
		t.scheduleAtFixedRate(task, 200, speed);
	}
	
	//timer for any time a reset occurs
	public static void newTimer() {
		t = new Timer();
		task = new TimerTask(){
			public void run(){
				//do exploration algorithm
				int num = exploration();
				startExploration=true;
				
				if (System.currentTimeMillis() - startTime > timeLimit * 1000 || isFullyExplored()){
					if (!isFullyExplored()){
						//do fastest path back to start zone
						FastestPath.isExploration = true;
						FastestPath.startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
								[Robot.getX() / MapLeft.GRID_SIZE];
						FastestPath.goalNode1 = MapLeft.cellArray[18][2];
						FastestPath.goalNode2 = MapLeft.cellArray[18][1];
						FastestPath fp = new FastestPath();
						
					}
				endTimer();
				}
				
				
			}	
		};
	}

	
	public static int exploration(){
		
		if (Robot.getI() == 13 && Robot.getJ() == 18 || Robot.getX() == 675 && Robot.getY() == 75){
			System.out.println("ENTETRED HERERERERER");
			hasReachedGoal = true;
		}
		
		//////////////////////////////////if simulator/////////////////////////////////////////
		if (MapLeft.real_bot == false){
			//set the initial robot location as explored
			int yy = Robot.getX() / MapLeft.GRID_SIZE;
			int ii = Robot.getY() / MapLeft.GRID_SIZE;
			MapLeft.cellArray[ii][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy+1].setIsExplored(true, true);
			

			//if robot is facing north
			if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
				if (Robot.sensorNorth1.getDir() == Sensor.SENSOR_DIR.NORTH){
					//if (no wall in front) && (turned right previously) go forward
					if (Robot.sensorNorth1.goForwardSensor1() == true && turnedLeftPrev == true){
						checkAutoTerminate();
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 1;
					}
					
					//else if (no wall at the left) turn 90 deg ACW
					else if (Robot.sensorWest1.goForwardSensor2() == true){
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.WEST);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.SOUTH);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = true;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 2;
					}
					
					
					//else if (no wall in front) go forward
					else if(Robot.sensorNorth1.goForwardSensor1() == true){
						checkAutoTerminate();
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 3;
					}
					
					//else (turn 90 deg clockwise)
					else{
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.EAST);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.NORTH);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 4;
					}
					
				}
			}
			
			
			//if robot is facing east
			if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
				if (Robot.sensorNorth1.getDir() == Sensor.SENSOR_DIR.EAST){
					//if (no wall in front) && (turned right previously) go forward
					if (Robot.sensorNorth1.goForwardSensor1() == true && turnedLeftPrev){
						checkAutoTerminate();
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 5;
					}
					
					//else if (no wall at the left) turn 90 deg ACW
					else if (Robot.sensorWest1.goForwardSensor2() == true){
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.NORTH);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.WEST);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = true;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 6;
					}
					
					
					//else if (no wall in front) go forward
					else if(Robot.sensorNorth1.goForwardSensor1() == true){
						checkAutoTerminate();
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 7;
					}
					
					//else (turn 90 deg clockwise)
					else{
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.SOUTH);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.EAST);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 8;
					}
					
				}
			}
			
			//if robot is facing south
			if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
				if (Robot.sensorNorth1.getDir() == Sensor.SENSOR_DIR.SOUTH){
					//if (no wall in front) && (turned right previously) go forward
					if (Robot.sensorNorth1.goForwardSensor1() == true && turnedLeftPrev){
						checkAutoTerminate();
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 9;
					}
					
					//else if (no wall at the left) turn 90 deg ACW
					else if (Robot.sensorWest1.goForwardSensor2() == true){
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.EAST);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.NORTH);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = true;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 10;
					}
					
					
					//else if (no wall in front) go forward
					else if(Robot.sensorNorth1.goForwardSensor1() == true){
						checkAutoTerminate();
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 11;
					}
					
					//else (turn 90 deg clockwise)
					else{
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.WEST);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.SOUTH);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 12;
					}
					
				}
			}
			
			//if robot is facing west
			if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
				if (Robot.sensorNorth1.getDir() == Sensor.SENSOR_DIR.WEST){
					//if (no wall in front) && (turned right previously) go forward
					if (Robot.sensorNorth1.goForwardSensor1() == true && turnedLeftPrev){
						checkAutoTerminate();
						//changed
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 13;
					}
					
					//else if (no wall at the left) turn 90 deg ACW
					else if (Robot.sensorWest1.goForwardSensor2() == true){
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.SOUTH);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.EAST);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = true;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 14;
					}
					
					
					//else if (no wall in front) go forward
					else if(Robot.sensorNorth1.goForwardSensor1() == true){
						checkAutoTerminate();
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 15;
					}
					
					//else (turn 90 deg clockwise)
					else{
						checkAutoTerminate();
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.NORTH);
						Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.WEST);
						Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
						Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
						Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
						turnedLeftPrev = false;
						genMapFileEvent();
						MapApp.panelLeft.drawing();
						return 16;
					}
					
				}
			}
			
			else{
				System.out.println("No direction set!!");
			}
			
			return 0;
		}
		
		//////////////////////////if real robot////////////////////////////////////
		else{
			//set the initial robot location as explored
			int yy = Robot.getX() / MapLeft.GRID_SIZE;
			int ii = Robot.getY() / MapLeft.GRID_SIZE;
			MapLeft.cellArray[ii][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy-1].setIsExplored(true, true);
			MapLeft.cellArray[ii][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii-1][yy+1].setIsExplored(true, true);
			MapLeft.cellArray[ii+1][yy+1].setIsExplored(true, true);
			
		//interpret sensor data with the pseudo code
		//if (System.currentTimeMillis() - startTime < timeLimit * 1000){
			String message = MapApp.nm.receiveMessage();
			String messageType = MapLeft.decodeMessageType(message);
			if (messageType==NetworkManager.SENSOR_DATA){
				if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
					if (Robot.realNorth2.goForwardRealSensor1(message) == true && turnedLeftPrev == true){
						
						//set robot location
						Robot.setI(Robot.getI() + 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 1);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ());
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 1);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI() + 1);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI() + 1);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 21;
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = true;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 2);
						Robot.realWest4.setI(Robot.realWest4.getI() - 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 2);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 2);
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 22;
					}
					
					else if (Robot.realNorth2.goForwardRealSensor1(message) == true){
						//set robot location
						Robot.setI(Robot.getI() + 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 1);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ());
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 1);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI() + 1);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI() + 1);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 23;
					}
					
					else{
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 2);
						Robot.realWest5.setI(Robot.realWest5.getI() + 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 2);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 24;
					}
				}
				
				//robot is facing east
				if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
					if (Robot.realNorth2.goForwardRealSensor1(message) == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() + 1);
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 1);
						Robot.realNorth2.setI(Robot.realNorth2.getI());
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 1);
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 1);
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 1);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 25;
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = true;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 2);
						Robot.realWest5.setI(Robot.realWest5.getI() - 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 2);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 26;
					}
					
					else if (Robot.realNorth2.goForwardRealSensor1(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() + 1);
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 1);
						Robot.realNorth2.setI(Robot.realNorth2.getI());
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 1);
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 1);
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 1);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 27;
					}
					
					else{
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 2);
						Robot.realWest4.setI(Robot.realWest4.getI() - 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 2);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 2);
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 28;
					}
				}
				
				if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
					if (Robot.realNorth2.goForwardRealSensor1(message) == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI() - 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 1);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ());
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 1);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI() - 1);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI() - 1);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 29;
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = true;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 2);
						Robot.realWest4.setI(Robot.realWest4.getI() + 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 2);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 2);
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 30;
					}
					
					else if (Robot.realNorth2.goForwardRealSensor1(message) == true){
						//set robot location
						Robot.setI(Robot.getI() - 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 1);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ());
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 1);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI() - 1);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI() - 1);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 31;
					}
					
					else{
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 2);
						Robot.realWest5.setI(Robot.realWest5.getI() - 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 2);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 32;
					}
				}
				
				if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
					if (Robot.realNorth2.goForwardRealSensor1(message) == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() - 1);
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 1);
						Robot.realNorth2.setI(Robot.realNorth2.getI());
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 1);
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 1);
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 1);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 33;
					}
					else if (Robot.realWest4.goForwardRealSensor2(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = true;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 2);
						Robot.realWest5.setI(Robot.realWest5.getI() + 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 2);
						Robot.realEast6.setJ(Robot.realEast6.getJ());
						
						//send message to arduino
						MapApp.nm.sendMessage("1", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 34;
					}
					else if (Robot.realNorth2.goForwardRealSensor1(message) == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() - 1);
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 1);
						Robot.realNorth2.setI(Robot.realNorth2.getI());
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 1);
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 1);
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 1);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						
						//send message to arduino
						MapApp.nm.sendMessage("0", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 35;
					}
					else{
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 2);
						Robot.realWest4.setI(Robot.realWest4.getI() + 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 2);
						Robot.realEast6.setI(Robot.realEast6.getI());
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 2);
						
						//send message to arduino
						MapApp.nm.sendMessage("2", NetworkManager.INSTRUCTIONS);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						genMapFileEvent(); //send MDF
						
						//repaint
						MapApp.panelLeft.drawing();
						//checkAutoTerminate();
						return 36;
					}
					
				}
				
				
			}
			//msgType != SEN_DATA
			else{
				System.out.println("Not the sensor data");
			}
		}
		return 40;
	
	}	
	
	
	
	public static void endTimer(){
		t.cancel();
		System.out.println("Timer cancelled");
	}

	
	public static void checkAutoTerminate(){
		int countExplored = 0;
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored()){
					countExplored++;
				}
			}
		}
		float percent = (float) countExplored/300;
		if ((percent * 100) >= coverageLimit){
			countExplored = 0;
			if (!isFullyExplored()){
				//do fastest path back to start zone
				FastestPath.isExploration = true;
				FastestPath.startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
						[Robot.getX() / MapLeft.GRID_SIZE];
				FastestPath.goalNode1 = MapLeft.cellArray[17][1];
				FastestPath.goalNode2 = MapLeft.cellArray[18][1];
				FastestPath fp = new FastestPath();
			}
			endTimer();
			
		}
	}
	
	public static String genMDFString(String p1){
		//change to hexadecimal
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
	
	
	public static void genMapFileEvent(){
		//String startP1 = "P1:";
		//String startP2 = " P2:";
		String p1 = genMDFString(genP1());
		String p2 = genMDFString(genP2());
		MapApp.nm.sendMessage(p1 + " " + p2, "MAP");
		
	}
	
	public static String genP1(){
		//bit 0: unexplored grid
		//bit 1: explored grid
		String begin = "11";
		String end = "11";
		String mapFile = "";
		mapFile = mapFile.concat(begin);
		for (int i=MapLeft.ROWS - 1; i>=0; i--){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored()){
					mapFile = mapFile.concat("1");
				}
				else{
					mapFile = mapFile.concat("0");
				}
			}
		}
		mapFile.concat(end);
		return mapFile;
	}
	
	public static String genP2(){
		//within the explored grids,
		//bit 0: empty grid
		//bit 1: obstacle grid
		String mapFile = "";
		for (int i=MapLeft.ROWS - 1; i>=0; i--){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored()){
					if (MapLeft.cellArray[i][j].getIsObstacle()){
						mapFile = mapFile.concat("1");
					}
					else{
						mapFile = mapFile.concat("0");
					}
				}
			}
		}
		if (mapFile.length() % 8 != 0){
			int remainder = 8 - (mapFile.length() % 8);
			for (int k=0; k<remainder; k++){ //add padding bits at the end of the string
				mapFile = mapFile.concat("0");
			}
		}
		
		return mapFile;
		
	}
	
	public static boolean isFullyExplored(){
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (!MapLeft.cellArray[i][j].getIsExplored()){
					return false;
				}
				
			}
		}
		
		if (Robot.getX()==75 && Robot.getY()==925){
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			return true;
		}
		else{
			return false;
		}
	}
	
	
}
