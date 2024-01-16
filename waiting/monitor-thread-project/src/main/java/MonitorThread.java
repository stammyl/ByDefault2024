package com.vodafone;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;
import java.util.ArrayList;


public class MonitorThread extends Thread {
    private DockerClient dockerClient;
    private List<DockerImage> dockerImages;

    public MonitorThread(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        this.dockerImages = new ArrayList<>();
    }
	

    @Override
    public void run() {
        while (true) {
			System.out.println("Started Monitor Thread Cycle!!");
            // Monitor Docker containers and update the list
            //List<Container> containers = dc.listContainersCmd().exec();
			List<Container> containers = this.dockerClient.listContainersCmd().withShowAll(true).exec();
            updateDockerImages(containers);

            // Sleep for a specific interval before the next check
            try {
                Thread.sleep(5000); // Sleep for 5 seconds (adjust as needed)
            } catch (InterruptedException e) {
                e.printStackTrace();
                // Handle interruption, for example, by exiting the loop
                break;
            }
        }
    }

    private synchronized void updateDockerImages(List<Container> containers) {
        dockerImages.clear(); // Clear existing data

        for (Container container : containers) {
            DockerImage dockerImage = new DockerImage(
                    container.getImage(),
                    container.getId(),
                    container.getStatus()
            );

            dockerImages.add(dockerImage);
			System.out.println("------Container monitored!!------");
			System.out.println("Container ID: " + container.getId());
			System.out.println("Image: "        + container.getImage());
			System.out.println("Status: "       + container.getStatus());
			System.out.println("-------------------------------");
        }
    }

    public List<DockerImage> getDockerImages() {
        return this.dockerImages; // Return a copy to avoid concurrency issues
    }
}