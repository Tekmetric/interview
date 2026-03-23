package com.interview.dto;

import com.interview.entity.FuelType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class VehicleResponse {
    private Long id;
    private Instant createdAt;
    private Integer modelYear;
    private String make;
    private String model;
    private String color;
    private String licensePlate;
    private String vin;
    private FuelType fuelType;
    private Integer doors;
    private Integer mileage;
}
