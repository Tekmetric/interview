package com.interview.domain;

import java.util.Objects;
import java.util.UUID;

public final class Vehicle {

    private final UUID id;
    private final String plateNumber;
    private final String model;
    private final UUID customerId;

    public Vehicle(UUID id, String plateNumber, String model, UUID customerId) {
        this.id = Objects.requireNonNull(id, "id");
        this.plateNumber = Objects.requireNonNull(plateNumber, "plateNumber");
        this.model = Objects.requireNonNull(model, "model");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
    }

    public UUID getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getModel() {
        return model;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
