package com.interview.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a repair order in the shop management workflow.
 * <p>
 * This entity is intentionally modeled as a single table to match the scope
 * of the exercise.  In a large production system, related concepts such as
 * customer and vehicle could be modeled separately.
 */
@Entity
@Table(name = "repair_orders")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    /**
     * VIN is treated as unique in this exercise to demonstrate duplicate
     * resource handling with a simple single-entity design.
     */
    @Column(name = "vehicle_vin", nullable = false, length = 17, unique = true)
    private String vehicleVin;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RepairOrderStatus status;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically sets timestamps when a new entity is first persisted.
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Automatically refreshes the update timestamp on modifications.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Required by JPA/Hibernate for entity instantiation.
     */
    public RepairOrder() {
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getVehicleVin() {
        return vehicleVin;
    }

    public String getDescription() {
        return description;
    }

    public RepairOrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setVehicleVin(String vehicleVin) {
        this.vehicleVin = vehicleVin;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(RepairOrderStatus status) {
        this.status = status;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
