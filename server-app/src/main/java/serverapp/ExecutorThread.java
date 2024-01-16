package com.vodafone;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class ExecutorThread extends Thread {
    private DockerClient dockerClient;
    private MonitorThread monitorThread;// this dependency is not necessary as far as here

    public ExecutorThread(DockerClient dockerClient, MonitorThread monitorThread) {
        this.dockerClient = dockerClient;
        this.monitorThread = monitorThread;
    }

    @Override
    public void run() {
        
        //Get id to start a container!(container has t oalready exist)
		 Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter a container ID to start: ");        
        String userInputContainerId = scanner.nextLine();
		startContainer(userInputContainerId);
        	

        // Sleep for a while to allow monitoring thread to collect data
        try {
            Thread.sleep(10000); // Sleep for 10 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }		
		
		System.out.print("Enter a container ID to stop: ");        
        userInputContainerId = scanner.nextLine();
		stopContainer(userInputContainerId);		
		
		scanner.close();	
        
    }

    private void startContainer(String containerId) {
        try {
           dockerClient.startContainerCmd(containerId).exec();
            System.out.println("Container started: " + containerId);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions related to starting a container
        }
    }

    private void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            System.out.println("Container stopped: " + containerId);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions related to stopping a container
        }
    }
}
