package Sensor;

/*
 * If sensor2 is at the same level as sensor1 and sensor3,
 * range = (data2/REAL_CELL);
 * else,
 * range = (data2/REAL_CELL) + 1;
 */

import Algorithm.ExplorationReal;
import Map.MapApp;
import Map.MapLeft;
import Network.NetworkManager;
import Robot.Robot;
public class SensorReal {
	
	public static int id;
	public static int lowerRange;
	public static int upperRange;
	
	public static int north1Lower = 15;
	public static int north1Upper = 52;
	public static int north2Lower = 12;
	public static int north2Upper = 40; 
	public static int north3Lower = 14; //13
	public static int north3Upper = 44; 
	public static int west4Lower = 22; //18 //20
	public static int west4Upper = 46;
	public static int west5Lower = 16; //14
	public static int west5Upper = 30;
	public static int east6Lower = 32; //28
	public static int east6Upper = 65; 
	
	public static int sen1Offset = -3; //5 //3
	public static int sen2Offset = 0; //3 //2
	public static int sen3Offset = 0; //5 //4
	public static int sen4Offset = 0;  //12
	public static int sen5Offset = 0;  //6
	public static int sen6Offset = 3; //5
	
	
	public int sensori; //i value wrt to bottom left (horizontal)
	public int sensorj; //j value wrt to bottom left (vertical)
	
	public static double data1;
	public static double data2;
	public static double data3;
	public static double data4;
	public static double data5;
	public static double data6;
	public static double[] dataValues = new double[6];
	
	protected static final int NORTH_MAX_RANGE = 12;
	protected static final int WEST_MAX_RANGE = 19;
	
	
	public static final int REAL_CELL = 10;
	
	protected SENSOR_DIR dir = SENSOR_DIR.NORTH;
	
	public enum SENSOR_DIR{
		NORTH, EAST, SOUTH, WEST; 
	}
	
	public SensorReal(int idNum, int lRange, int uRange, int i, int j, SENSOR_DIR direction){
		id = idNum;
		lowerRange = lRange;
		upperRange = uRange;
		sensori = i;
		sensorj = j;
		dir = direction;
	}
	
	public void setI(int i){
		sensori = i;
	}
	
	public int getI(){
		return sensori;
	}
	
	public void setJ(int j){
		sensorj = j;
	}
	
	public int getJ(){
		return sensorj;
	}
	
	public int getLRange(){
		return lowerRange;
	}
	
	public int getURange(){
		return upperRange;
	}
	
	public void setSenDir(SENSOR_DIR direction){
		dir = direction;
	}
	
	public SENSOR_DIR getSenDir(){
		return dir;
	}
	
