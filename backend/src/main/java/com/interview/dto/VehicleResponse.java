package com.interview.dto;

import java.time.Instant;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String make,
        String model,
        Integer year,
        String vin,
        Integer mileage,
        Instant createdAt,
        Instant updatedAt
) {}