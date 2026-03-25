package com.interview.dto;

import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Car response payload")
public record CarResponse(

        @Schema(description = "Car ID", example = "1")
        Long id,

        @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186")
        String vin,

        @Schema(description = "Car brand", example = "Honda")
        String brand,

        @Schema(description = "Car model", example = "Civic")
        String model,

        @Schema(description = "Manufacturing year", example = "2023")
        Integer manufacturedYear,

        @Schema(description = "Car color", example = "Blue")
        String color,

        @Schema(description = "Fuel type", example = "GASOLINE")
        FuelType fuelType,

        @Schema(description = "Transmission type", example = "Automatic")
        String transmission,

        @Schema(description = "Base price", example = "25000.00")
        BigDecimal basePrice,

        @Schema(description = "Selling price", example = "24000.00")
        BigDecimal sellingPrice,

        @Schema(description = "Car status", example = "AVAILABLE")
        CarStatus status,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {}
