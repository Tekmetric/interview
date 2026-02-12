package com.interview.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class VehicleRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    public VehicleRequest() {
    }

    public VehicleRequest(String plateNumber, String model, UUID customerId) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.customerId = customerId;
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
