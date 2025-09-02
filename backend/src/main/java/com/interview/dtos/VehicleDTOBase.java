package com.interview.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public interface VehicleDTOBase {
    @Schema(description = "Vehicle Identification Number", example = "1HGCM82633A123456")
    String vin();

    @Schema(description = "Make of the vehicle", example = "Toyota")
    String make();

    @Schema(description = "Model of the vehicle", example = "Camry")
    String model();

    @Schema(description = "Manufacture year of the vehicle", example = "2020")
    Integer manufactureYear();

    @Schema(description = "License plate of the vehicle", example = "ABC-1234")
    String licensePlate();

    @Schema(description = "Name of the vehicle owner", example = "John Doe")
    String ownerName();
}