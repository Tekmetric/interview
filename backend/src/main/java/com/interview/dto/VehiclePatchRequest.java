package com.interview.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Partial-update payload. All fields are nullable. A {@code null} field is treated as
 * "leave unchanged" (not strict RFC 7396 Merge Patch).
 */
public record VehiclePatchRequest(
        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
        @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}", message = "VIN contains invalid characters")
        String vin,

        @Size(max = 64) String make,

        @Size(max = 64) String model,

        @Min(1900) Integer year,

        @Size(max = 16) String licensePlate,

        @PositiveOrZero Long mileage) {
}
