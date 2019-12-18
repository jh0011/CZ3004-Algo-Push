package Network;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * 
 * When sending to arduino, put ~ as the message.
 */

public class NetworkManager {
	
	//types of messages that will be passed
	public static final String EX_START = "EXPLORATION";       		// Android --> PC
    public static final String FP_START = "FASTESTPATH";       		// Android --> PC
    public static final String WP_INDEX = "WPINDEX";       			// Android --> PC
    public static final String BOT_LOC = "BOTLOC";         			// Android --> PC
    public static final String MAP_STRINGS = "MAP";         		// PC --> Android
    public static final String BOT_POS = "BOTPOS";         			// PC --> Android
    public static final String BOT_START = "S";     				// PC --> Arduino
    public static final String INSTRUCTIONS = "";      				// PC --> Arduino
    public static final String CALIBRATE = "";      				// PC --> Arduino 
    public static final String BOT_START_FP = "F";     				// PC --> Arduino
    public static final String SENSOR_DATA = "SENDATA";             // Arduino --> PC
    public static final String CALI_DONE = "CALIBRATIONDONE";       // Arduino --> PC
    public static final String BOT_DONE = "BOTDONE";                // Arduino --> PC
    public static final String BOT_READY = "BOTREADY";              // Arduino --> PC
    
    //message headers for RPi to know where to forward the message
    public static final String TO_ARDUINO = "A";
    public static final String TO_ANDROID = "B";
    
    //server IP address
    private String serverIP = "192.168.1.10"; //192.168.12.12 (RPi ip address) 
    //port number
    private int portNum = 63; //1273 (RPi port Num)
    
    private static NetworkManager netMgr = null;
    private static Socket connection = null;
    
    private BufferedReader reader;
    private BufferedWriter writer;
    
    
    
    public NetworkManager getNetMgr(){
    	if (netMgr == null){
    		netMgr = new NetworkManager();
    	}
    	return netMgr;
    }
    
    
    public void openConnection(){
    	System.out.println("Waiting for connection...");
    	try{
            connection = new Socket(serverIP, portNum);
            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(connection.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("openConnection() --> " + "Connection established successfully!");
    		return;
    		
    	}
    	catch(UnknownHostException e1){
    		System.out.println("openConnection() --> UnknownHostException");
    		closeConnection();
    	}
    	catch(IOException e2){
    		System.out.println("openConnection() --> IOException");
    		closeConnection();
    	}
    	catch(Exception e3){
    		System.out.println("openConnection() --> Some Exception");
            System.out.println(e3.toString());
            closeConnection();
    	}
    	
    	System.out.println("Failed to establish connection!");
    }
    
    
    
    public void closeConnection(){
    	System.out.println("Closing connection...");
        try {
            reader.close();
            if (connection != null) {
            	connection.close();
            	connection = null;
            }
            System.out.println("Connection closed!");
        } 
        catch (IOException e1) {
            System.out.println("closeConnection() --> IOException");
        } 
        catch (NullPointerException e2) {
            System.out.println("closeConnection() --> NullPointerException");
        } 
        catch (Exception e3) {
            System.out.println("closeConnection() --> Some Exception");
            System.out.println(e3.toString());

        }
    }
    
    
    
    public void sendMessage(String msg, String msgType){
    	//System.out.println("Sending a message...");
        try {
            String outputMsg;
            
            //if sending message to android
            if (msgType.equals(MAP_STRINGS) || msgType.equals(BOT_POS)){
                outputMsg = TO_ANDROID + " " + msgType + " " + msg + "\n";
            } 
            
            //if sending message to arduino without prefix
            else if (msgType.equals(INSTRUCTIONS)){
            	outputMsg = TO_ARDUINO + msgType + " " + msg + "\n";
            }
            
            //if sending message to arduino with prefix
            else{
            	outputMsg = TO_ARDUINO + " " + msgType + msg + "\n";
            }
            System.out.println("Sending out message: " + outputMsg);
            writer.write(outputMsg);
            writer.newLine();
            writer.flush();
        } 
        catch (IOException e1) { //did not manage to write the message
            System.out.println("sendMessage() --> IOException");
            //closeConnection();
        } 
        catch (Exception e2) {
            System.out.println("sendMessage() --> Some Exception");
            System.out.println(e2.toString());
            //closeConnection();
        }
    }

    
    public String receiveMessage() {

        try {

            String outputFromRpi = null;



            if (reader.ready()) {

                String input = reader.readLine();

                if (input != "" && input.length() > 0) {

                    System.out.println("Message from rpi: " + input);

                    outputFromRpi = input;

                }

            }

            return outputFromRpi;

        } catch (IOException e1) {

            System.out.println("1.3");

            System.out.println("receiveMessage() --> IOException");
            closeConnection();

        } catch (Exception e2) {

            System.out.println("1.4");

            System.out.println("receiveMessage() --> Some Exception");

            System.out.println(e2.toString());
            closeConnection();

        }


        System.out.println("1.5");

        return null;

    }
    
    
    
    public boolean isConnected() {
        return connection.isConnected();
    }
    
    
}
