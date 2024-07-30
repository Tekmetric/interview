package com.interview.autoshop.dto.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCarDto {

    private String vin;

    private Long ownerId;

    private String model;

    private String make;

    private String color;

    private String licensePlate;
}
