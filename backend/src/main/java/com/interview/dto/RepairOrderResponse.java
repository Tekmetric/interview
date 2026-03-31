package com.interview.dto;

import com.interview.entity.RepairOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RepairOrderResponse {

    private final Long id;
    private final String customerName;
    private final String vehicleVin;
    private final String description;
    private final RepairOrderStatus status;
    private final BigDecimal totalCost;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public RepairOrderResponse(Long id, String customerName, String vehicleVin, String description, RepairOrderStatus status, BigDecimal totalCost, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleVin = vehicleVin;
        this.description = description;
        this.status = status;
        this.totalCost = totalCost;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
}
