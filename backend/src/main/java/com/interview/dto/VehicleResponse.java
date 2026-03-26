package com.interview.dto;

import com.interview.model.FuelType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String make,
        String model,
        Integer year,
        String vin,
        Integer mileage,
        String licensePlate,
        String customerName,
        FuelType fuelType,
        Map<String, String> metadata,
        Instant createdAt,
        Instant updatedAt
) {}
