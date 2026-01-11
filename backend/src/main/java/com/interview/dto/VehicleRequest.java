package com.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Brand is required")
    @Size(min = 1, max = 100, message = "Brand must be between 1 and 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(min = 1, max = 100, message = "Model must be between 1 and 100 characters")
    private String model;

    @NotNull(message = "Registration year is required")
    @Min(value = 1886, message = "Registration year must be at least 1886")
    @Max(value = 2100, message = "Registration year cannot be in the far future")
    private Integer registrationYear;

    @NotBlank(message = "License plate is required")
    @Size(min = 2, max = 20, message = "License plate must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9\\-\\s]+$", message = "License plate must contain only uppercase letters, numbers, hyphens, and spaces")
    private String licensePlate;

    @NotNull(message = "Owner ID is required")
    @Positive(message = "Owner ID must be a positive number")
    private Long ownerId;
}
