package com.interview.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vehicle")
public class VehicleEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private UUID id;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "customer_id", length = 36, nullable = false)
    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private CustomerEntity customer;

    protected VehicleEntity() {
    }

    public VehicleEntity(UUID id, String plateNumber, String model, UUID customerId) {
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
