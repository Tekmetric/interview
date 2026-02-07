package com.interview.model.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class VehicleRequest {

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be at least 1900")
    @Max(value = 2100, message = "Year must be at most 2100")
    private Integer year;

    @Size(max = 17, message = "VIN must not exceed 17 characters")
    private String vin;

    @Size(max = 20)
    private String licensePlate;

    @Size(max = 30)
    private String color;

    @Min(value = 0, message = "Mileage cannot be negative")
    private Integer mileage;

    @NotBlank(message = "Owner name is required")
    @Size(max = 100)
    private String ownerName;

    @Size(max = 20)
    private String ownerPhone;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}