// DockerImage.java
package com.vodafone;

public class DockerImage {
    private String image;
    private String id;
    private String status;

    public DockerImage(String image, String id, String status) {
        this.image = image;
        this.id = id;
        this.status = status;
    }

    public String getImage() {
        return this.image;
    }

    public String getId() {
        return this.id;
    }

    public String getStatus() {
        return this.status;
    }
}
