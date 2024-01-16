package com.vodafone.restapiapp;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Scanner;

// import java.io.OutputStream;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.Statement; 
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;


public class  RestApi{

	private static HttpServer server;
	//private static final DatabaseThread databaseThread = new DatabaseThread();
    //public static void main(String[] args) throws IOException {
     public static void StartServer() throws IOException {
		 
		 Log(":)");		 
		 Log("Starting RESTAPI listener....");
		 Log(":)");
		 
        // Create an HTTP server on port 8080
        RestApi.server = HttpServer.create(new InetSocketAddress(8081), 0);
		
		
		Log("Connecting to Docker....");
		//Creat ExecutoThread Instance
		DockerClient dc = RestApi.getDockerClient();
		//ExecutorThread executorThread = new ExecutorThread(dc);
		ExecutorThread executorThread = new ExecutorThread(dc,"ee902dfe26af",true);
		
		Log("Endpoints ok....");
        // Create a context for the "/api/hello" endpoint
        RestApi.server.createContext("/api/hello", new HelloHandler());
        //RestApi.server.createContext("/api/greet", new GreetHandler());
        //RestApi.server.createContext("/api/testGet", new GetInstancesHandler());
        //RestApi.server.createContext("/api/mockInsert", new CreateAndInsertHandler());
        RestApi.server.createContext("/api/create", new CreateTables());
        RestApi.server.createContext("/api/insertMock", new InsertMockImages());
        RestApi.server.createContext("/api/getIamges", new GetImages());
		
        RestApi.server.createContext("/api/startExecutor", new ExecutorControlHandler(executorThread));
        RestApi.server.createContext("/api/stopExecutor",  new ExecutorControlHandler(executorThread));        
        RestApi.server.createContext("/api/startCont",  new ExecutorControlHandler(dc,"ee902dfe26af",true));//RestApi.server.createContext("/api/start",  new ExecutorControlHandler(executorThread));
        RestApi.server.createContext("/api/stopCont",  new ExecutorControlHandler(dc,"ee902dfe26af",false));

		RestApi.server.setExecutor(null);
        // Start the server
        RestApi.server.start();
        System.out.println("Server started on port 8081");

		// Shutdown hook to close the database thread
        // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // //DbHandler.shutdown();
            // //server.stop(0);
			// RestApi.server.StopServer();
            // System.out.println("Server stopped.");
        // }));		
		
    }	
	public static void StopServer()throws IOException{
		// Stop the server
        RestApi.server.stop(0);
        System.out.println("Server stopped.");
	}
	
	//Docker
	private static DockerClient getDockerClient(){			
								
			DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
			builder.withDockerHost("tcp://localhost:2375");
			DockerClient dc = DockerClientBuilder.getInstance(builder).build();
			dc.versionCmd().exec();
			return dc;
	}
	
	//Handlers
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
	
