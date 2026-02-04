package com.interview.model.dto;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    @NotBlank(message = "VIN is required")
    private String vin;

    private String make;
    private String model;
    private Integer year;
}