package com.interview.dto;

import jakarta.validation.constraints.*;

/**
 * Payload for creating a new vehicle.
 */
public record VehicleRequest(
        @NotBlank
        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
        @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN contains invalid characters")
        String vin,

        @NotBlank @Size(max = 64) String make,

        @NotBlank @Size(max = 64) String model,

        @Min(1900) int year,

        @Size(max = 16) String licensePlate,

        @PositiveOrZero long mileage) {
}
