package com.vodafone.restapiapp;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class ExecutorThread extends Thread {
    private DockerClient dockerClient;
	private boolean isRunning = false;
	private boolean isStart = true;
	private String contId = "";
    //private MonitorThread monitorThread;// this dependency is not necessary as far as here

    //public ExecutorThread(DockerClient dockerClient, MonitorThread monitorThread) {
        //this.dockerClient = dockerClient;
        //this.monitorThread = monitorThread;
    //}
	public ExecutorThread(DockerClient dockerClient) {
        this.dockerClient = dockerClient;        
    }
	public ExecutorThread(DockerClient dockerClient,String contId, boolean isstart) {
        this.dockerClient = dockerClient;        
        this.contId = contId;        
        this.isStart = isstart;        
    }

    @Override
    public void run() {
        
        // //Get id to start a container!(container has t oalready exist)
		 // Scanner scanner = new Scanner(System.in);
        
        // System.out.print("Enter a container ID to start: ");        
        // String userInputContainerId = scanner.nextLine();
		// startContainer(userInputContainerId);
        	

        // // Sleep for a while to allow monitoring thread to collect data
        // try {
            // Thread.sleep(3000); // Sleep for 10 seconds (adjust as needed)
        // } catch (InterruptedException e) {
            // e.printStackTrace();
        // }		
		
		// System.out.print("Enter a container ID to stop: ");        
        // userInputContainerId = scanner.nextLine();
		// stopContainer(userInputContainerId);		
		
		// scanner.close();	
		 // isRunning = true;

        // // Your thread logic (e.g., a loop)
        // while (isRunning) {
            // // Perform your tasks here
			// System.out.println("ExecutorThread is running...");

        // }
		if (isStart)
			startContainer(this.contId);
		else
			stopContainer(this.contId);
        
    }
	public void stopInstance() {
        isRunning = false;
    }

    private void startContainer(String containerId) {
        try {
           dockerClient.startContainerCmd(containerId).exec();
            System.out.println("Container started: " + containerId);
        } catch (Exception e) {
			System.out.println("Error with starting container id :" + containerId);
            //e.printStackTrace();
            // Handle exceptions related to starting a container
        }
    }

    private void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            System.out.println("Container stopped: " + containerId);
        } catch (Exception e) {
            e.printStackTrace();
			System.out.println("Error with stoping container id :" + containerId);
            // Handle exceptions related to stopping a container
        }
    }
}
