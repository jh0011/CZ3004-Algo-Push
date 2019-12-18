package Interface;

import java.util.Scanner;
import Map.MapApp;
import Map.MapLeft;
import Network.NetworkManager;
import static Map.MapApp.nm;


public class ArduinoInterface {

    public static void checkReady() { 
    	nm.sendMessage("1", NetworkManager.INSTRUCTIONS); }



    public static void moveForward() {

        nm.sendMessage("3", NetworkManager.INSTRUCTIONS);

    }



    public static void moveBackward() {

        nm.sendMessage("6", NetworkManager.INSTRUCTIONS);

    }



    public static void turnRight() {

        nm.sendMessage("5", NetworkManager.INSTRUCTIONS);

    }



    public static void turnLeft() {

        nm.sendMessage("4", NetworkManager.INSTRUCTIONS);

    }



    public static void turnLeft180() {

        nm.sendMessage("7", NetworkManager.INSTRUCTIONS);

    }



    public static void turnRight180() { 
    	nm.sendMessage("8", NetworkManager.INSTRUCTIONS); 
    }



    public static void returnSensorData() {

        nm.sendMessage("2", NetworkManager.INSTRUCTIONS);

    }



    public static void calibrateFront() {

        nm.sendMessage("11", NetworkManager.INSTRUCTIONS);

    }
    
    public static void calibrateStep() {

        nm.sendMessage("13", NetworkManager.INSTRUCTIONS);

    }



    public static void calibrateLeft() {

        nm.sendMessage("12", NetworkManager.INSTRUCTIONS);

    }



    public static void calibrateTurnLeft() {

        nm.sendMessage("10", NetworkManager.INSTRUCTIONS);

    }
    
    public static void stopBot(){
    	nm.sendMessage("9", NetworkManager.INSTRUCTIONS);
    }
    
    
    public static void sendFastestPath(String fp) {

        nm.sendMessage(fp, NetworkManager.INSTRUCTIONS);

    }
    
    public static void botListening(){
    	nm.sendMessage("14", NetworkManager.INSTRUCTIONS);
    }
    
    public static void moveMultiple(double distance){
    	nm.sendMessage("3:"+distance+"~", NetworkManager.INSTRUCTIONS);
    }


    public static String returnMessage(String receivedMesssge) {
    	
        String message;

        int countTries = 0;

        do {

            //System.out.print(String.valueOf(countTries)+" : ");

            message = nm.receiveMessage();

            try {

                Thread.sleep(0); //200 //40

            } catch (InterruptedException EX) {

                System.out.println(EX.getMessage());

            }

            countTries++;
            	//MapLeft.decodeMessageType(message) // && countTries != 250
        } while ((message == null || !receivedMesssge.equals(MapLeft.decodeMessageType(message)))); //90 

        return message;

    }



    public static void main(String[] args) throws InterruptedException {



        nm.openConnection();



        int opt = 0;

        Scanner scanner = new Scanner(System.in);

        while (opt != 11) {

            System.out.printf("%d. %s\n", 0, "CHECK READY");

            System.out.printf("%d. %s\n", 1, "MOVE FORWARD");

            System.out.printf("%d. %s\n", 2, "MOVE BACKWARD");

            System.out.printf("%d. %s\n", 3, "ROTATE LEFT");

            System.out.printf("%d. %s\n", 4, "ROTATE RIGHT");

            System.out.printf("%d. %s\n", 5, "ROTATE LEFT 180");

            System.out.printf("%d. %s\n", 6, "ROTATE RIGHT 180");

            System.out.printf("%d. %s\n", 7, "RETURN SENSOR DATA");

            System.out.printf("%d. %s\n", 8, "CALIBRATE FRONT");

            System.out.printf("%d. %s\n", 9, "CALIBRATE LEFT");

            System.out.printf("%d. %s\n", 10, "TURN LEFT & CALIBRATE");

            System.out.printf("%d. %s\n", 11, "EXIT");

            System.out.print("ENTER YOUR OPTION : ");

            opt = scanner.nextInt();



            switch (opt) {

                case 0:

                    checkReady();

                    System.out.println(returnMessage("BOTREADY"));

                    break;

                case 1:

                    moveForward();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 2:

                    moveBackward();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 3:

                    turnLeft();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 4:

                    turnRight();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 5:

                    turnLeft180();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 6:

                    turnRight180();

                    System.out.println(returnMessage("BOTDONE"));

                    break;

                case 7:

                    returnSensorData();

                    System.out.println(returnMessage("SENDATA"));

                    break;

                case 8:

                    calibrateFront();

                    System.out.println(returnMessage("CALIBRATIONDONE"));

                    break;

                case 9:

                    calibrateLeft();

                    System.out.println(returnMessage("CALIBRATIONDONE"));

                    break;

                case 10:

                    calibrateTurnLeft();

                    System.out.println(returnMessage("CALIBRATIONDONE"));

                    break;

                case 11:

                    break;

                default:

                    System.out.println("Invalid Entry.");

                    break;



            }



        }
        
        nm.closeConnection();

    }

}