package com.interview.dto;

import java.time.Instant;

public record VehicleResponse(
        Long id,
        String vin,
        String make,
        String model,
        int year,
        String licensePlate,
        long mileage,
        Instant createdAt,
        Instant updatedAt) {
}
