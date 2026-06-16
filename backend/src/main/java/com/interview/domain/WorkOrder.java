package com.interview.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class WorkOrder {

    private final UUID id;
    private final UUID customerId;
    private final UUID vehicleId;
    private final String description;
    private final WorkOrderStatus status;
    private final Instant createdAt;

    public WorkOrder(UUID id, UUID customerId, UUID vehicleId, String description,
                     WorkOrderStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId");
        this.description = description;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
