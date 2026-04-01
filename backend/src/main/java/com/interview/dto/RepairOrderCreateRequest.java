package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Only some fields are mandatory for simplicity. Also, some fields are dynamically set by the system like the id, totals, and initial status.
 * A production system might cross-check vehicle details against a VIN database but that's out of scope for this interview exercise.
 */
public record RepairOrderCreateRequest(
        @NotBlank @Size(min=3, max=32) String orderNumber,
        @NotBlank @Size(min=11, max=17) String vin,
        Integer vehicleYear,
        String vehicleMake,
        String vehicleModel,
        String customerName,
        String customerPhone,
        List<RepairLineItemRequest> lineItems
) {}
