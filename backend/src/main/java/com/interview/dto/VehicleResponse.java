package com.interview.dto;

import com.interview.entity.FuelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Schema(name = "VehicleResponse", description = "Vehicle representation returned by the API")
public class VehicleResponse {
    @Schema(description = "Database identifier", example = "1")
    private Long id;

    @Schema(description = "Timestamp when the vehicle was created", example = "2026-03-24T13:15:30Z")
    private Instant createdAt;

    @Schema(description = "Model year of the vehicle", example = "2024")
    private Integer modelYear;

    @Schema(description = "Manufacturer name", example = "Toyota")
    private String make;

    @Schema(description = "Model name", example = "Corolla")
    private String model;

    @Schema(description = "Exterior color", example = "Silver")
    private String color;

    @Schema(description = "License plate", example = "ABC123")
    private String licensePlate;

    @Schema(description = "17-character vehicle identification number", example = "JTDB4MEE9L1234567")
    private String vin;

    @Schema(description = "Primary fuel type", example = "GASOLINE")
    private FuelType fuelType;

    @Schema(description = "Number of doors", example = "4")
    private Integer doors;

    @Schema(description = "Current mileage", example = "45000")
    private Integer mileage;
}