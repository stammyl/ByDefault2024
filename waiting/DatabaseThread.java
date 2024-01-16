package com.vodafone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Added import for Statement
import java.util.List;
import java.util.ArrayList;

public class DatabaseThread extends Thread {
    private static final String DATABASE_URL = "jdbc:h2:mem:dockerCluster;DB_CLOSE_DELAY=-1";

    public void run() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
            // Create the table if it doesn't exist
            createTableIfNotExists(connection);

            while (true) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists(Connection connection) throws SQLException {
        try (Statement createStatement = connection.createStatement()) {
            createStatement.execute(
                    "CREATE TABLE IF NOT EXISTS images (" +
                            "id VARCHAR(255) PRIMARY KEY," +
                            "name VARCHAR(255)," +
                            "status VARCHAR(255)" +
                            ")"
            );
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
        try (PreparedStatement queryStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM images WHERE id = ?"
        )) {
            queryStatement.setString(1, dockerImage.getId());
            try (var resultSet = queryStatement.executeQuery()) {
                resultSet.next();
                int count = resultSet.getInt(1);

                if (count == 0) {
                    // Image doesn't exist, insert it into the database
                    try (PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO images (id, name, status) VALUES (?, ?, ?)"
                    )) {
                        insertStatement.setString(1, dockerImage.getId());
                        insertStatement.setString(2, dockerImage.getImage());
                        insertStatement.setString(3, dockerImage.getStatus());
                        insertStatement.executeUpdate();
                    }
                } else {
                    System.out.println("Image already exists!!");
                    // Image already exists, update it (if needed)
                    // Add your update logic here
                }
            }
        }
    }
}