	private static class CreateTables implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange){
			try{		
				//Class.forName("com.mysql.cj.jdbc.Driver");			
				//Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");
				//Db.deleteImagesTable(connection);
				
				System.out.println("Create and Delete started!");
				
				//if (GetAnswer("Want to delete table?")){
					Db.deleteImagesTable();
					Db.deleteImagesTable();				
					System.out.println("Table deletion finished!");
				//}
				//if (GetAnswer("Want to create tables?")){
					Db.createCalcsTable();
					Db.createImagesTable();			
					System.out.println("Table creation finished!");
				//}
				// Send a response
				sendResponse(exchange, 200, "Table created and mock images inserted successfully.");
				// Close the database connection
				//connection.close();

			} 
			catch (Exception e) {
				try{
					System.out.println("Internal Exception occured");
					e.printStackTrace();
					sendResponse(exchange, 500, "Internal Server Error");		
				}
				catch (Exception ex){
					System.out.println("Another exception occured");
					e.printStackTrace();
				}
			}
		}
		
		private boolean GetAnswer(String quest){
			System.out.println(quest);
			Scanner scanner = new Scanner(System.in);
			boolean result = false;
			while (true) {
				String userInput = scanner.nextLine();
				if ("Yes".equalsIgnoreCase(userInput.trim()) ) {
					result = true;
					break;
				}
				else if("No".equalsIgnoreCase(userInput.trim())){
					break;
				}
				else{
					System.out.println("Please insert yes or no");
				}
			}
			return result;
		}
		
		private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(statusCode, response.length());
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		}
	}
	
	private static class InsertMockImages implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) {
			try {
				System.out.println("Creating mock images...");
				Db.insertCalc("23/12/2023");
				Db.insertImage("running", "nginx", 1);
				sendResponse(exchange, 200, "Mock images inserted successfully!");
			} catch (Exception e) {
				try {
					System.out.println("Internal Exception occurred");
					e.printStackTrace();
					sendResponse(exchange, 500, "Internal Server Error");
				} catch (Exception ex) {
					System.out.println("Another exception occurred");
					ex.printStackTrace();
				}
			}
		}   

		private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(statusCode, response.length());
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		}
	}
	
	private static class GetImages implements HttpHandler{
	
		@Override
		public void handle(HttpExchange exchange){
			try {
				System.out.println("Getting all images...");
				List<DockerImage> dockerImages = Db.getAllImages();
				String jsonResponse = convertToJson(dockerImages);
				System.out.println("Images fetched ok!");
				sendJsonResponse(exchange, 200, jsonResponse);
				
			} catch (Exception e) {
				try {
					System.out.println("Internal Exception occurred");
					e.printStackTrace();
					sendResponse(exchange, 500, "Internal Server Error");
				} catch (Exception ex) {
					System.out.println("Another exception occurred");
					ex.printStackTrace();
				}
			}
		}
		
		private String convertToJson(List<DockerImage> dockerImages) throws JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(dockerImages);
		}

		private void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(statusCode, jsonResponse.length());

			try (OutputStream os = exchange.getResponseBody()) {
				os.write(jsonResponse.getBytes());
			}
		}
		
		private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(statusCode, response.length());
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		}
	}

	
	private static class ExecutorControlHandler implements HttpHandler {

    private ExecutorThread executorThread;

    public ExecutorControlHandler(ExecutorThread executorThread) {
        this.executorThread = executorThread;
    }
	public ExecutorControlHandler(DockerClient dc,String contId,boolean isStart) {
        this.executorThread = new ExecutorThread(dc,contId, isStart);
    }
	

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
		System.out.println("path:" + path);
        switch (path) {
            case "/api/startExecutor":
                startExecutor(exchange);
                break;
            case "/api/stopExecutor":
                stopExecutor(exchange);
                break;
			case "/api/startCont":
				start(exchange, "Docker instance starting....");
			case "/api/stopCont":
				start(exchange, "Docker instance stopping....");				
            default:
                sendResponse(exchange, 404, "Not Found");
        }
    }

	private void start(HttpExchange exchange,String msg) throws IOException {
		if (!executorThread.isAlive()) {
            executorThread.start();
            sendResponse(exchange, 200, msg);
        } else {
            sendResponse(exchange, 400, "Executor is already running.");
        }
	}
	
    private void startExecutor(HttpExchange exchange) throws IOException {
        if (!executorThread.isAlive()) {
            executorThread.start();
            sendResponse(exchange, 200, "Executor started.");
        } else {
            sendResponse(exchange, 400, "Executor is already running.");
        }
    }

    private void stopExecutor(HttpExchange exchange) throws IOException {
        if (executorThread.isAlive()) {
            executorThread.stopInstance();
            //executorThread.stop();
            sendResponse(exchange, 200, "Executor stopping...");
        } else {
            sendResponse(exchange, 400, "Executor is not running.");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, response.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
	}
	
	//privates
	private static void Log(String msg){
		System.out.println(msg);
	}
	
}
	
