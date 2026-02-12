package com.interview.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class WorkOrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;

    private String description;

    public WorkOrderRequest() {
    }

    public WorkOrderRequest(UUID customerId, UUID vehicleId, String description) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.description = description;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
