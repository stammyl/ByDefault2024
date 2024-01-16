package com.vodafone.serverapp;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Start the HTTP server process
        startHttpServerProcess();

        // Start the threads process
        //startThreadsProcess();
    }

    private static void startHttpServerProcess() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-cp",
                    ".",  // Use "." for the current directory
                    "RestApi"
            );
            processBuilder.directory(new java.io.File(System.getProperty("user.dir")));  // Set the working directory
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("HTTP Server process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // private static void startThreadsProcess() {
        // try {
            // ProcessBuilder processBuilder = new ProcessBuilder(
                    // "java",
                    // "-cp",
                    // ".",  // Use "." for the current directory
                    // "MainThreads"
            // );
            // processBuilder.directory(new java.io.File(System.getProperty("user.dir")));  // Set the working directory
            // Process process = processBuilder.start();
            // int exitCode = process.waitFor();
            // System.out.println("Threads process exited with code " + exitCode);
        // } catch (IOException | InterruptedException e) {
            // e.printStackTrace();
        // }
    // }
}
