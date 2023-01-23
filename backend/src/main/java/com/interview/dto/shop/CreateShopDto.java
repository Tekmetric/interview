package com.interview.dto.shop;

import com.interview.dto.ApiRequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateShopDto extends ApiRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotBlank
    @Size(min = 3, max = 100)
    private String imageFilename;

    @NotBlank
    @Size(min = 3, max = 100)
    private String location;

    private int staffNumber;

    private double avgOrder;

    public CreateShopDto(String title, String imageFilename, int staffNumber, double avgOrder, String location) {
        this.title = title;
        this.imageFilename = imageFilename;
        this.staffNumber = staffNumber;
        this.avgOrder = avgOrder;
        this.location = location;
    }

    public CreateShopDto() {
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
}
