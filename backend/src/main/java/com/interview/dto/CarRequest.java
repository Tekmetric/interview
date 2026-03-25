package com.interview.dto;

import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Request payload for creating or updating a car")
public record CarRequest(

        @NotBlank(message = "VIN is required")
        @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
        @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186")
        String vin,

        @NotBlank(message = "Brand is required")
        @Schema(description = "Car brand", example = "Honda")
        String brand,

        @NotBlank(message = "Model is required")
        @Schema(description = "Car model", example = "Civic")
        String model,

        @NotNull(message = "Year is required")
        @Schema(description = "Manufacturing year", example = "2023")
        Integer manufacturedYear,

        @Size(max = 50, message = "Color must not exceed 50 characters")
        @Schema(description = "Car color", example = "Blue")
        String color,

        @NotNull(message = "Fuel type is required")
        @Schema(description = "Fuel type", example = "GASOLINE")
        FuelType fuelType,

        @Size(max = 50, message = "Transmission must not exceed 50 characters")
        @Schema(description = "Transmission type", example = "Automatic")
        String transmission,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.01", message = "Base price must be greater than 0")
        @Schema(description = "Base price of the car", example = "25000.00")
        BigDecimal basePrice,

        @DecimalMin(value = "0.01", message = "Selling price must be greater than 0")
        @Schema(description = "Selling price (required for RESERVED/SOLD, must be null for AVAILABLE)", example = "24000.00")
        BigDecimal sellingPrice,

        @Schema(description = "Car status (defaults to AVAILABLE if not provided)", example = "AVAILABLE")
        CarStatus status
) {}
