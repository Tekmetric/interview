package com.interview.dto;

import com.interview.model.RepairOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRepairOrderCommand(
    @NotBlank String description,
    @NotNull RepairOrderStatus status,
    @NotBlank String vehicleMake,
    @NotBlank String vehicleModel,
    @NotNull @Positive Integer vehicleYear,
    String licensePlate
) {}
