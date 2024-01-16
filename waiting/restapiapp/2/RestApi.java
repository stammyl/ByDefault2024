package com.vodafone.restapiapp;

import java.util.List;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class  RestApi{

	private static HttpServer server;
	private static final DatabaseThread databaseThread = new DatabaseThread();
    //public static void main(String[] args) throws IOException {

	public static void main(String[] args)
	{
		try{
			System.out.println("I am a Geek");
			StartServer();
			
			System.out.println("Server started on port 8081");
		
			// Wait for user input before exiting			
			Scanner scanner = new Scanner(System.in);
			while (true) {
				String userInput = scanner.nextLine();
				if ("exit".equalsIgnoreCase(userInput.trim())) {
					break;
				}
			}
			StopServer();
		}		
		catch (Exception ex){
			System.out.println("An error occured - stopping server!");
			ex.printStackTrace();
		}
		// catch(IOException ex){
			// System.out.println("An error occured - stopping server!");
			// ex.printStackTrace();
		// }
	}    

	public static void StartServer() throws IOException {
        // Create an HTTP server on port 8080
        RestApi.server = HttpServer.create(new InetSocketAddress(8081), 0);
		
        // Create a context for the "/api/hello" endpoint
        RestApi.server.createContext("/api/hello", new HelloHandler());
        RestApi.server.createContext("/api/greet", new GreetHandler());
        //RestApi.server.createContext("/api/testGet", new GetInstancesHandler());
        //RestApi.server.createContext("/api/mockInsert", new CreateAndInsertHandler());
        RestApi.server.createContext("/api/db", new DbHandler());

		RestApi.server.setExecutor(null);
        // Start the server
        RestApi.server.start();   

		//start database thread!
		databaseThread.start();
		try {
            // Waiting for thread to finish
            databaseThread.join();            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		
    }
	
	public static void StopServer()throws IOException{
		// Stop the server
        RestApi.server.stop(0);
        System.out.println("Server stopped.");
	}

    public static Connection createConnection() throws SQLException {
    // JDBC URL for H2 in-memory database
    String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    // Database credentials
    String user = "sa";
    String password = "password";

    // Create a connection
    Connection connection = DriverManager.getConnection(url, user, password);

    return connection;
}

	
	private static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
			System.out.println("Call has been made on url:'/api/hello' ");
            // Define the response
            String response = "Hello Christakis from the REST API!";

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.length());

            // Write the response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
	
	private static class GreetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Call has been made on url:'/api/greet'");
            // Define the response
            String response = "Greetings from the REST API!";

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.length());

            // Write the response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }	

	private static class DbHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Call has been made on url:'/api/db' ");
		
		try{
			List<DockerImage> imagesCopy = new ArrayList<DockerImage>();
                
			DockerImage d1  = new DockerImage("nginx", "1", "running");
			DockerImage d2  = new DockerImage("linux", "2", "stopped");
			imagesCopy.add(d1);
			imagesCopy.add(d2);

			// Process the images and save them to the database using the already started Thread
			databaseThread.SaveImages(null, imagesCopy);

			System.out.println("Press any key to continue...");
			System.in.read();
			System.out.println("Finito!"); 
			
		}		  
		
		catch (SQLException e) {
            e.printStackTrace();	
			System.out.println("Exception occured when try to insert images! ");			
        }
		
		String response = "Db response!";

		// Set the response headers
		exchange.getResponseHeaders().set("Content-Type", "text/plain");
		exchange.sendResponseHeaders(200, response.length());

		// Write the response to the output stream
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response.getBytes());
		}			
        
    }
}


}