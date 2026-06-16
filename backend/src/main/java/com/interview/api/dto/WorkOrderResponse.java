package com.interview.api.dto;

import com.interview.domain.WorkOrderStatus;

import java.time.Instant;
import java.util.UUID;

public class WorkOrderResponse {

    private UUID id;
    private UUID customerId;
    private UUID vehicleId;
    private String description;
    private WorkOrderStatus status;
    private Instant createdAt;

    public WorkOrderResponse() {
    }

    public WorkOrderResponse(UUID id, UUID customerId, UUID vehicleId, String description,
                             WorkOrderStatus status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
