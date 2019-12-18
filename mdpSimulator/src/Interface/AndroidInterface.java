package Interface;

import static Map.MapApp.nm;

import java.util.Scanner;

public class AndroidInterface {
	
    public static String returnMessage() {

        String message;

        do {

            message = nm.receiveMessage();

        } while (message == null);

        return message;

    }
    
    public static void main(String[] args) throws InterruptedException {
        nm.openConnection();
        System.out.println(returnMessage());
    }
}
