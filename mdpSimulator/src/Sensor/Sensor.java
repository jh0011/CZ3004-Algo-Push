package Sensor;
import Map.*;
import Robot.*;

public class Sensor {
	
	private int id;
	private int range;
	private int senLocX; //coordinates from top left corner
	private int senLocY; //coordinates from top left corner
	private final int i=19;
	private final int j=14;
	
	public static int p;
	public static int k;
	
	protected SENSOR_DIR dir = SENSOR_DIR.NORTH;
	
	public enum SENSOR_DIR{
		NORTH, EAST, SOUTH, WEST; 
	}
	
	public Sensor(int id, int senRange, int senX, int senY, SENSOR_DIR direction){
		this.id = id;
		range = senRange;
		senLocX = senX;
		senLocY = senY;
		dir = direction;
	}
	
	public void setRange(int senRange){
		range = senRange;
	}
	
	public int getRange(){
		return range;
	}
	
	public void setDir(SENSOR_DIR direction){
		dir = direction;
	}
	
	public SENSOR_DIR getDir(){
		return dir;
	}
	
	public void setSenLocX(int loc){
		senLocX = loc;
	}
	
	public int getSenLocX(){
		return senLocX;
	}
	
	public void setSenLocY(int loc){
		senLocY = loc;
	}
	
