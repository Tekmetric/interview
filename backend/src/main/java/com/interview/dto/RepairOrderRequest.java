package com.interview.dto;

import com.interview.entity.RepairOrderStatus;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class RepairOrderRequest {

    @NotBlank
    @Size(max = 100)
    private String customerName;

    @NotBlank
    @Size(min = 17, max = 17)
    private String vehicleVin;

    @NotBlank
    @Size(max = 500)
    private String description;

    @NotNull
    private RepairOrderStatus status;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalCost;

    public RepairOrderRequest() {
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
