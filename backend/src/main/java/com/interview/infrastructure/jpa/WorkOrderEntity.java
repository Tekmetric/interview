package com.interview.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "work_order")
public class WorkOrderEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "customer_id", length = 36, nullable = false)
    private UUID customerId;

    @Column(name = "vehicle_id", length = 36, nullable = false)
    private UUID vehicleId;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private WorkOrderStatusEntity status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected WorkOrderEntity() {
    }

    public WorkOrderEntity(UUID id, UUID customerId, UUID vehicleId, String description,
                          WorkOrderStatusEntity status, Instant createdAt) {
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

    public WorkOrderStatusEntity getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatusEntity status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public enum WorkOrderStatusEntity {
        OPEN,
        IN_PROGRESS,
        DONE
    }
}
