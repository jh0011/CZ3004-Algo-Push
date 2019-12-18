package Map;

import Algorithm.Exploration;
import Algorithm.FPReal;
import Algorithm.FastestPath;
import Algorithm.FastestPathStepByStep;
import Map.MapLeft;
import Network.NetworkManager;

import javax.swing.*;
import Robot.Robot;
import Sensor.Sensor;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class BtnRight extends JPanel{
	
	protected static JButton loadMap = new JButton();
	protected static JButton rotateRobot = new JButton();
	protected static JButton exploration = new JButton();
	protected static JButton fastestPath = new JButton();
	protected static JButton manualMove = new JButton();
	protected static JButton genMapFile = new JButton();
	protected static JButton reset = new JButton();
	protected static JButton uncoverAll = new JButton(); 
	protected static JButton hideAll = new JButton();
	protected static JButton exportMap = new JButton();
	protected static JButton manualTerminate = new JButton();
		
	protected static JComboBox userSpeed;
	protected static JComboBox userCoverage;
	protected static JTextField userTime;
	protected static JTextField waypointLocation;
	protected static JTextField robotLocation;
		
		
	protected static String[] speedValues = {"Select speed", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	protected static String[] coverageValues = {"Select coverage %", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
	public static int corX = 11; //waypoint i-index from the bottom left
	public static int corY = 3; //waypoint j-index from the bottom left
	
	protected static int rotateCount = 0;
	protected static int waypointCount = 0;
	protected static int fastestPathCount = 0;
	
	
	
	public void addButtons(){	
		
		
		loadMap = new JButton("Load Map");
		loadMap.setBackground(MapLeft.colourYellow);
		loadMap.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		loadMap.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					loadMapEvent();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Something went wrong!");
				}
			}
		});
		
		rotateRobot = new JButton("Rotate the Robot");
		rotateRobot.setBackground(MapLeft.colourYellow);
		rotateRobot.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		rotateRobot.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				Graphics g = null;
				rotateCount++;
				rotateRobotEvent(g);
			}
		});
		exploration = new JButton("Start Exploration");
		exploration.setBackground(MapLeft.colourYellow);
		exploration.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		exploration.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				fastestPathCount++;
				explorationEvent();
			}
		});
		
		uncoverAll = new JButton("Uncover all");
		uncoverAll.setBackground(MapLeft.colourYellow);
		uncoverAll.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		uncoverAll.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				uncoverAllEvent();
			}
		});
		hideAll = new JButton("Hide all");
		hideAll.setBackground(MapLeft.colourYellow);
		hideAll.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		hideAll.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				hideAllEvent();
			}
		});
		
		fastestPath = new JButton("Start Fastest Path");
		fastestPath.setBackground(MapLeft.colourYellow);
		fastestPath.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		fastestPath.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				fastestPathEvent();
			}
		});
		
		manualMove = new JButton("Manual move");
		manualMove.setBackground(MapLeft.colourYellow);
		manualMove.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		manualMove.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				manualMoveEvent();
			}
		});
		genMapFile = new JButton("Map desc. file");
		genMapFile.setBackground(MapLeft.colourYellow);
		genMapFile.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		genMapFile.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				genMapFileEvent();
			}
		});
		
		reset = new JButton("Reset");
		reset.setBackground(MapLeft.colourYellow);
		reset.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		reset.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				resetEvent();
			}
		});
		
		exportMap = new JButton("Export map");
		exportMap.setBackground(MapLeft.colourYellow);
		exportMap.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		exportMap.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				exportMapEvent();
			}
		});
		
		manualTerminate = new JButton("Manually terminate");
		manualTerminate.setBackground(MapLeft.colourYellow);
		manualTerminate.setFont(new Font("Arial", Font.PLAIN, MapLeft.fontSize));
		manualTerminate.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				manualTerminateEvent();
			}
		});
		
		
		//initialise JTextField and JComboBox
		userSpeed = new JComboBox(speedValues);
		userSpeed.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String speed = (String)userSpeed.getSelectedItem();
				int speedLimit = Integer.parseInt(speed);
				selectSpeedEvent(speedLimit);
			}
		});
		userCoverage = new JComboBox(coverageValues);
		userCoverage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String coverage = (String)userCoverage.getSelectedItem();
				int coverageLimit = Integer.parseInt(coverage);
				selectCoverageEvent(coverageLimit);
			}
		});
		
		userTime = new JTextField("Enter time limit (min:sec): ");
		userTime.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String timeLimit = userTime.getText();
				timeLimitEvent(timeLimit);
			}
		});
		waypointLocation = new JTextField("Waypoint (x, y):  ");
		waypointLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String wpLoc = waypointLocation.getText();
				confirmWPEvent(wpLoc);
			}
		});
		
		robotLocation = new JTextField("Robot (x,y) = ");
		robotLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String robot = robotLocation.getText();
				setRobotEvent(robot);
			}
		});
	}
	
	public void loadMapEvent() throws IOException{
		MapLeft.setReadFile(true);
	    MapApp.panelLeft.drawing();
	}
	
	public static void explorationEvent(){
		//if simulator
		if (MapLeft.real_bot == false){
			Exploration.startTime = System.currentTimeMillis();
			Exploration.startTimer();
		}
		
		//if real robot
		else{
			MapApp.nm.sendMessage("~", NetworkManager.BOT_START);
			while (true){
				String message = MapApp.nm.receiveMessage();
			    String messageType = MapLeft.decodeMessageType(message);
			    if (messageType == NetworkManager.SENSOR_DATA){
			    	Exploration.startExploration = true;
			    	//send message to move the robot
			    	//MapApp.nm.sendMessage(null, "INSTR"); //checkForward
			    	Exploration.startTime = System.currentTimeMillis();
			    	Exploration.startTimer();
			    	break;
			    }
			}
		}
	}
	
	public void uncoverAllEvent(){
		MapLeft.hideAll = false;
		MapApp.panelLeft.drawing();
	}
	public void hideAllEvent(){
		MapLeft.hideAll = true;
		MapApp.panelLeft.drawing();
	}
	
	public void rotateRobotEvent(Graphics g){
		//if facing north
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH){
			Robot.setDirection(Robot.ROBOT_DIR.EAST);
			Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.EAST);
			Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.NORTH);
			Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.SOUTH);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
			Exploration.turnedLeftPrev = true;
			MapApp.panelLeft.drawing();
		}
				
		//if facing east
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST){
			Robot.setDirection(Robot.ROBOT_DIR.SOUTH);
			Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.SOUTH);
			Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.EAST);
			Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.WEST);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
			Exploration.turnedLeftPrev = true;
			MapApp.panelLeft.drawing();	
		}
		
		//if facing south
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH){
			Robot.setDirection(Robot.ROBOT_DIR.WEST);
			Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.WEST);
			Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.SOUTH);
			Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.NORTH);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
			Exploration.turnedLeftPrev = true;
			MapApp.panelLeft.drawing();
		}
		
		//if facing west
		else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST){
			Robot.setDirection(Robot.ROBOT_DIR.NORTH);
			Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.NORTH);
			Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.WEST);
			Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.EAST);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX() - MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX() + MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
			Exploration.turnedLeftPrev = true;
			MapApp.panelLeft.drawing();
		}

	}
	
	public static void fastestPathEvent(){
		FastestPath.isExploration = false;
		FastestPath.fastestPath = true;
		for (int i=0; i<MapLeft.ROWS; i++){
			for (int j=0; j<MapLeft.COLS; j++){
				MapLeft.cellArray[i][j].setIsPath(false);
			}
		}
		FastestPath fp = new FastestPath();
		
	}
	
	
	public void manualMoveEvent(){
		//move north
		if (Robot.getDirection() == Robot.ROBOT_DIR.NORTH && Robot.getY() > 2 * MapLeft.GRID_SIZE) {
			Robot.setX(Robot.getX());
			Robot.setY(Robot.getY() - MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() - MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() - MapLeft.GRID_SIZE);
			MapApp.panelLeft.drawing();
		}
		
		//move south
		else if (Robot.getDirection() == Robot.ROBOT_DIR.SOUTH && Robot.getY() < (MapLeft.ROWS - 2) * MapLeft.GRID_SIZE) {
			Robot.setX(Robot.getX());
			Robot.setY(Robot.getY() + MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX());
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX());
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY() + MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX());
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY() + MapLeft.GRID_SIZE);
			MapApp.panelLeft.drawing();
		}
		
		//move east
		else if (Robot.getDirection() == Robot.ROBOT_DIR.EAST && Robot.getX() < (MapLeft.COLS - 2) * MapLeft.GRID_SIZE) {
			Robot.setX(Robot.getX() + MapLeft.GRID_SIZE);
			Robot.setY(Robot.getY());
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX()+ MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX()+ MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX()+ MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
			MapApp.panelLeft.drawing();
		}
		//move west
		else if (Robot.getDirection() == Robot.ROBOT_DIR.WEST && Robot.getX() > 2 * MapLeft.GRID_SIZE) {
			Robot.setX(Robot.getX() - MapLeft.GRID_SIZE);
			Robot.setY(Robot.getY());
			Robot.sensorNorth1.setSenLocX(Robot.sensorNorth1.getSenLocX()- MapLeft.GRID_SIZE);
			Robot.sensorNorth1.setSenLocY(Robot.sensorNorth1.getSenLocY());
			Robot.sensorWest1.setSenLocX(Robot.sensorWest1.getSenLocX()- MapLeft.GRID_SIZE);
			Robot.sensorWest1.setSenLocY(Robot.sensorWest1.getSenLocY());
			Robot.sensorEast1.setSenLocX(Robot.sensorEast1.getSenLocX()- MapLeft.GRID_SIZE);
			Robot.sensorEast1.setSenLocY(Robot.sensorEast1.getSenLocY());
			MapApp.panelLeft.drawing();
		}
	}
	
	
	public void genMapFileEvent(){
		String p1 = genP1();
		String p2 = genP2();
		
		int sum=0;
		String result = "";
		String finalResult = "";
		String mapFile = "";
		for (int k=0; k<mapFile.length(); k=k+4){
			if (mapFile.charAt(k)=='1'){
				sum+=8;
			}
			if (mapFile.charAt(k+1)=='1'){
				sum+=4;
			}
			if (mapFile.charAt(k+2)=='1'){
				sum+=2;
			}
			if (mapFile.charAt(k+3)=='1'){
				sum+=1;
			}
			
			result = Integer.toHexString(sum); //convert to hexadecimal
			finalResult = finalResult.concat(result);
			sum=0; //set the sum to zero for the next iteration
		}
		System.out.println("final result: "+finalResult);
	}
	
	public String genP1(){
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
	
	public String genP2(){
		//within the explored grids,
		//bit 0: empty grid
		//bit 1: obstacle grid
		String mapFile = "";
		for (int i=MapLeft.ROWS - 1; i>=0; i--){
			for (int j=0; j<MapLeft.COLS; j++){
				if (MapLeft.cellArray[i][j].getIsExplored()){
					if (MapLeft.cellArray[i][j].getIsObstacle()){
						mapFile.concat("1");
					}
					else{
						mapFile.concat("0");
					}
				}
			}
		}
		if (mapFile.length() % 8 != 0){
			int remainder = 8 - (mapFile.length() % 8);
			for (int k=0; k<remainder; k++){ //add padding bits at the end of the string
				mapFile.concat("1");
			}
		}
		
		return mapFile;
		
	}
	
	public void resetEvent(){
		//reset everything to the beginning
		//bring robot to beginning
		//manualTerminateEvent(); 
		
		Robot.setX(Robot.INIT_BOT_X);
		Robot.setY(Robot.INIT_BOT_Y);
		Robot.sensorNorth1.setSenLocX(Robot.INIT_BOT_X - 25);
		Robot.sensorNorth1.setSenLocY(Robot.INIT_BOT_Y - 75);
		Robot.sensorWest1.setSenLocX(Robot.INIT_BOT_X - 75);
		Robot.sensorWest1.setSenLocY(Robot.INIT_BOT_Y - 25);
		Robot.sensorEast1.setSenLocX(Robot.INIT_BOT_X + 25);
		Robot.sensorEast1.setSenLocY(Robot.INIT_BOT_Y - 25);
		Robot.setDirection(Robot.ROBOT_DIR.NORTH);
		Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.NORTH);
		Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.WEST);
		Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.EAST);
		MapApp.panelLeft.drawing();
		
		//re-colour everything back to normal
		waypointCount = 0;
		fastestPathCount = 0;
		Exploration.startExploration=false;
		Exploration.coverageLimit = 10000000;
		Exploration.timeLimit = 1000000;
		Exploration.speed = 200;
		FastestPath.fastestPath = false;
		
		
			
		/*reset the timer*/
		Exploration.newTimer();

	}
	public void exportMapEvent(){
		//MapApp.nm.sendMessage("~", "BOTREVERSE");
	}
	
	public void manualTerminateEvent(){
		//make the robot come back to start zone
	    //reset direction and coordinates of the sensors
		
		Robot.sensorNorth1.setSenLocX(Robot.INIT_BOT_X - 25);
		Robot.sensorNorth1.setSenLocY(Robot.INIT_BOT_Y - 75);
		Robot.sensorWest1.setSenLocX(Robot.INIT_BOT_X - 75);
		Robot.sensorWest1.setSenLocY(Robot.INIT_BOT_Y - 25);
		Robot.sensorEast1.setSenLocX(Robot.INIT_BOT_X + 25);
		Robot.sensorEast1.setSenLocY(Robot.INIT_BOT_Y - 25);
		Robot.setDirection(Robot.ROBOT_DIR.NORTH);
		Robot.sensorNorth1.setDir(Sensor.SENSOR_DIR.NORTH);
		Robot.sensorWest1.setDir(Sensor.SENSOR_DIR.WEST);
		Robot.sensorEast1.setDir(Sensor.SENSOR_DIR.EAST);
		FastestPath.isExploration = true;
		FastestPath.startNode = MapLeft.cellArray[Robot.getY() / MapLeft.GRID_SIZE]
				[Robot.getX() / MapLeft.GRID_SIZE];
		FastestPath.goalNode1 = MapLeft.cellArray[17][1];
		FastestPath.goalNode2 = MapLeft.cellArray[18][1];
		FastestPath fp = new FastestPath();
		Exploration.endTimer();
		//FastestPath.endTimer();
		/*reset the timer*/
		Exploration.newTimer();
		
		MapApp.panelLeft.drawing();
		

	}
	
	public void timeLimitEvent(String limit){
		//finish exploration within this time
		String timeLimit = limit.replace("Enter time limit (min:sec): ","");
		String[] timeL = timeLimit.split(":");
		String x = timeL[0].trim();
		String y = timeL[1].trim();
		int timex = Integer.parseInt(x);
		int timey = Integer.parseInt(y);
		Exploration.timeLimit = (timex * 60) + timey;
		
	}
	
	public void confirmWPEvent (String wpLoc){
		String loc = wpLoc.replace("Waypoint (x, y):  ", " ");
		String[] locWP = loc.split(",");
		String x = locWP[0].trim();
		String y = locWP[1].trim();
		corX = Integer.parseInt(x);
		corY = Integer.parseInt(y);
		waypointCount++;
		MapApp.panelLeft.drawing();
		
	}
	

	
	public void selectSpeedEvent(int speed){
		int speedLimit = 1000 / speed;
		Exploration.speed = speedLimit;
		
	}
	
	public void selectCoverageEvent(int coverage){
		//set the coverage limit in Exploration
		Exploration.coverageLimit = coverage;
		
	}
	
	public void setRobotEvent(String location){
		String loc = location.replace("Robot (x,y) = ", " ");
		String[] locRobot = loc.split(",");
		String x = locRobot[0].trim();
		String y = locRobot[1].trim();
		int robotX = Integer.parseInt(x);
		int robotY = Integer.parseInt(y);
		
		Robot.setX((robotX * MapLeft.GRID_SIZE) + 25);
		Robot.setY((20 * MapLeft.GRID_SIZE) - (robotY * MapLeft.GRID_SIZE) - 25);
		int indexx = Robot.getX();
		int indexy = Robot.getY();
		Robot.sensorNorth1.setSenLocX(Robot.getX());
		Robot.sensorNorth1.setSenLocY(Robot.getY() - MapLeft.GRID_SIZE);
		Robot.sensorWest1.setSenLocX(Robot.getX() - MapLeft.GRID_SIZE);
		Robot.sensorWest1.setSenLocY(Robot.getY());
		Robot.sensorEast1.setSenLocX(Robot.getX() + MapLeft.GRID_SIZE);
		Robot.sensorEast1.setSenLocY(Robot.getY());
		MapApp.panelLeft.drawing();
	}

}
	
	

