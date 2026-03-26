package com.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record VehicleRequest(

        @NotBlank(message = "Make is required")
        String make,

        @NotBlank(message = "Model is required")
        String model,

        @NotNull(message = "Year is required")
        @Min(value = 1886, message = "Year must be 1886 or later")
        @Max(value = 2026, message = "Year must be 2026 or earlier")
        Integer year,

        @NotBlank(message = "VIN is required")
        @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be 17 alphanumeric characters")
        String vin,

        @NotNull(message = "Mileage is required")
        @PositiveOrZero(message = "Mileage must be zero or greater")
        Integer mileage
) {}