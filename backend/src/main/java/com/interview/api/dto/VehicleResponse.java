package com.interview.api.dto;

import java.util.UUID;

public class VehicleResponse {

    private UUID id;
    private String plateNumber;
    private String model;
    private UUID customerId;

    public VehicleResponse() {
    }

    public VehicleResponse(UUID id, String plateNumber, String model, UUID customerId) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.model = model;
        this.customerId = customerId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }
}
