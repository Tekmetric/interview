package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Status is managed exclusively via /start and /close endpoints — not updatable here.
public record UpdateRepairOrderCommand(
    @NotBlank String description,
    @NotBlank String vehicleMake,
    @NotBlank String vehicleModel,
    @NotNull @Positive Integer vehicleYear,
    String licensePlate
) {}
