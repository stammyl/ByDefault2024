package com.vodafone;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;


import java.util.List;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static void main(String[] args) {
        
		try {     
			//-------------1st implementation - with executor!-------------------		
			/*	
			System.out.println("Started!!");
			// Initialize DockerClient						
			DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
			builder.withDockerHost("tcp://localhost:2375");
			DockerClient dc = DockerClientBuilder.getInstance(builder).build();
			dc.versionCmd().exec();
			
			//Initialize Monitor Thread
			MonitorThread monitorThread = new MonitorThread(dc);
			ExecutorThread executorThread = new ExecutorThread(dc, monitorThread);

			
			// Start monitor thread
			monitorThread.start();
			executorThread.start();
		
			//Waiting threads to finish
			try {
				monitorThread.join();
				executorThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			System.out.println("Press any key to continue...");
			System.in.read();
			System.out.println("Finito!");
			dc.close();
			*/
			//---------------------------------
			System.out.println("Monitor and Database Started!!");
			DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder();
			builder.withDockerHost("tcp://localhost:2375");
			DockerClient dc = DockerClientBuilder.getInstance(builder).build();
			dc.versionCmd().exec();
			
			//Initialize Monitor and Database Thread
			BlockingQueue<DockerImage> sharedList = new ArrayBlockingQueue<>(10);

			MonitorThread monitorThread = new MonitorThread(dc, sharedList);
			monitorThread.start();

			DatabaseThread databaseThread = new DatabaseThread(sharedList);
			databaseThread.start();
			
			//Waiting threads to finish
			try {
				monitorThread.join();
				databaseThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			System.out.println("Press any key to continue...");
			System.in.read();
			System.out.println("Finito!");
			dc.close();
			
        } catch (Exception e) {
            // Handle any exception
			System.err.println("An error occurred: " + e.getMessage());
        } finally {
			
		}
	}
}