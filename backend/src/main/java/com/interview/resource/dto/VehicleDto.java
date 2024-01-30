package com.interview.resource.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    
    private Long id;
    private String licensePlate;
    private VehicleStateDto state;
    private String brand;
    private String model;
    private Integer registrationYear;
    private Double cost;
    private Date creationDate;
    private Date lastModificationDate;
}
