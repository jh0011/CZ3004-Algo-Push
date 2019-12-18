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

import Interface.ArduinoInterface;
import Map.*;
import Network.NetworkManager;
import Robot.*;
import Sensor.*;
public class ExplorationReal {
	
	public static boolean startExploration = false; //to colour the map without initialisation
	public static boolean turnedLeftPrev = false;
	public static boolean hasReachedGoal = false;
	public static int coverageLimit = 100; 
	public static int timeLimit = 10000; //5min 30sec = 330sec
	public static long startTime = 0; //timer's start time
	
	protected static int timeSleep = 0; //time delay to send to arduino
	protected static int offsetTimeSleep = 0; //timeSleep - offsetTimeSleep => for repainting
	protected static int timeSleepAndroid = 0;
	protected static int TimeSleepSensor = 0; //0
	
	protected static boolean listening = false;
	
	protected static int calibrateCounter = 2;
	
	protected static int leftTurnCount = 0;
	
	
	public static void exploration(String message) throws InterruptedException{
		startExploration = true;
		
		while (message!=null){
			
			calibrateCounter++;
		//////////////////////////////////////if real robot///////////////////////////////////////////
	
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
			
			MapLeft.cellArray[ii][yy].setIsObstacle(false);
			MapLeft.cellArray[ii-1][yy].setIsObstacle(false);
			MapLeft.cellArray[ii+1][yy].setIsObstacle(false);
			MapLeft.cellArray[ii][yy-1].setIsObstacle(false);
			MapLeft.cellArray[ii-1][yy-1].setIsObstacle(false);
			MapLeft.cellArray[ii+1][yy-1].setIsObstacle(false);
			MapLeft.cellArray[ii][yy+1].setIsObstacle(false);
			MapLeft.cellArray[ii-1][yy+1].setIsObstacle(false);
			MapLeft.cellArray[ii+1][yy+1].setIsObstacle(false);
			
			MapLeft.cellArray[ii][yy].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii-1][yy].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii+1][yy].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii][yy-1].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii-1][yy-1].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii+1][yy-1].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii][yy+1].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii-1][yy+1].setIsPhysicallyVisited(true);
			MapLeft.cellArray[ii+1][yy+1].setIsPhysicallyVisited(true);
			
			
			if (Robot.getI() == 13 && Robot.getJ() == 18){
				hasReachedGoal = true;
			}
			
			String botMessage3;
			do{
				ArduinoInterface.returnSensorData();
				botMessage3 = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
				}while(botMessage3 == null);
			
			String messageType = MapLeft.decodeMessageType(botMessage3); //message
			
			if (messageType.equals(NetworkManager.SENSOR_DATA)){
				SensorReal.dataValues = SensorReal.getSensorValues(botMessage3); //update the static data values
				
				//once the sensor data is received, calibration should be checked
				String messageSENDATA;
				
				if (calibrateCounter >= 3){
					//messageSENDATA = checkCalibrate(); //when successful, try with a void function instead
					if(checkCalibrate2())
						calibrateCounter = 0;
				}
				
				else{
					messageSENDATA = botMessage3;
				}
				
				messageSENDATA = botMessage3;
				
				
				//messageSENDATA = 
				checkFrontCalibrate(messageSENDATA);
				
				//step calibration
				String botMessage4; 
				do{
				ArduinoInterface.calibrateStep();
				botMessage4 = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage4 == null);
				
				
				renewMDF();
				
				SensorReal.dataValues = SensorReal.getSensorValues(messageSENDATA);
				
				//facing north
				if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
					boolean sensor1Forward = Robot.realNorth2.goForwardRealSensor1(messageSENDATA);
					if (sensor1Forward == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() + 1);
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						System.out.println("I: "+Robot.getI() + " J: "+ Robot.getJ());
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
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
						
						MapApp.panelLeft.drawing();
						
						//send message to arduino
						Thread.sleep(TimeSleepSensor);
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						
						if (checkAutoTerminate() == true){
							
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 21;
						
						
						
						
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(messageSENDATA) == true && (hasReachedGoal || leftTurnCount <= 4)){
						
						leftTurnCount++;
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						System.out.println("I: "+Robot.getI() + " J: "+ Robot.getJ());
						turnedLeftPrev = true;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
						//set sensor locations
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 2);
						Robot.realWest5.setI(Robot.realWest5.getI() + 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						
						MapApp.panelLeft.drawing();
						
						//send message to arduino
						Thread.sleep(TimeSleepSensor);
						String botMessage; 
						do{
						ArduinoInterface.turnLeft();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//Thread.sleep(timeSleep);
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
					
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						//MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 22;
					}
					
					else if (sensor1Forward == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() + 1);
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
						System.out.println("I: "+Robot.getI() + " J: "+ Robot.getJ());
						Robot.setDirection(Robot.ROBOT_DIR.NORTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "NORTH";
						
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
						
						MapApp.panelLeft.drawing();
						
						//send message to arduino
						Thread.sleep(TimeSleepSensor);
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE); //confirmation message
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						//MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 23;
					}
					
					else{
						leftTurnCount = 0;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 2);
						Robot.realWest4.setI(Robot.realWest4.getI() + 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 2);
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						
						MapApp.panelLeft.drawing();
						
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnRight();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						Thread.sleep(timeSleep - offsetTimeSleep);
						
						//repaint
						//MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 24;
					}
				}
				
				//robot is facing east
				else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
					boolean sensor1Forward = Robot.realNorth2.goForwardRealSensor1(messageSENDATA);
					if (sensor1Forward == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI() + 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 25;
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(messageSENDATA) == true && (!hasReachedGoal||leftTurnCount<=4)){
						
						leftTurnCount++;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 2);
						Robot.realWest4.setI(Robot.realWest4.getI() - 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 2);
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnLeft();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 26;
					}
					
					else if (sensor1Forward == true){
						//set robot location
						Robot.setI(Robot.getI() + 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.EAST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "EAST";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 27;
					}
					
					else{
						leftTurnCount = 0;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() - 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() - 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() - 2);
						Robot.realWest5.setI(Robot.realWest5.getI() + 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnRight();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 28;
					}
				}
				
				//facing south
				else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
					boolean sensor1Forward = Robot.realNorth2.goForwardRealSensor1(messageSENDATA);
					if (sensor1Forward == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() - 1);
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 29;
					}
					
					else if (Robot.realWest4.goForwardRealSensor2(messageSENDATA) == true && (!hasReachedGoal || leftTurnCount <= 4)){
						leftTurnCount++;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 2);
						Robot.realWest5.setI(Robot.realWest5.getI() - 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnLeft();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 30;
					}
					
					else if (sensor1Forward == true){
						//set robot location
						Robot.setI(Robot.getI());
						Robot.setJ(Robot.getJ() - 1);
						Robot.setX(Robot.getX());
						Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
						Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "SOUTH";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 31;
					}
					
					else{
						leftTurnCount = 0;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI() - 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() - 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() + 2);
						Robot.realWest4.setI(Robot.realWest4.getI() - 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() - 2);
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() - 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnRight();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 32;
					}
				}
				
				//facing west
				else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
					boolean sensor1Forward = Robot.realNorth2.goForwardRealSensor1(messageSENDATA);
					if (sensor1Forward == true && turnedLeftPrev == true){
						//set robot location
						Robot.setI(Robot.getI() - 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 33;
					}
					else if (Robot.realWest4.goForwardRealSensor2(messageSENDATA) == true && (hasReachedGoal||leftTurnCount<=4)){
						leftTurnCount++;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI() + 2);
						Robot.realNorth1.setJ(Robot.realNorth1.getJ());
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() - 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI());
						Robot.realNorth3.setJ(Robot.realNorth3.getJ() - 2);
						Robot.realWest4.setI(Robot.realWest4.getI() + 2);
						Robot.realWest4.setJ(Robot.realWest4.getJ());
						Robot.realWest5.setI(Robot.realWest5.getI());
						Robot.realWest5.setJ(Robot.realWest5.getJ() + 2);
						Robot.realEast6.setI(Robot.realEast6.getI() + 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnLeft();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 34;
					}
					else if (sensor1Forward == true){
						//set robot location
						Robot.setI(Robot.getI() - 1);
						Robot.setJ(Robot.getJ());
						Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
						Robot.setY(Robot.getY());
						Robot.setDirection(Robot.ROBOT_DIR.WEST);
						turnedLeftPrev = false;
						String newI = Integer.toString(Robot.getI());
						String newJ = Integer.toString(Robot.getJ());
						String loc = newI + " " + newJ + " " + "WEST";
						
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
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.moveForward();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 35;
					}
					else{
						leftTurnCount = 0;
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
						Robot.realNorth1.setI(Robot.realNorth1.getI());
						Robot.realNorth1.setJ(Robot.realNorth1.getJ() + 2);
						Robot.realNorth2.setI(Robot.realNorth2.getI() + 1);
						Robot.realNorth2.setJ(Robot.realNorth2.getJ() + 1);
						Robot.realNorth3.setI(Robot.realNorth3.getI() + 2);
						Robot.realNorth3.setJ(Robot.realNorth3.getJ());
						Robot.realWest4.setI(Robot.realWest4.getI());
						Robot.realWest4.setJ(Robot.realWest4.getJ() + 2);
						Robot.realWest5.setI(Robot.realWest5.getI() - 2);
						Robot.realWest5.setJ(Robot.realWest5.getJ());
						Robot.realEast6.setI(Robot.realEast6.getI() - 1);
						Robot.realEast6.setJ(Robot.realEast6.getJ() + 1);
						Thread.sleep(TimeSleepSensor);
						//send message to arduino
						
						String botMessage; 
						do{
						ArduinoInterface.turnRight();
						botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
						}while(botMessage == null);
						
						//send message to android
						MapApp.nm.sendMessage(loc, NetworkManager.BOT_POS); //send location
						Thread.sleep(timeSleepAndroid);
						genMapFileEvent(); //send MDF
						
						//repaint
						Thread.sleep(timeSleep - offsetTimeSleep);
						MapApp.panelLeft.drawing();
						if (checkAutoTerminate() == true){
							//MapApp.nm.sendMessage("~", NetworkManager.BOT_STOP);
							break;
						}
						//return 36;
					}
					
				}
				

			}
			
			Thread.sleep(TimeSleepSensor);
			
		}
		
		//out of the while loop
		System.out.println("ROBOT HAS TERMINATED AND CALIBRATED AND IS READY FOR FASTEST PATH.");
		
	}


	
	

	
	public static boolean checkAutoTerminate(){
		int countExplored = 0;
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored()){
					countExplored++;
				}
			}
		}
		//System.out.println(countExplored + " grids are unexplored so far!");
		float percent = (float) countExplored/300;
		float coverage = percent * 100;
	
		if (hasReachedGoal == true && Robot.getI() == 1 && Robot.getJ() == 1){
			setToExplored();
			genMapFileEvent();
			System.out.println("Percentage explored: "+coverage);
			finalCalibrate();
			MapApp.nm.sendMessage("1 1 NORTH", NetworkManager.BOT_POS);
			MapApp.panelLeft.drawing();
			return true;
		}
	
		System.out.println("Percentage explored: "+coverage);
		return false;
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
		MapApp.nm.sendMessage(p1 + " " + p2, NetworkManager.MAP_STRINGS);
		
		
	}
	
	public static String genP1(){
		//bit 0: unexplored grid
		//bit 1: explored grid
		String begin = "11";
		String end = "11";
		String mapFile = "11";
		//mapFile = mapFile.concat(begin);
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
		//mapFile.concat(end);
		mapFile = mapFile.concat("11");
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
	
	public static void printCellValues(){
		System.out.println("RealNort1 I: "+Robot.realNorth1.getI());
		System.out.println("RealNorth1 J: "+Robot.realNorth1.getJ());
		System.out.println("RealNort2 I: "+Robot.realNorth2.getI());
		System.out.println("RealNorth2 J: "+Robot.realNorth2.getJ());
		System.out.println("RealNort3 I: "+Robot.realNorth3.getI());
		System.out.println("RealNorth3 J: "+Robot.realNorth3.getJ());
		System.out.println("RealWest4 I: "+Robot.realWest4.getI());
		System.out.println("RealWest4 J: "+Robot.realWest4.getJ());
		System.out.println("RealWest5 I: "+Robot.realWest5.getI());
		System.out.println("RealWest5 J: "+Robot.realWest5.getJ());
		System.out.println("RealEast6 I: "+Robot.realEast6.getI());
		System.out.println("RealEast6 J: "+Robot.realEast6.getJ());
	}
	
	
	public static void checkFrontCalibrate(String message) throws InterruptedException{
		SensorReal.dataValues = SensorReal.getSensorValues(message);
		SensorReal.data1 = SensorReal.dataValues[0];
		SensorReal.data2 = SensorReal.dataValues[1];
		SensorReal.data3 = SensorReal.dataValues[2];
		SensorReal.data4 = SensorReal.dataValues[3];
		SensorReal.data5 = SensorReal.dataValues[4];
		SensorReal.data6 = SensorReal.dataValues[5];
		
		
		//if robot facing north
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			if (SensorReal.canFrontCalibrate(Robot.realNorth1.getI(), Robot.realNorth1.getJ() + 1) && 
					SensorReal.canFrontCalibrate(Robot.realNorth3.getI(), Robot.realNorth3.getJ() + 1)){
				System.out.println("DOING FRONT CALIBRATION NORTH NOW");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateFront();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
			}
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			if (SensorReal.canFrontCalibrate(Robot.realNorth1.getI() + 1, Robot.realNorth1.getJ()) && 
					SensorReal.canFrontCalibrate(Robot.realNorth3.getI() + 1, Robot.realNorth3.getJ())){
				System.out.println("DOING FRONT CALIBRATION EAST NOW");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateFront();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
			}
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			if (SensorReal.canFrontCalibrate(Robot.realNorth1.getI(), Robot.realNorth1.getJ() - 1) && 
					SensorReal.canFrontCalibrate(Robot.realNorth3.getI(), Robot.realNorth3.getJ() - 1)){
				System.out.println("DOING FRONT CALIBRATION SOUTH NOW");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateFront();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
			}
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
			if (SensorReal.canFrontCalibrate(Robot.realNorth1.getI() - 1, Robot.realNorth1.getJ()) && 
					SensorReal.canFrontCalibrate(Robot.realNorth3.getI() - 1, Robot.realNorth3.getJ())){
				System.out.println("DOING FRONT CALIBRATION WEST NOW");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateFront();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
			}
		}
	}


	
	
	//left wall calibrate
	public static boolean checkCalibrate2() throws InterruptedException{
		boolean calibrationDone = false;
		
		//robot facing north
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			
		
			boolean enterWestCalibration = SensorReal.canCalibrate(Robot.realWest4.getI() - 1, Robot.realWest4.getJ()) &&
					SensorReal.canCalibrate(Robot.realWest5.getI() - 1, Robot.realWest5.getJ());
//					SensorReal.canCalibrate(Robot.realWest4.getI() - 1, Robot.realWest4.getJ() - 1) &&
			System.out.println("West calibration: "+enterWestCalibration);
			//west calibration
			//send parameters as the grid to check for obstacle or out of range
			if (enterWestCalibration){
				System.out.println("DOING NORTH CALIBRATION");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateTurnLeft();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
				calibrationDone = true;
			}
			
		}
		
		//robot facing east
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			
			
			//west calibration
			//send parameters as the grid to check for obstacle or out of range

			//SensorReal.canCalibrate(Robot.realWest4.getI() - 1, Robot.realWest4.getJ() + 1) &&
			boolean enterWestCalibration = SensorReal.canCalibrate(Robot.realWest4.getI(), Robot.realWest4.getJ() + 1) &&
					SensorReal.canCalibrate(Robot.realWest5.getI(), Robot.realWest5.getJ() + 1);
			System.out.println("Enter west calibration: "+enterWestCalibration);
			if (enterWestCalibration){
				System.out.println("DOING EAST CALIBRATION");
				System.out.println();	
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateTurnLeft();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
				calibrationDone = true;
			}
			
		}
		
		//robot facing south
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			
			
			//west calibration
			//send parameters as the grid to check for obstacle or out of range

			//SensorReal.canCalibrate(Robot.realWest4.getI() + 1, Robot.realWest4.getJ() + 1) &&
			boolean enterWestCalibration = SensorReal.canCalibrate(Robot.realWest4.getI() + 1, Robot.realWest4.getJ()) &&
					SensorReal.canCalibrate(Robot.realWest5.getI() + 1, Robot.realWest5.getJ());
			if (enterWestCalibration){
				System.out.println("DOING SOUTH CALIBRATION");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateTurnLeft();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
				
				calibrationDone = true;
			}
			
		}
		
		//robot facing west
		else{
			
			
			//west calibration
			//send parameters as the grid to check for obstacle or out of range

			//SensorReal.canCalibrate(Robot.realWest4.getI() + 1, Robot.realWest4.getJ() - 1) &&
			boolean enterWestCalibration = SensorReal.canCalibrate(Robot.realWest4.getI(), Robot.realWest4.getJ() - 1) &&
					SensorReal.canCalibrate(Robot.realWest5.getI(), Robot.realWest5.getJ() - 1);
			if (enterWestCalibration){
				System.out.println("DOING WEST CALIBRATION");
				Thread.sleep(TimeSleepSensor);
				String botMessage; 
				do{
				ArduinoInterface.calibrateTurnLeft();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.CALI_DONE);
				}while(botMessage == null);
				calibrationDone = true;
			}
			
		}
		
		return calibrationDone;
		
	}
	
	
	
	public static void renewMDF(){
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsObstacle() && MapLeft.cellArray[i][j].getIsPhysicallyVisited()){
					MapLeft.cellArray[i][j].setIsObstacle(false);
					System.out.println("renewMDF cellArray[i][j] = "+i + " , "+j);
					MapApp.panelLeft.drawing();
					//System.out.println("physicallyVisitedI: "+ i+ " physicallyVisitedJ: "+j);
					
				}
			}
		}
	}
	
	public static void setToExplored(){
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored() == false){
					MapLeft.cellArray[i][j].setIsExplored(true, true);
				}
			}
		}
	}
	
	public static void finalCalibrate(){
		//rotate the robot till it faces north
		//take sensor reading
		//left wall calibrate
		//take sensor reading
		//set the robot direction to north
		//check for front calibrate
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			String botMessage; 
			do{
			ArduinoInterface.returnSensorData();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkCalibrate2(); //left wall calibrate
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			do{
				ArduinoInterface.returnSensorData();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkFrontCalibrate(botMessage); //front wall calibrate
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			String botMessage; 
			do{
			ArduinoInterface.turnLeft();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
			}while(botMessage == null);
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
			ArduinoInterface.returnSensorData();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkCalibrate2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
				ArduinoInterface.returnSensorData();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkFrontCalibrate(botMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			String botMessage; 
			do{
			ArduinoInterface.turnLeft180();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
			}while(botMessage == null);
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
			ArduinoInterface.returnSensorData();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkCalibrate2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
				ArduinoInterface.returnSensorData();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkFrontCalibrate(botMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else if(Robot.getDirection() == Robot.ROBOT_DIR.WEST){
			String botMessage; 
			do{
			ArduinoInterface.turnRight();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.BOT_DONE);
			}while(botMessage == null);
			
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
			ArduinoInterface.returnSensorData();
			botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkCalibrate2();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			do{
				ArduinoInterface.returnSensorData();
				botMessage = ArduinoInterface.returnMessage(NetworkManager.SENSOR_DATA);
			}while(botMessage == null);
			
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			SensorReal.dataValues = SensorReal.getSensorValues(botMessage);
			
			try {
				checkFrontCalibrate(botMessage);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else{
			System.out.println("FINAL CALIBRATE FALLING IN THE ELSE BLOCKKKKKK");
		}
		
	}
	
}
