package com.interview.dto;

import com.interview.entity.FuelType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequest {
    @Min(1900)
    @NotNull
    private Integer modelYear;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    private String color;

    @Pattern(regexp = "^[A-Z0-9 -]{1,8}$")
    private String licensePlate;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{17}$")
    private String vin;

    @NotNull
    private FuelType fuelType;

    @Min(0)
    private Integer doors;

    @Min(0)
    private Integer mileage;
}
