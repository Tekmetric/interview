package com.interview.dto;

public record VehicleSummaryDTO(
    Long id,
    String make,
    String model,
    Integer year,
    String licensePlate,
    int repairOrderCount
) {

}
