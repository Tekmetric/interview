package com.interview.model.dto;


import com.interview.model.entity.Vehicle;
import java.time.LocalDateTime;

public class VehicleResponse {

    private Long id;
    private String make;
    private String model;
    private Integer year;
    private String vin;
    private String licensePlate;
    private String color;
    private Integer mileage;
    private String ownerName;
    private String ownerPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.id = vehicle.getId();
        response.make = vehicle.getMake();
        response.model = vehicle.getModel();
        response.year = vehicle.getYear();
        response.vin = vehicle.getVin();
        response.licensePlate = vehicle.getLicensePlate();
        response.color = vehicle.getColor();
        response.mileage = vehicle.getMileage();
        response.ownerName = vehicle.getOwnerName();
        response.ownerPhone = vehicle.getOwnerPhone();
        response.createdAt = vehicle.getCreatedAt();
        response.updatedAt = vehicle.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }

    public String getVin() {
        return vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getColor() {
        return color;
    }

    public Integer getMileage() {
        return mileage;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}