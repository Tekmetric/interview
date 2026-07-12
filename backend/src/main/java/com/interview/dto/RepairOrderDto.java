package com.interview.dto;

import com.interview.domain.RepairOrderStatus;

import java.time.Instant;

public class RepairOrderDto {

    private Long id;
    private Long version;
    private String customerName;
    private String description;
    private RepairOrderStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public RepairOrderDto() {
        // Jackson
    }

    public RepairOrderDto(
            Long id,
            Long version,
            String customerName,
            String description,
            RepairOrderStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.version = version;
        this.customerName = customerName;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RepairOrderStatus getStatus() {
        return status;
    }

    public void setStatus(RepairOrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