	//check if can go forward
	//north sensor
	public static boolean goForwardRealSensor1(String message){//message consists of all 5 readings
		
		//double[] dataValues = getSensorValues(message);
		dataValues = getSensorValues(message);
		data1 = dataValues[0];
		data2 = dataValues[1];
		data3 = dataValues[2];
		data4 = dataValues[3];
		data5 = dataValues[4];
		data6 = dataValues[5];
		
//		System.out.println("data1: "+ data1);
//		System.out.println("data2: "+ data2);
//		System.out.println("data3: "+ data3);
//		System.out.println("data4: "+ data4);
//		System.out.println("data5: "+ data5);
//		System.out.println("data6: "+ data6);
		
		
		try{
			System.out.println("Robot's direction: "+Robot.getDirection());
			//////////////////////////////facing north
			
			if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
				//DATA 1 PROCESSING
				if (data1 <= north1Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth1.getJ()+1 > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsObstacle(true);
						//System.out.println("Printing north 1c");
						//System.out.println("Data1c: "+data1 + ", realNorth1 lower range: "+SensorReal.north1Lower);
						//System.out.println("SENSOR1 NORTH NORTH1 C");
					}
				}
				
				if (data1 <= north1Upper && data1 > north1Lower){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(data1/REAL_CELL); i++){
						if (Robot.realNorth1.getJ() + i > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsObstacle(false);
						if (i == (int)(data1/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 NORTH NORTH1 B");
						
					}
				}
				 
				if (data1 > north1Upper){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(north1Upper/REAL_CELL); i++){
						if (Robot.realNorth1.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() + i)][Robot.realNorth1.getI()].setIsObstacle(false);
						//System.out.println("Printing north 1a");
						//System.out.println("Data1a: "+data1 + ", realNorth1 upper range: "+SensorReal.north1Upper);
						//System.out.println("SENSOR1 NORTH NORTH1 A");
					}
				}
				
				
				
				//DATA2 PROCESSING
				 if (data2 <= north2Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth2.getJ()+1 > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsObstacle(true);
						//System.out.println("Printing north 2c");
						//System.out.println("Data2c: "+data2 + ", realNorth2 Lower range: "+SensorReal.north2Lower);
						//System.out.println("SENSOR1 NORTH NORTH2 C");
					}
				}
				
				
				 if (data2 <= north2Upper && data2 > north2Lower){
					 data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(data2/REAL_CELL)+1; i++){
						if (Robot.realNorth2.getJ() + i > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsObstacle(false);
						if (i == (int)(data2/REAL_CELL)+1){
							/*System.out.println("Printing north 2b");
							System.out.println("Data2b: "+data2 + ", realNorth2 Lower range: "+SensorReal.north2Lower + 
									", realNorth2 Upper range: "+SensorReal.north2Upper);*/
							MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 NORTH NORTH2 B");
					}
				}
				
				 if (data2 > north2Upper){
					 data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(north2Upper/REAL_CELL); i++){
						if (Robot.realNorth2.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + i)][Robot.realNorth2.getI()].setIsObstacle(false);
						//System.out.println("Printing north 2a");
						//System.out.println("Data2a: "+data2 + ", realNorth2 upper range: "+SensorReal.north2Upper);
						//System.out.println("SENSOR1 NORTH NORTH2 A");
					}
				}
				
				
				
				//DATA3 PROCESSING
				if (data3 <= north3Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth3.getJ()+1 > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 NORTH NORTH3 C");
					}
				}
				
				
				
				if (data3 <= north3Upper && data3 > north3Lower){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(data3/REAL_CELL); i++){
						if (Robot.realNorth3.getJ() + i > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsObstacle(false);
						if (i == (int)(data3/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 NORTH NORTH3 B");
					}
				}
				
				if (data3 > north3Upper){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(north3Upper/REAL_CELL); i++){
						if (Robot.realNorth3.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() + i)][Robot.realNorth3.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 NORTH NORTH3 A");
					}
				}
				
				
				
				//DATA4 PROCESSING
				if (data4 <= west4Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest4.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsObstacle(true);
						//System.out.println("SENSOR1 NORTH WEST4 C");
					}
				}
				
				
				
				if (data4 <= west4Upper && data4 > west4Lower){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(data4/REAL_CELL)  + 1; i++){
						if (Robot.realWest4.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsObstacle(false);
						if (i == (int)(data4/REAL_CELL) + 1){
							MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 NORTH WEST4 B");
					}
				}
				
				if (data4 > west4Upper){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(west4Upper/REAL_CELL); i++){
						if (Robot.realWest4.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() - i].setIsObstacle(false);
						//System.out.println("SENSOR1 NORTH WEST4 A");
					}
				}
				
				//DATA5 PROCESSING
				if (data5 <= west5Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest5.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsObstacle(true);
						//System.out.println("SENSOR1 NORTH WEST5 C");
					}
				}
				
				
				
				if (data5 <= west5Upper && data5 > west5Lower){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(data5/REAL_CELL); i++){
						if (Robot.realWest5.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsObstacle(false);
						if (i == (int)(data5/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 NORTH WEST5 B");
					}
				}
				
				if (data5 > west5Upper){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(west5Upper/REAL_CELL); i++){
						if (Robot.realWest5.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() - i].setIsObstacle(false);
						//System.out.println("SENSOR1 NORTH WEST5 A");
					}
				}
				
				//DATA6 PROCESSING
				System.out.println("data6 <= east6Lower: "+(data6 <= east6Lower));
				if (data6 <= east6Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realEast6.getI() + 3 > 14){
							break;
						}
						//System.out.println("IsSetBySR: "+ (!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + 3].isSetBySR()));
						//System.out.println((Robot.realEast6.getJ())+" "+(Robot.realEast6.getI()+3));
						if (!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + 3].isSetBySR()){
							System.out.println("COLOURING EAST SENSOR");
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + 3].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + 3].setIsObstacle(true);
							//System.out.println("SENSOR1 NORTH EAST6 C");
						}
					}
				}
				
				
				
				if (data6 > east6Lower && data6 <= east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(data6/REAL_CELL) + 1; i++){
						if (Robot.realEast6.getI() + i > 14){
							break;
						}
						if (!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].isSetBySR()){
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].setIsObstacle(false);
							if (i == (int)(data6/REAL_CELL) + 1){
								MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].setIsObstacle(true);
							}
							//System.out.println("SENSOR1 NORTH EAST6 B");
						}
					}
				}
				
				if (data6 > east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(east6Upper/REAL_CELL); i++){
						if (Robot.realEast6.getI() + i > 14){
							break;
						}
						if(!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].isSetBySR()){
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() + i].setIsObstacle(false);
							//System.out.println("SENSOR1 NORTH EAST6 A");
						}
					}
				}
				
				data1+=sen1Offset;
				data2+=sen2Offset;
				data3+=sen3Offset;
				
				//check immediate row north
				if (data1 <= north1Lower || data2 <= north2Lower || data3 <= north3Lower){
//					System.out.println("data1: "+ data1);
//					System.out.println("data2: "+ data2);
//					System.out.println("data3: "+ data3);
					
					System.out.println("EXITING HERE 1");
					return false;
				}
				for (int i=-1; i<=1; i++){
					if (Robot.realNorth2.getJ() + 1 > 19){
						System.out.println("EXITING HERE 2");
						return false;
					}
					/*if (MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + 1)][Robot.realNorth2.getI() + i].getIsObstacle()){
						return false;
					}*/
				}
				System.out.println("EXITING HERE 3");
				return true;
				
			}
			
			/////////////////////////////////////////facing east
			else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
				//DATA 1 PROCESSING
				if (data1 <= north1Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth1.getI()+1 > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsObstacle(true);
						//System.out.println("SENSOR1 EAST NORTH1 C");
					}
				}
				
				if (data1 <= north1Upper && data1 > north1Lower){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(data1/REAL_CELL); i++){
						if (Robot.realNorth1.getI() + i > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsObstacle(false);
						if (i == (int)(data1/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsObstacle(true);
							//System.out.println("SENSOR VALUE: "+ dataValues[0]);
						}
						//System.out.println("SENSOR1 EAST NORTH1 B");
					}
				}
				
				if (data1 > north1Upper){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(north1Upper/REAL_CELL); i++){
						if (Robot.realNorth1.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() + i].setIsObstacle(false);
						//System.out.println("SENSOR1 EAST NORTH1 A");
					}
				}
				
				
				
				//DATA2 PROCESSING
				if (data2 <= north2Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth2.getI()+1 > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsObstacle(true);
						//System.out.println("SENSOR1 EAST NORTH2 C");
					}
				}
				
				if (data2 <= north2Upper && data2 > north2Lower){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(data2/REAL_CELL)+1; i++){
						if (Robot.realNorth2.getI() + i > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsObstacle(false);
						if (i == (int)(data2/REAL_CELL) + 1){
							MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 EAST NORTH2 B");
					}
				}
				
				if (data2 > north2Upper){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(north2Upper/REAL_CELL); i++){
						if (Robot.realNorth2.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() + i].setIsObstacle(false);
						//System.out.println("SENSOR1 EAST NORTH2 A");
					}
				}
				
				//DATA3 PROCESSING
				if (data3 <= north3Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth3.getI()+1 > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsObstacle(true);
						//System.out.println("SENSOR1 EAST NORTH3 C");
					}
				}
				
				if (data3 <= north3Upper && data3 > north3Lower){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(data3/REAL_CELL); i++){
						if (Robot.realNorth3.getI() + i > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsObstacle(false);
						if (i == (int)(data3/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 EAST NORTH3 B");
					}
				}
				
				if (data3 > north3Upper){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(north3Upper/REAL_CELL); i++){
						if (Robot.realNorth3.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() + i].setIsObstacle(false);
						//System.out.println("SENSOR1 EAST NORTH3 A");
					}
				}
				
				
				
				//DATA4 PROCESSING
				if (data4 <= west4Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest4.getJ() + i > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 EAST WEST4 C");
					}
				}
				
				if (data4 <= west4Upper && data4 > west4Lower){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(data4/REAL_CELL); i++){
						if (Robot.realWest4.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsObstacle(false);
						if (i == (int)(data4/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 EAST WEST4 B");
					}
				}
				
				if (data4 > west4Upper){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(west4Upper/REAL_CELL); i++){
						if (Robot.realWest4.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() + i)][Robot.realWest4.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 EAST WEST4 A");
					}
				}
				
				//DATA5 PROCESSING
				if (data5 <= west5Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest5.getJ() + i > 19){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 EAST WEST5 C");
					}
				}
				
				
				if (data5 <= west5Upper && data5 > west5Lower){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(data5/REAL_CELL); i++){
						if (Robot.realWest5.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsObstacle(false);
						if (i == (int)(data5/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 EAST WEST5 B");
					}
				}
				
				if (data5 > west5Upper){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(west5Upper/REAL_CELL); i++){
						if (Robot.realWest5.getJ() + i > 19){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() + i)][Robot.realWest5.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 EAST WEST5 A");
					}
				}
				
				//DATA6 PROCESSING
				if (data6 <= east6Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realEast6.getJ() - 3 < 0){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() -3)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -3)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -3)][Robot.realEast6.getI()].setIsObstacle(true);
							//System.out.println("SENSOR1 EAST EAST6 C");
						}
					}
				}
				

				if (data6 > east6Lower && data6 <= east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(data6/REAL_CELL) + 1; i++){
						
						if (Robot.realEast6.getJ() - i < 0){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].setIsObstacle(false);
							if (i == (int)(data6/REAL_CELL) + 1){
								MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].setIsObstacle(true);
							}
							//System.out.println("SENSOR1 EAST EAST6 B");
						}
					}
				}
				
				if (data6 > east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(east6Upper/REAL_CELL); i++){
						if (Robot.realEast6.getJ() - i < 0){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() -i)][Robot.realEast6.getI()].setIsObstacle(false);
							//System.out.println("SENSOR1 EAST EAST6 A");
						}
					}
				}
				
				//printObstacleMaze();
				
				data1+=sen1Offset;
				data2+=sen2Offset;
				data3+=sen3Offset;
				
				//check immediate row east
				if (data1 <= north1Lower || data2 <= north2Lower || data3 <= north3Lower){
					System.out.println("EXITING HERE 4");
					return false;
				}
				for (int i=-1; i<=1; i++){
					if (Robot.realNorth2.getI() + 1 > 14){
						System.out.println("EXITING HERE 5");
						return false;
					}
					/*if (MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + 1)][Robot.realNorth2.getI() + i].getIsObstacle()){
						return false;
					}*/
				}
				System.out.println("EXITING HERE 6");
				return true;
			}
			
			//////////////////////////////////////facing south
			else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
				
				//DATA 1 PROCESSING
				if (data1 <= north1Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth1.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 SOUTH NORTH1 C");
					}
				}
				
				if (data1 <= north1Upper && data1 > north1Lower){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(data1/REAL_CELL); i++){
						if (Robot.realNorth1.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsObstacle(false);
						if (i == (int)(data1/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 SOUTH NORTH1 B");
					}
				}
				
				if (data1 > north1Upper){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(north1Upper/REAL_CELL); i++){
						if (Robot.realNorth1.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ() - i)][Robot.realNorth1.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 SOUTH NORTH1 A");
					}
				}
				
				
				
				//DATA2 PROCESSING
				if (data2 <= north2Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth2.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 SOUTH NORTH2 C");
					}
				}
				
				if (data2 <= north2Upper && data2 > north2Lower){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(data2/REAL_CELL) + 1; i++){
						if (Robot.realNorth2.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsObstacle(false);
						if (i == (int)(data2/REAL_CELL) + 1){
							MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 SOUTH NORTH2 B");
					}
				}
				
				if (data2 > north2Upper){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(north2Upper/REAL_CELL); i++){
						if (Robot.realNorth2.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ() - i)][Robot.realNorth2.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 SOUTH NORTH2 A");
					}
				}
				
				//DATA3 PROCESSING
				if (data3 <= north3Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth3.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 SOUTH NORTH3 C");
					}
				}
				
				if (data3 <= north3Upper && data3 > north3Lower){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(data3/REAL_CELL); i++){
						if (Robot.realNorth3.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsObstacle(false);
						if (i == (int)(data3/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 SOUTH NORTH3 B");
					}
				}
				
				if (data3 > north3Upper){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(north3Upper/REAL_CELL); i++){
						if (Robot.realNorth3.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ() - i)][Robot.realNorth3.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 SOUTH NORTH3 A");
					}
				}
				
				
				
				//DATA4 PROCESSING
				if (data4 <= west4Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest4.getI() + i > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsObstacle(true);
						//System.out.println("SENSOR1 SOUTH WEST4 C");
					}
				}
				
				
				if (data4 <= west4Upper && data4 > west4Lower){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(data4/REAL_CELL); i++){
						if (Robot.realWest4.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsObstacle(false);
						if (i == (int)(data4/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 SOUTH WEST4 B");
					}
				}
				
				if (data4 > west4Upper){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(west4Upper/REAL_CELL); i++){
						if (Robot.realWest4.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ())][Robot.realWest4.getI() + i].setIsObstacle(false);
						//System.out.println("SENSOR1 SOUTH WEST4 A");
					}
				}
				
				//DATA5 PROCESSING
				if (data5 <= west5Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest5.getI() + i > 14){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsObstacle(true);
						//System.out.println("SENSOR1 SOUTH WEST5 C");
					}
				}
				
				if (data5 <= west5Upper && data5 > west5Lower){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(data5/REAL_CELL); i++){
						if (Robot.realWest5.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsObstacle(false);
						if (i == (int)(data5/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 SOUTH WEST5 B");
					}
				}
				
				if (data5 > west5Upper){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(west5Upper/REAL_CELL); i++){
						if (Robot.realWest5.getI() + i > 14){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ())][Robot.realWest5.getI() + i].setIsObstacle(false);
						//System.out.println("SENSOR1 SOUTH WEST5 A");
					}
				}
				
				//DATA6 PROCESSING
				if (data6 <= east6Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realEast6.getI() - 3 < 0){
							break;
						}
						if (!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - 3].isSetBySR()){
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - 3].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - 3].setIsObstacle(true);
							//System.out.println("SENSOR1 SOUTH EAST6 C");
						}
					}
				}
				
				if (data6 > east6Lower && data6 <= east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(data6/REAL_CELL) + 1; i++){
						if (Robot.realEast6.getI() - i < 0){
							break;
						}
						if(!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].isSetBySR()){
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].setIsObstacle(false);
							if (i == (int)(data6/REAL_CELL) + 1){
								MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].setIsObstacle(true);
							}
							//System.out.println("SENSOR1 SOUTH EAST6 B");
						}
					}
				}
				
				if (data6 > east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(east6Upper/REAL_CELL); i++){
						if (Robot.realEast6.getI() - i < 0){
							break;
						}
						if(!MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].isSetBySR()){
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].setIsExplored(true, false);
							MapLeft.cellArray[19 - Robot.realEast6.getJ()][Robot.realEast6.getI() - i].setIsObstacle(false);
							//System.out.println("SENSOR1 SOUTH EAST6 A");
						}
					}
				}
				
				data1+=sen1Offset;
				data2+=sen2Offset;
				data3+=sen3Offset;
				
				//check immediate row south
				if (data1 <= north1Lower || data2 <= north2Lower || data3 <= north3Lower){
					System.out.println("EXITING HERE 7");
					return false;
				}
				for (int i=-1; i<=1; i++){
					if (Robot.realNorth2.getJ() - 1 < 0){
						System.out.println("EXITING HERE 8");
						return false;
					}
					/*if (MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + 1)][Robot.realNorth2.getI() + i].getIsObstacle()){
						return false;
					}*/
				}
				System.out.println("EXITING HERE 9");
				return true;
			}
		
			/////////////////////////////////facing west
			else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
				//DATA 1 PROCESSING
				if (data1 <= north1Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth1.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsObstacle(true);
						//System.out.println("SENSOR1 WEST NORTH1 C");
					}
				}
				
				if (data1 <= north1Upper && data1 > north1Lower){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(data1/REAL_CELL); i++){
						if (Robot.realNorth1.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsObstacle(false);
						if (i == (int)(data1/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 WEST NORTH1 B");
					}
				}
				
				if (data1 > north1Upper){
					data1 = data1 - sen1Offset;
					for (int i=1; i<=(int)(north1Upper/REAL_CELL); i++){
						if (Robot.realNorth1.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth1.getJ())][Robot.realNorth1.getI() - i].setIsObstacle(false);
						//System.out.println("SENSOR1 WEST NORTH1 A");
					}
				}
				
				
				//DATA2 PROCESSING
				if (data2 <= north2Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth2.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsObstacle(true);
						//System.out.println("SENSOR1 WEST NORTH2 C");
					}
				}
				
				if (data2 <= north2Upper && data2 > north2Lower){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(data2/REAL_CELL) + 1; i++){
						if (Robot.realNorth2.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsObstacle(false);
						if (i == (int)(data2/REAL_CELL) + 1){
							MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 WEST NORTH2 B");
					}
				}
				
				if (data2 > north2Upper){
					data2 = data2 - sen2Offset;
					for (int i=1; i<=(int)(north2Upper/REAL_CELL); i++){
						if (Robot.realNorth2.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth2.getJ())][Robot.realNorth2.getI() - i].setIsObstacle(false);
						//System.out.println("SENSOR1 WEST NORTH2 A");
					}
				}
				
				//DATA3 PROCESSING
				if (data3 <= north3Lower){ 
					for (int i=1; i<=1; i++){
						if (Robot.realNorth3.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsObstacle(true);
						//System.out.println("SENSOR1 WEST NORTH3 C");
					}
				}
				
				if (data3 <= north3Upper && data3 > north3Lower){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(data3/REAL_CELL); i++){
						if (Robot.realNorth3.getI() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsObstacle(false);
						if (i == (int)(data3/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 WEST NORTH3 B");
					}
				}
				
				if (data3 > north3Upper){
					data3 = data3 - sen3Offset;
					for (int i=1; i<=(int)(north3Upper/REAL_CELL); i++){
						if (Robot.realNorth3.getI() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realNorth3.getJ())][Robot.realNorth3.getI() - i].setIsObstacle(false);
						//System.out.println("SENSOR1 WEST NORTH3 A");
					}
				}
				
				
				
				//DATA4 PROCESSING
				if (data4 <= west4Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest4.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 WEST WEST4 C");
					}
				}
				
				if (data4 <= west4Upper && data4 > west4Lower){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(data4/REAL_CELL); i++){
						if (Robot.realWest4.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsObstacle(false);
						if (i == (int)(data4/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 WEST WEST4 B");
					}
				}
				
				if (data4 > west4Upper){
					data4 = data4 - sen4Offset;
					for (int i=1; i<=(int)(west4Upper/REAL_CELL); i++){
						if (Robot.realWest4.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest4.getJ() - i)][Robot.realWest4.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 WEST WEST4 A");
					}
				}
				
				
				//DATA5 PROCESSING
				if (data5 <= west5Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realWest5.getJ() - i < 0){
							break;
						}
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsObstacle(true);
						//System.out.println("SENSOR1 WEST WEST5 C");
					}
				}
				
				if (data5 <= west5Upper && data5 > west5Lower){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(data5/REAL_CELL); i++){
						if (Robot.realWest5.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsObstacle(false);
						if (i == (int)(data5/REAL_CELL)){
							MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsObstacle(true);
						}
						//System.out.println("SENSOR1 WEST WEST5 B");
					}
				}
				
				if (data5 > west5Upper){
					data5 = data5 - sen5Offset;
					for (int i=1; i<=(int)(west5Upper/REAL_CELL); i++){
						if (Robot.realWest5.getJ() - i < 0){
							break;
						}
						//set everything in between as explored and empty
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsExplored(true, true);
						MapLeft.cellArray[19 - (Robot.realWest5.getJ() - i)][Robot.realWest5.getI()].setIsObstacle(false);
						//System.out.println("SENSOR1 WEST WEST5 A");
					}
				}
				
				
				//DATA6 PROCESSING
				if (data6 <= east6Lower){
					for (int i=1; i<=1; i++){
						if (Robot.realEast6.getJ() + 3 > 19){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() + 3)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + 3)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + 3)][Robot.realEast6.getI()].setIsObstacle(true);
							//System.out.println("SENSOR1 WEST EAST6 C");
						}
					}
				}
				
				if (data6 > east6Lower && data6 <= east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(data6/REAL_CELL) + 1; i++){
						if (Robot.realEast6.getJ() + i > 19){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].setIsObstacle(false);
							if (i == (int)(data6/REAL_CELL) + 1){
								MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].setIsObstacle(true);
							}
							//System.out.println("SENSOR1 WEST EAST6 B");
						}
					}
				}
				
				if (data6 > east6Upper){
					data6 = data6 - sen6Offset;
					for (int i=1; i<=(int)(east6Upper/REAL_CELL); i++){
						if (Robot.realEast6.getJ() + i > 19){
							break;
						}
						if(!MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].isSetBySR()){
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].setIsExplored(true, false);
							MapLeft.cellArray[19 - (Robot.realEast6.getJ() + i)][Robot.realEast6.getI()].setIsObstacle(false);
							//System.out.println("SENSOR1 WEST EAST6 A");
						}
					}
				}
				
				
				//printObstacleMaze();
				
				data1+=sen1Offset;
				data2+=sen2Offset;
				data3+=sen3Offset;
				
				//check immediate row west
				if (data1 <= north1Lower || data2 <= north2Lower || data3 <= north3Lower){
					System.out.println("EXITING HERE 10");
					return false;
				}
				for (int i=-1; i<=1; i++){
					if (Robot.realNorth2.getI() - 1 < 0){
						System.out.println("EXITING HERE 11");
						return false;
					}
					/*if (MapLeft.cellArray[19 - (Robot.realNorth2.getJ() + 1)][Robot.realNorth2.getI() + i].getIsObstacle()){
						return false;
					}*/
				}
				System.out.println("EXITING HERE 12");
				return true;
			}
			
			/////////////////////////wrong direction
			else{
				System.out.println("Wrong direction.");
			}
		}
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			System.out.println("Array is out of bounds nowwwww");
		}
		
		catch(Exception e){
			e.printStackTrace();
			System.out.println("Something else in general went wrong");
		}
		return true;
		
	
	}
	
	//west sensor
	public boolean goForwardRealSensor2(String message){
		
		dataValues = getSensorValues(message);
		data1 = dataValues[0];
		data2 = dataValues[1];
		data3 = dataValues[2];
		data4 = dataValues[3];
		data5 = dataValues[4];
		data6 = dataValues[5];
		
		System.out.println("goForwardRealSensor2 data4: "+data4);
		System.out.println("goForwardRealSensor2 data5: "+data5);
		
		//return true if no wall 
		//return false if there is a wall
		
		//if distance between sensor and virtual contour is exactly or slightly more than 10cm
		if (data4<=west4Lower || data5<=west5Lower){
			System.out.println("WALL/OBSTACLE AT THE LEFT SIDE");
			return false;
		}
		
		//robot facing north
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			//System.out.println("RealWestI: "+Robot.realWest4.getI());
			if (Robot.realWest4.getI() - 1 < 0 || Robot.realWest5.getI() - 1 < 0){
				System.out.println("SENSOR2 NORTH FALSE");
				return false;
			}
			
			if (MapLeft.cellArray[19 - (Robot.getJ())][Robot.getI() - 2].getIsObstacle()){
				System.out.println("SENSOR2 NORTH WALL MIDDLE");
				return false;
			}

			System.out.println("SENSOR2 NORTH TRUE");
			return true;
		}
		
		//robot facing east
		else if(Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			if (Robot.realWest4.getJ() + 1 > 19 || Robot.realWest5.getJ() + 1 > 19){
				System.out.println("SENSOR2 EAST FALSE");
				return false;
			}
			
			if (MapLeft.cellArray[19 - (Robot.getJ() + 2)][Robot.getI()].getIsObstacle()){
				System.out.println("SENSOR2 EAST WALL MIDDLE");
				return false;
			}
			

			System.out.println("SENSOR2 EAST TRUE");
			return true;
		}
		
		//robot facing south
		else if(Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			if (Robot.realWest4.getI() + 1 > 14 || Robot.realWest5.getI() + 1 > 14){
				System.out.println("SENSOR2 SOUTH FALSE");
				return false;
			}
			
			
			if (MapLeft.cellArray[19 - (Robot.getJ())][Robot.getI() + 2].getIsObstacle()){
				System.out.println("SENSOR2 SOUTH WALL MIDDLE");
				return false;
			}
			
		
			System.out.println("SENSOR2 SOUTH TRUE");
			return true;
		}
		
		//robot facing west
		else{
			if (Robot.realWest4.getJ() - 1 < 0 || Robot.realWest5.getJ() - 1 < 0){
				System.out.println("SENSOR2 WEST FALSE");
				return false;
			}

			if (MapLeft.cellArray[19 - (Robot.getJ() - 2)][Robot.getI()].getIsObstacle()){
				System.out.println("SENSOR2 WEST WALL MIDDLE");
				return false;
			}
			
			System.out.println("SENSOR2 WEST TRUE");
			return true;
		}
		
		
	}
	
	public static double[] getSensorValues(String message){//PREFIX DATA1 DATA2 DATA3 DATA4 DATA5 DATA6
		
		message = message.trim();
		
		//System.out.println("getSensorValues: message: "+message);
		String[] messageArray = message.split(" ");
		//double[] dataValues = new double[6];
		
		for (int i=1; i<=6; i++){
			try{
				double dataVal = Double.parseDouble(messageArray[i]);
				//System.out.println("DataValueeee: "+dataVal);
				dataValues[i-1] = dataVal;
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
			
		}
		data1 = dataValues[0];
		data2 = dataValues[1];
		data3 = dataValues[2];
		data4 = dataValues[3];
		data5 = dataValues[4];
		data6 = dataValues[5];
		/*System.out.println(dataValues[0]);
		System.out.println(dataValues[1]);
		System.out.println(dataValues[2]);
		System.out.println(dataValues[3]);
		System.out.println(dataValues[4]);
		System.out.println(dataValues[5]);*/
		return dataValues;
	}
	
	//true means can calibrate
	//false means cannot calibrate
	
	
	
	//i and j wrt to bottom left. i is horizontal and j is vertical.
	public static boolean canCalibrate(int i, int j){ //check for either wall or obstacle
		//System.out.println("can calibrate() data4 = "+ data4);
		//System.out.println("can calibrate() data5 = "+ data5);
		if ((j < 0 || j > 19 || i < 0 || i > 14 || (data4<=west4Lower && data5<=west5Lower && Math.abs(data4 - data5) <= 6))&&(data4!=18 && data5!=13)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean canFrontCalibrate(int i, int j){ //check for either wall or obstacle
		if (j < 0 || j > 19 || i < 0 || i > 14 || (data1<=north1Lower && data3<=north3Lower)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void printObstacleMaze(){
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				MapLeft.cellArray[i][j].printObstacle();
			}
			System.out.println();
		}
	}
	
}
