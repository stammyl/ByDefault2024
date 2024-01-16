package com.vodafone.restapiapp;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class Db {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/test";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
	
    // Method to create a database connection
    private static Connection getConnection() throws SQLException {
		Initialize();
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
	
	private static void Initialize(){
		try{			
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (Exception e){
			System.out.println("Problem executing Class : ");
			e.printStackTrace();
		}
	}
    // Method to create the CALCS table
    public static void createCalcsTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createCalcsTable = "CREATE TABLE IF NOT EXISTS CALCS (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "day VARCHAR(255)" +
                    ")";
            statement.executeUpdate(createCalcsTable);

            System.out.println("CALCS table created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to create the IMAGES table with a foreign key reference to CALCS
    public static void createImagesTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createImagesTable = "CREATE TABLE IF NOT EXISTS IMAGES (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "status VARCHAR(255)," +
                    "image VARCHAR(255)," +
                    "calcs_id INT," +
                    "FOREIGN KEY (calcs_id) REFERENCES CALCS(id)" +
                    ")";
            statement.executeUpdate(createImagesTable);

            System.out.println("IMAGES table created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new entry into the CALCS table
    public static void insertCalc(String day) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO CALCS (day) VALUES (?)")) {

            preparedStatement.setString(1, day);
            preparedStatement.executeUpdate();

            System.out.println("New entry inserted into CALCS table.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert a new entry into the IMAGES table
    public static void insertImage(String status, String imageUrl, int calcsId) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO IMAGES (status, image, calcs_id) VALUES (?, ?, ?)")) {

            preparedStatement.setString(1, status);
            preparedStatement.setString(2, imageUrl);
            preparedStatement.setInt(3, calcsId);
            preparedStatement.executeUpdate();

            System.out.println("New entry inserted into IMAGES table.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete the CALCS table
    public static void deleteCalcsTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String deleteCalcsTable = "DROP TABLE IF EXISTS CALCS";
            statement.executeUpdate(deleteCalcsTable);

            System.out.println("CALCS table deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    public static void deleteImagesTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String deleteImagesTable = "DROP TABLE IF EXISTS IMAGES";
            statement.executeUpdate(deleteImagesTable);
            System.out.println("IMAGES table deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   
    

    // Method to delete the IMAGES table
    public static void deleteImagesTable() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String deleteImagesTable = "DROP TABLE IF EXISTS IMAGES";
            statement.executeUpdate(deleteImagesTable);

            System.out.println("IMAGES table deleted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	public static List<DockerImage> getAllImages() {
        List<DockerImage> dockerImages = new ArrayList<DockerImage>();
		String sql = "SELECT id, status, image, calcs_id FROM IMAGES";
		
        try (Connection connection = Db.getConnection();			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			) {

			while (resultSet.next()) {
				
				int id = resultSet.getInt("id");
				String idStr = Integer.toString(id);
				String status = resultSet.getString("status");
				String imageUrl = resultSet.getString("image");
				int calcsId = resultSet.getInt("calcs_id");
				String calcsIdStr = Integer.toString(calcsId);

				DockerImage dockerImage = new DockerImage(imageUrl, idStr,status, calcsIdStr);
				dockerImages.add(dockerImage);			
			}
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception according to your application's requirements
        }

        return dockerImages;
    }

}

