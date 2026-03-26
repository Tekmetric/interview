package com.interview.dto;

import com.interview.model.FuelType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record VehicleRequest(

        @NotBlank(message = "Make is required")
        String make,

        @NotBlank(message = "Model is required")
        String model,

        @NotNull(message = "Year is required")
        @Min(value = 1886, message = "Year must be 1886 or later")
        @Max(value = 2026, message = "Year must be 2026 or earlier")
        Integer year,

        @NotNull(message = "Mileage is required")
        @PositiveOrZero(message = "Mileage must be zero or greater")
        Integer mileage,

        FuelType fuelType,

        @Size(max = 100, message = "Customer name must be at most 100 characters")
        String customerName,

        @Size(max = 20, message = "License plate must be at most 20 characters")
        String licensePlate,

        @Pattern(
                regexp = "^$|^[A-HJ-NPR-Z0-9]{17}$",
                message = "VIN must be 17 alphanumeric characters when provided")
        String vin,

        @Size(max = 32, message = "Metadata may have at most 32 entries")
        Map<String, String> metadata
) {}
