package com.interview.vehicle.web.model;

import com.interview.vehicle.model.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Request for updating a vehicle")
public class UpdateVehicleRequest implements Serializable {

    @NotNull(message = "type is required")
    @Schema(description = "Type of vehicle", example = "SUV", requiredMode = Schema.RequiredMode.REQUIRED)
    private VehicleType type;

    @Min(value = 1900, message = "Year of fabrication should not be older than {value}")
    @Schema(description = "The year of fabrication", example = "2016")
    private Integer fabricationYear;

    @NotBlank(message = "make is required")
    @Schema(description = "The make of vehicle", example = "Toyota", requiredMode = Schema.RequiredMode.REQUIRED)
    private String make;

    @NotBlank(message = "model is required")
    @Schema(description = "The model of vehicle", example = "Prius", requiredMode = Schema.RequiredMode.REQUIRED)
    private String model;
}
