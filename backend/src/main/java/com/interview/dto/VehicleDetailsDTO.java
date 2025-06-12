package com.interview.dto;

public record VehicleDetailsDTO(
    Long id,
    String make,
    String model,
    Integer year,
    String licensePlate,
    CustomerSummaryDTO customer
) {

}
