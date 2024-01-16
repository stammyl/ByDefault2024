package com.vodafone.clientapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RestClient {

    private static final String BASE_URL = "http://localhost:8081";//"http://your-server-api-url";

    public static String startDockerInstance() throws IOException {
        // Implement HTTP call to start Docker instance
        // Return the response as a String
        //return executeHttpRequest("/start");
        return executeHttpRequest("/api/startCont");
    }

    public static String stopDockerInstance() throws IOException {
        // Implement HTTP call to stop Docker instance
        // Return the response as a String
        //return executeHttpRequest("/stop");
        return executeHttpRequest("/api/stopCont");
    }

    private static String executeHttpRequest(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();
            return response.toString();
        } else {
            // Handle error
            return null;
        }
    }	
	
}
