package com.interview.model.dto;

import com.interview.model.entity.Vehicle;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String make,
        String model,
        Integer year,
        String vin,
        String licensePlate,
        String color,
        Integer mileage,
        String ownerName,
        String ownerPhone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getVin(),
                vehicle.getLicensePlate(),
                vehicle.getColor(),
                vehicle.getMileage(),
                vehicle.getOwnerName(),
                vehicle.getOwnerPhone(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}