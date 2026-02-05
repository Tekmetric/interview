package com.interview.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    @NotBlank(message = "VIN is required")
    private String vin;
    private String make;
    private String model;
    private Integer year;

    public VehicleDTO(String vin, String make, String model, Integer year) {
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
    }

}