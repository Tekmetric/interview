package com.interview.dto;

import com.interview.domain.RepairOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RepairOrderCreateDto {

    @NotBlank
    @Size(max = 200)
    private String customerName;

    @Size(max = 1000)
    private String description;

    @NotNull
    private RepairOrderStatus status;

    public RepairOrderCreateDto() {
        // Jackson
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
}
