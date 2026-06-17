package com.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleFilterRequest {
    private String brand;
    private String model;
    private Integer registrationYear;
    private Integer registrationYearFrom;
    private Integer registrationYearTo;
    private String licensePlate;
    private Long ownerId;
}
