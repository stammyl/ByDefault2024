package com.vodafone.restapiapp;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class  RestApi{

	private static HttpServer server;
    //public static void main(String[] args) throws IOException {
     public static void StartServer() throws IOException {
        // Create an HTTP server on port 8080
        RestApi.server = HttpServer.create(new InetSocketAddress(8081), 0);
	
        // Create a context for the "/api/hello" endpoint
        RestApi.server.createContext("/api/hello", new HelloHandler());
        RestApi.server.createContext("/api/greet", new GreetHandler());
        RestApi.server.createContext("/api/testGet", new GetInstancesHandler());
        RestApi.server.createContext("/api/mockInsert", new CreateAndInsertHandler());

        // Start the server
        RestApi.server.start();
        System.out.println("Server started on port 8081");		
		
    }
	
	public static void StopServer()throws IOException{
		// Stop the server
        RestApi.server.stop(0);
        System.out.println("Server stopped.");
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
	
	private static class GetInstancesHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				// Establish an H2 in-memory database connection
				Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");

				// Execute a query to fetch instances from the database
				String sql = "SELECT id, image, status FROM images";
				try (PreparedStatement statement = connection.prepareStatement(sql)) {
					try (ResultSet resultSet = statement.executeQuery()) {
						// Build the response based on the query results
						StringBuilder response = new StringBuilder();
						while (resultSet.next()) {
							int id = resultSet.getInt("id");
							String image = resultSet.getString("image");
							String status = resultSet.getString("status");

							response.append(String.format("ID: %d, Image: %s, Status: %s%n", id, image, status));
						}

						// Send the response
						sendResponse(exchange, 200, response.toString());
					}
				}

				// Close the database connection
				connection.close();

			} catch (SQLException e) {
				e.printStackTrace();
				sendResponse(exchange, 500, "Internal Server Error");
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
	
	private static class CreateAndInsertHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			try {
				System.out.println("Connection to db started..");
				// Establish an H2 in-memory database connection
				Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");//, "username", "password");
				System.out.println("Connection to db ok!");
				// Create the "images" table if it doesn't exist
				System.out.println("Table creation started...");
				createImagesTable(connection);
				System.out.println("Table creation ok!");

				// Insert three mock Docker images into the "images" table
				System.out.println("Mock images creaton started...");
				insertMockImages(connection);
				System.out.println("Mock images creaton ok!");
				
				// Send a response
				sendResponse(exchange, 200, "Table created and mock images inserted successfully.");

				// Close the database connection
				connection.close();

			} catch (SQLException e) {
				e.printStackTrace();
				sendResponse(exchange, 500, "Internal Server Error");
			}
		}

		private void createImagesTable(Connection connection) throws SQLException {
			String createTableSQL = "CREATE TABLE IF NOT EXISTS images (" +
					"id INT AUTO_INCREMENT PRIMARY KEY," +
					"image VARCHAR(255) NOT NULL," +
					"status VARCHAR(50) NOT NULL," +
					"calculation_id INT," +
					"FOREIGN KEY (calculation_id) REFERENCES calculations(id)" +
					")";
			try (PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
				//statement.executeUpdate();
				statement.execute();
			}
		}

		private void insertMockImages(Connection connection) throws SQLException {
			String insertSQL = "INSERT INTO images (image, status, calculation_id) VALUES (?, ?, ?)";
			try (PreparedStatement statement = connection.prepareStatement(insertSQL)) {
				// Insert three mock Docker images
				insertImage(statement, "docker_image_1", "running", 1);
				insertImage(statement, "docker_image_2", "stopped", 2);
				insertImage(statement, "docker_image_3", "pending", 3);
			}
		}

		private void insertImage(PreparedStatement statement, String image, String status, int calculationId) throws SQLException {
			statement.setString(1, image);
			statement.setString(2, status);
			statement.setInt(3, calculationId);
			statement.executeUpdate();
		}

		private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
			exchange.getResponseHeaders().set("Content-Type", "text/plain");
			exchange.sendResponseHeaders(statusCode, response.length());
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		}
	}

	
}