	public int getSenLocY(){
		return senLocY;
	}
	
	
	public static boolean goForwardSensor1(){
		
		//if facing north
		//if (Robot.sensorNorth1.getDir() == SENSOR_DIR.NORTH){
			if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						//Sensor one location
						
						
						int indx=getNorth1X(); //j
						int indy=getNorth1Y(); //i
						int indxx=getEast1X(); //j
						int indyy=getEast1Y(); //i
						int indxWest=getWest1X(); //j
						int indyWest=getWest1Y(); //i
						
						if (indy == 0){
							return false;
						}
						
						//east sensor
						int count = 1;
						int counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<4; b++){
								if (indxx + count > 14){
									break;
								}
								if (MapLeft.cellArray[indyy + counter][indxx + count].getIsObstacle()){
									MapLeft.cellArray[indyy + counter][indxx + count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyy + counter][indxx + count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						
						//west sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indxWest - count < 0){
									break;
								}
								if (MapLeft.cellArray[indyWest + counter][indxWest - count].getIsObstacle()){
									MapLeft.cellArray[indyWest + counter][indxWest - count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyWest + counter][indxWest - count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//north sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indy - count < 0){
									break;
								}
								if (MapLeft.cellArray[indy - count][indx + counter].getIsObstacle()){
									MapLeft.cellArray[indy - count][indx + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indy - count][indx + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
					
						
						//north sensor
						if (i==indy-1){//row = 1 above the sensor
							if (j==indx-1 || j==indx || j==indx+1){ //col = 3 cells
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
						
						
					}
				}
				return true;
			}
		//}
		
		
		//if facing east
		//if (Robot.sensorNorth1.getDir() == SENSOR_DIR.EAST){
			if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
				
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						//Sensor one location
						int indx=getNorth1X(); //j
						int indy=getNorth1Y(); //i
						int indxx=getEast1X(); //j
						int indyy=getEast1Y(); //i
						int indxWest=getWest1X(); //j
						int indyWest=getWest1Y(); //i
						
						if (indx == 14){
							return false;
						}
						
						//east sensor
						int counter = -1;
						int count = 1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<4; b++){
								if (indyy + count > 19){
									break;
								}
								if (MapLeft.cellArray[indyy + count][indxx + counter].getIsObstacle()){
									MapLeft.cellArray[indyy + count][indxx + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyy + count][indxx + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//west sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indyWest - count < 0){
									break;
								}
								if (MapLeft.cellArray[indyWest - count][indxWest + counter].getIsObstacle()){
									MapLeft.cellArray[indyWest - count][indxWest + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyWest - count][indxWest + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//north sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indx + count > 14){
									break;
								}
								if (MapLeft.cellArray[indy + counter][indx + count].getIsObstacle()){
									MapLeft.cellArray[indy + counter][indx + count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indy + counter][indx + count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
					
						
						//north sensor
						if(j==indx+1){//col = 1 to the right of sensor
							if (i==indy-1 || i==indy || i==indy+1){
								MapApp.panelLeft.drawing();
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		//} 
		
		//if facing south
		//if (Robot.sensorNorth1.getDir() == SENSOR_DIR.SOUTH){
			if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
				
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						
						//Sensor one location
						int indx=getNorth1X(); //j
						int indy=getNorth1Y(); //i
						int indxx=getEast1X(); //j
						int indyy=getEast1Y(); //i
						int indxWest=getWest1X(); //j
						int indyWest=getWest1Y(); //i
						
						if(indy == 19){
							return false;
						}
						
						//east sensor
						int counter = -1;
						int count = 1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<4; b++){
								if (indxx - count < 0){
									break;
								}
								if (MapLeft.cellArray[indyy + counter][indxx - count].getIsObstacle()){
									MapLeft.cellArray[indyy + counter][indxx - count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyy + counter][indxx - count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//west sensor
						counter  = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indxWest + count > 14){
									break;
								}
								if (MapLeft.cellArray[indyWest + counter][indxWest + count].getIsObstacle()){
									MapLeft.cellArray[indyWest + counter][indxWest + count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyWest + counter][indxWest + count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//north sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indy + count > 19){
									break;
								}
								if (MapLeft.cellArray[indy + count][indx + counter].getIsObstacle()){
									MapLeft.cellArray[indy + count][indx + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indy + count][indx + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
					
						
						//north sensor
						if(i==indy+1){//row = 1 above the sensor
							if (j==indx-1 || j==indx || j==indx+1){ //col = 3 cells
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		//}
		
		//if facing west
		//if (Robot.sensorNorth1.getDir() == SENSOR_DIR.WEST){
			if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
				
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						
						//Sensor one location
						int indx=getNorth1X(); //j
						int indy=getNorth1Y(); //i
						int indxx=getEast1X(); //j
						int indyy=getEast1Y(); //i
						int indxWest=getWest1X(); //j
						int indyWest=getWest1Y(); //i
						
						if (indx == 0){
							return false;
						}
						
						//east sensor
						int counter = -1;
						int count = 1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<4; b++){
								if (indyy - count < 0){
									break;
								}
								if (MapLeft.cellArray[indyy - count][indxx + counter].getIsObstacle()){
									MapLeft.cellArray[indyy - count][indxx + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyy - count][indxx + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//west sensor
						counter = -1;
						for (int a =0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indyWest + count > 19){
									break;
								}
								if (MapLeft.cellArray[indyWest + count][indxWest + counter].getIsObstacle()){
									MapLeft.cellArray[indyWest + count][indxWest + counter].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indyWest + count][indxWest + counter].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						//north sensor
						counter = -1;
						for (int a=0; a<3; a++){
							count = 1;
							for (int b=0; b<3; b++){
								if (indx - count < 0){
									break;
								}
								if (MapLeft.cellArray[indy + counter][indx - count].getIsObstacle()){
									MapLeft.cellArray[indy + counter][indx - count].setIsExplored(true, true);
									break;
								}
								else{
									MapLeft.cellArray[indy + counter][indx - count].setIsExplored(true, true);
								}
								count++;
							}
							counter++;
						}
						
						
						
						//north sensor
						if(j==indx-1){//col = 1 to the right of sensor
							if (i==indy-1 || i==indy || i==indy+1){
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		//} 
		
		
		return false;
	}
	
	public boolean goForwardSensor2(){
		
		//robot is facing north
		if (Robot.sensorWest1.getDir() == SENSOR_DIR.WEST){
			if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						
						//Sensor two location
						int indx=getWest1X();
						int indy=getWest1Y();
						int indxx=getEast1X();
						int indyy=getEast1Y();
						int indxN=getNorth1X();
						int indyN=getNorth1Y();
						if (indx == 0){
							return false;
						}
						
						//west sensor || j==indx-2
						if(j==indx-1 ){//col = 1 to the left of sensor 
							if (i==indy-1 || i==indy || i==indy+1){
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
			
		}
		
		//robot is facing east
		if (Robot.sensorWest1.getDir() == SENSOR_DIR.NORTH){
			if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						
						//Sensor two location
						int indx=getWest1X();
						int indy=getWest1Y();
						int indxx=getEast1X();
						int indyy=getEast1Y();
						int indxN=getNorth1X();
						int indyN=getNorth1Y();
						
						
						if (indy == 0){
							return false;
						}
						
						//west sensor || i==indy-2
						if (i==indy-1 ){//row = 1 above the sensor 
							if (j==indx-1 || j==indx || j==indx+1){ //col = 3 cells
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									//goForward = false;
									return false;
								}
							}
						}
					}
				}
				return true;
			}
			
		}
		
		
		//if robot is facing south
		if (Robot.sensorWest1.getDir() == SENSOR_DIR.EAST){
			if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						//Sensor two location
						int indx=getWest1X();
						int indy=getWest1Y();
						int indxx=getEast1X();
						int indyy=getEast1Y();
						int indxN=getNorth1X();
						int indyN=getNorth1Y();
						
						
						if (indx == 14){
							return false;
						}
						
						//west sensor
						if(j==indx+1){//col = 1 to the right of sensor 
							if (i==indy-1 || i==indy || i==indy+1){
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		}
		
		
		//robot is facing west
		if (Robot.sensorWest1.getDir() == SENSOR_DIR.SOUTH){
			if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
				for (int i=0; i<MapLeft.ROWS; i++){
					for (int j=0; j<MapLeft.COLS; j++){
						
						//Sensor two location
						int indx=getWest1X();
						int indy=getWest1Y();
						int indxx=getEast1X();
						int indyy=getEast1Y();
						int indxN=getNorth1X();
						int indyN=getNorth1Y();
						
						
						if (indy == 19){
							return false;
						}
						
						//west sensor
						if (i==indy+1){//row = 1 above the sensor 
							if (j==indx-1 || j==indx || j==indx+1){ //col = 3 cells
								if (MapLeft.cellArray[i][j].getIsObstacle() == true){
									return false;
								}
							}
						}
					}
				}
				return true;
			}
		}
		
		return false;
	}
	
	
	public static int getNorth1X(){
		int sensorx = Robot.sensorNorth1.getSenLocX();
		int indx =  sensorx / MapLeft.GRID_SIZE;
		return indx;
	}
	
	public static int getNorth1Y(){
		int sensory = Robot.sensorNorth1.getSenLocY();
		int indy = sensory / MapLeft.GRID_SIZE;
		return indy;
	}
	
	public static int getWest1X(){
		int sensorx = Robot.sensorWest1.getSenLocX();
		int indx =  sensorx / MapLeft.GRID_SIZE;
		return indx;
	}
	
	public static int getWest1Y(){
		int sensory = Robot.sensorWest1.getSenLocY();
		int indy = sensory / MapLeft.GRID_SIZE;
		return indy;
	}
	
	public static int getEast1X(){
		int sensorxx = Robot.sensorEast1.getSenLocX();
		int indxx =  sensorxx / MapLeft.GRID_SIZE;
		return indxx;
	}
	
	public static int getEast1Y(){
		int sensoryy = Robot.sensorEast1.getSenLocY();
		int indyy = sensoryy / MapLeft.GRID_SIZE;
		return indyy;
	}

}