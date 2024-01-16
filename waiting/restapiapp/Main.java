package com.vodafone.restapiapp;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
		
		try{
			 // Start the RestApi process as a separate process
			startRestApiProcess();

			// Your main logic here
			System.out.println("Main application is running. Insert 'exit' to stop.");

			// Wait for user input before exiting
			//new Scanner(System.in).nextLine();
			// Wait for user input or termination signal
			Scanner scanner = new Scanner(System.in);
			while (true) {
				String userInput = scanner.nextLine();
				if ("exit".equalsIgnoreCase(userInput.trim())) {
					break;
				}
			}
			stopRestApiProcess();
		}		
		catch (Exception e){
			System.out.println("Another Exception occured. Stopping server...");
            stopRestApiProcess();
            System.out.println("Server stopped.");
			e.printStackTrace();
		}
		// catch (InterruptedException e) {
            // // Handle interruption and stop the server gracefully
            // System.out.println("Server interrupted. Stopping...");
            // stopRestApiProcess();
            // System.out.println("Server stopped.");
        // }
    }

    private static void startRestApiProcess() {
        try {
            RestApi.StartServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	private static void stopRestApiProcess() {
        try {
            RestApi.StopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
