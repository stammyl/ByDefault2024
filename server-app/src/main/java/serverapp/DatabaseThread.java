package com.vodafone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Added import for Statement
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseThread extends Thread {
    private static final String DATABASE_URL = "jdbc:h2:mem:dockerCluster;DB_CLOSE_DELAY=-1";
	private final BlockingQueue<DockerImage> sharedList;
	
	public DatabaseThread(BlockingQueue<DockerImage> sharedList) {
        this.sharedList = sharedList;
    }
	
    public void run() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // Create the table if it doesn't exist
            createTableCalcsIfNotExists(connection);
            createTableIfNotExists(connection);

            while (true) {
				//tested ok!
				/*
                List<DockerImage> imagesCopy = new ArrayList<>();
                
                DockerImage d1  = new DockerImage("nginx", "1", "running");
                DockerImage d2  = new DockerImage("linux", "2", "stopped");
                imagesCopy.add(d1);
                imagesCopy.add(d2);

                // Process the images and save them to the database
                processAndSaveImages(connection, imagesCopy);

                // Sleep for a while before checking again
                try {
                    Thread.sleep(5000); // Adjust as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
				*/
				if (!sharedList.isEmpty()){
					DockerImage di = sharedList.take();
					updateDatabase(connection, di);
				}				
                Thread.sleep(10000);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		catch (Exception e){
			System.out.println("Other Exception occured from Main of DB and Monitor Threads");
			e.printStackTrace();
		}
    }

    private void createTableIfNotExists(Connection connection) throws SQLException {
        try (Statement createStatement = connection.createStatement()) {
            createStatement.execute(
                    "CREATE TABLE IF NOT EXISTS images (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "image VARCHAR(255)," +
					"status VARCHAR(255)," +                    
                    "calcs_id INT," +
                    "FOREIGN KEY (calcs_id) REFERENCES CALCS(id)"+
                            ")"
            );
        }
    }
	private void createTableCalcsIfNotExists(Connection connection) throws SQLException {
        try (Statement createStatement = connection.createStatement()) {
            createStatement.execute(
                  "CREATE TABLE IF NOT EXISTS calcs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "day VARCHAR(255)" +
                    ")");
        }
    }

    private void processAndSaveImages(Connection connection, List<DockerImage> images) {
        try {
            for (DockerImage dockerImage : images) {
                // Insert or update the database with the Docker image information
                updateDatabase(connection, dockerImage);
				System.out.println("Image saved!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabase(Connection connection, DockerImage dockerImage) throws SQLException {
        // Check if the image already exists in the database
		
		//update calcs table
		try (PreparedStatement queryStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM calcs WHERE day = ?"
        )){
			queryStatement.setString(1, dockerImage.getCalc());
			try (var resultSet = queryStatement.executeQuery()) {
                resultSet.next();
				int count = resultSet.getInt(1);
				   // Image doesn't exist, insert it into the database
				if(count == 0){
					 try (PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO calcs (day) VALUES (?)"
                    )) {
                        insertStatement.setString(1, dockerImage.getCalc());                       
                        insertStatement.executeUpdate();
                    }					
				}                   
                else {
                    System.out.println("Image already exists in images!!");
                    // Image already exists, update it (if needed)
                    // Add your update logic here
                }
			}
		}
		
		//update images table
        try (PreparedStatement queryStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM images WHERE image = ?"
        )) {
            queryStatement.setString(1, dockerImage.getId());
            try (var resultSet = queryStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    // Image doesn't exist, insert it into the database
                    try (PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO images (image,status,calcs_id) VALUES (?, ?, ?)"
                    )) {
                        insertStatement.setString(1, dockerImage.getId());
                        insertStatement.setString(2, dockerImage.getImage());
                        insertStatement.setString(3, dockerImage.getStatus());
                        insertStatement.executeUpdate();
                    }
                } else {
                    System.out.println("Image already exists in images!!");
                    // Image already exists, update it (if needed)
                    // Add your update logic here
                }
            }
        }
    }
}
