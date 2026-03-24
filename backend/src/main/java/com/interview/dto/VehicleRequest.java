package com.interview.dto;

import com.interview.entity.FuelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "VehicleRequest", description = "Request payload used to create or update a vehicle")
public class VehicleRequest {
    @Schema(description = "Model year of the vehicle", example = "2024", minimum = "1900")
    @Min(1900)
    @NotNull
    private Integer modelYear;

    @Schema(description = "Manufacturer name", example = "Toyota")
    @NotBlank
    private String make;

    @Schema(description = "Model name", example = "Corolla")
    @NotBlank
    private String model;

    @Schema(description = "Exterior color", example = "Silver")
    private String color;

    @Schema(description = "License plate, 1 to 8 uppercase letters, digits, spaces, or hyphens", example = "ABC123")
    @Pattern(regexp = "^[A-Z0-9 -]{1,8}$")
    private String licensePlate;

    @Schema(description = "17-character VIN using uppercase letters and digits", example = "JTDB4MEE9L1234567")
    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{17}$")
    private String vin;

    @Schema(description = "Primary fuel type", example = "GASOLINE")
    @NotNull
    private FuelType fuelType;

    @Schema(description = "Number of doors", example = "4", minimum = "0")
    @Min(0)
    private Integer doors;

    @Schema(description = "Current mileage", example = "45000", minimum = "0")
    @Min(0)
    private Integer mileage;
}