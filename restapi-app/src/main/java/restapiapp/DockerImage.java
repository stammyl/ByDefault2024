// DockerImage.java
package com.vodafone.restapiapp;

public class DockerImage {
    private String image;
    private String id;
    private String status;
	private String calculation;

    public DockerImage(String image, String id, String status) {
        this.image = image;
        this.id = id;
        this.status = status;
        //this.calculation = "";
    }
	public DockerImage(String image, String id, String status, String calculation) {
        this.image = image;
        this.id = id;
        this.status = status;
        this.calculation = calculation;
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
	public String getCalc() {
        return this.calculation;
    }
}
