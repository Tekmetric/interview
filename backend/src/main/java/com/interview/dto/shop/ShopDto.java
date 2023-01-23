package com.interview.dto.shop;

import java.util.UUID;

public class ShopDto {

    private UUID id;

    private String title;

    private String imageFilename;

    private String location;

    private int staffNumber;

    private double avgOrder;

    public ShopDto(UUID id, String title, String imageFilename, int staffNumber, double avgOrder, String location) {
        this.id = id;
        this.title = title;
        this.imageFilename = imageFilename;
        this.staffNumber = staffNumber;
        this.avgOrder = avgOrder;
        this.location = location;
    }

    public ShopDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public int getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(int staffNumber) {
        this.staffNumber = staffNumber;
    }

    public double getAvgOrder() {
        return avgOrder;
    }

    public void setAvgOrder(double avgOrder) {
        this.avgOrder = avgOrder;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
