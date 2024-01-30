package com.interview.resource.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCreationRequest {

    @NotBlank
    @Size(min = 6, max = 10)
    private String licensePlate;

    private String brand;

    private String model;

    private Integer registrationYear;

    private Double cost;
}
