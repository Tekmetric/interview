package com.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "VehicleSearchCriteria", description = "Optional filters used when listing vehicles")
public class VehicleSearchCriteria {
    @Schema(description = "Filter by exact VIN", example = "JTDB4MEE9L1234567")
    @Pattern(regexp = "^[A-Z0-9]{17}$", message = "vin must be 17 uppercase letters or digits")
    private String vin;

    @Schema(description = "Filter by exact license plate", example = "ABC123")
    @Pattern(regexp = "^[A-Z0-9 -]{1,8}$", message = "licensePlate must be 1 to 8 uppercase letters, digits, or separators")
    private String licensePlate;

    @Schema(description = "Filter by manufacturer", example = "Toyota")
    private String make;

    @Schema(description = "Filter by model name", example = "Corolla")
    private String model;

    @Schema(description = "Filter by model year", example = "2024", minimum = "1900")
    @Min(value = 1900, message = "year must be at least 1900")
    private Integer year;
}