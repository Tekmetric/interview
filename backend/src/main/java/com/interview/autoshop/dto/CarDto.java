package com.interview.autoshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private Long id;

    private String vin;

    private ClientDto owner;

    private String model;

    private String make;

    private String color;

    private String licensePlate;
}
