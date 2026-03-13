package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor

public class VehicleRequest {
    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    private String vin;
    @NotBlank(message = "Make is required")
    private String make;
    @NotBlank(message = "Model is required")
    private String model;
    @Min(value = 1900, message = "Year must be >= 1900")
    private int year;
}