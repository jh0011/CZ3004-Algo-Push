package Robot;

import Sensor.Sensor;
import Sensor.SensorReal;

public class Robot {
	
	//robot's direction
	
	public static ROBOT_DIR dir = ROBOT_DIR.NORTH;
	
	public static final int ROBOT_DIR_SIZE = 20;
	public static final int ROBOT_SIZE = 100;
	public static final int ROBOT_CONTOUR = 150;
	public static final int NUM_SENSORS = 3;
	public static final int SR_RANGE = 100; //SR might be 3 grids
	public static final int LR_RANGE = 150; //LR might be 4 grids
	public static int INIT_BOT_X = 75; //1*gridsize 25     //(x=1)
	public static int INIT_BOT_Y = 925; //18*gridsize + 25 //(y=1) = ROWS - 18
	
	public static int botI; //i value from bottom left (horizontal)
	public static int botJ; //j value from bottom left (vertical)
	
	private static int botCorx = 75; //x coordinate of robot wrt top left
	private static int botCory = 925; //y coordinate of robot wrt top left
	
	
	//simulator sensor
	public static Sensor sensorNorth1 = new Sensor(0, SR_RANGE, botCorx - 25, botCory - 75, Sensor.SENSOR_DIR.NORTH);
	public static Sensor sensorWest1 = new Sensor(1, SR_RANGE, botCorx - 75, botCory - 25, Sensor.SENSOR_DIR.WEST);
	public static Sensor sensorEast1 = new Sensor(2, LR_RANGE, botCorx + 25, botCory - 25, Sensor.SENSOR_DIR.EAST);
	
	
	//real run sensor
	public static SensorReal realNorth1 = new SensorReal(1, 12, 20, botI - 1, botJ + 1, SensorReal.SENSOR_DIR.NORTH); //9.5
	public static SensorReal realNorth2 = new SensorReal(2, 12, 20, botI, botJ + 1, SensorReal.SENSOR_DIR.NORTH); //7
	public static SensorReal realNorth3 = new SensorReal(3, 12, 20, botI + 1, botJ + 1, SensorReal.SENSOR_DIR.NORTH); //10
	public static SensorReal realWest4 = new SensorReal(4, 19, 20, botI - 1, botJ + 1, SensorReal.SENSOR_DIR.WEST); //16
	public static SensorReal realWest5 = new SensorReal(5, 13, 20, botI - 1, botJ - 1, SensorReal.SENSOR_DIR.WEST); //11
	public static SensorReal realEast6 = new SensorReal(6, 28, 40, botI - 1, botJ, SensorReal.SENSOR_DIR.EAST);
	
	
	
	public enum ROBOT_DIR {
		NORTH, EAST, SOUTH, WEST; 
	}
	
	public static ROBOT_DIR getDirection(){
		return dir;
	}
	
	public static void setDirection(ROBOT_DIR direction){
		dir = direction;
	}
	
	public static void setX(int x){
		botCorx = x;  
	}
	
	public static void setY(int y){
		botCory = y; 
	}
	
	public static int getX(){
		return botCorx;
	}
	
	public static int getY(){
		return botCory;
	}
	
	public static void setI(int i){
		botI = i;
		//System.out.println("set botI = "+botI);
	}
	
	public static void setJ(int j){
		botJ = j;
		//System.out.println("set botJ = "+botJ);
	}
	
	public static int getI(){
		//System.out.println("set botI = "+botI);
		return botI;
	}
	
	public static int getJ(){
		//System.out.println("set botJ = "+botJ);
		return botJ;
	}
	
	/*public static void setSensorLocations(int i, int j){ //i and j wrt to bottom left
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			Robot.realNorth1.setI(i + 1);
			Robot.realNorth1 .setJ(j - 1);
			Robot.realNorth2.setI(i + 1);
			Robot.realNorth2.setJ(j);
			Robot.realNorth3.setI(i + 1);
			Robot.realNorth3.setJ(j + 1);
			Robot.realWest4.setI(i + 1);
			Robot.realWest5.setJ(j - 1);
			Robot.realWest5.setI(i - 1);
			Robot.realWest5.setJ(j - 1);
			Robot.realEast6.setI(i + 1);
			Robot.realEast6.setJ(j + 1);
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			Robot.realNorth1.setI(i + 1);
			Robot.realNorth1 .setJ(j + 1);
			Robot.realNorth2.setI(i);
			Robot.realNorth2.setJ(j + 1);
			Robot.realNorth3.setI(i - 1);
			Robot.realNorth3.setJ(j + 1);
			Robot.realWest4.setI(i + 1);
			Robot.realWest5.setJ(j + 1);
			Robot.realWest5.setI(i + 1);
			Robot.realWest5.setJ(j - 1);
			Robot.realEast6.setI(i - 1);
			Robot.realEast6.setJ(j + 1);
		}
		
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			Robot.realNorth1.setI(i - 1);
			Robot.realNorth1 .setJ(j + 1);
			Robot.realNorth2.setI(i - 1);
			Robot.realNorth2.setJ(j);
			Robot.realNorth3.setI(i - 1);
			Robot.realNorth3.setJ(j - 1);
			Robot.realWest4.setI(i - 1);
			Robot.realWest5.setJ(j + 1);
			Robot.realWest5.setI(i + 1);
			Robot.realWest5.setJ(j + 1);
			Robot.realEast6.setI(i - 1);
			Robot.realEast6.setJ(j - 1);
		}
		
		else{
			Robot.realNorth1.setI(i - 1);
			Robot.realNorth1 .setJ(j - 1);
			Robot.realNorth2.setI(i);
			Robot.realNorth2.setJ(j - 1);
			Robot.realNorth3.setI(i + 1);
			Robot.realNorth3.setJ(j - 1);
			Robot.realWest4.setI(i - 1);
			Robot.realWest5.setJ(j - 1);
			Robot.realWest5.setI(i - 1);
			Robot.realWest5.setJ(j + 1);
			Robot.realEast6.setI(i + 1);
			Robot.realEast6.setJ(j - 1);
		}
	}*/


}