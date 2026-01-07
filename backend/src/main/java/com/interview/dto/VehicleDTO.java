package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique vehicle identifier (auto-generated)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Model year is required")
    @Min(value = 1900, message = "Model year must be 1900 or later")
    @Max(value = 2100, message = "Model year must be 2100 or earlier")
    private Integer modelYear;

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be alphanumeric (excluding I, O, Q)")
    private String vin;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
}
