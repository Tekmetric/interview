package com.interview.dto;

import java.util.UUID;

public record CarDto(
        UUID id,
        String vin,
        Integer year,
        String make,
        String model,
        String trim,
        String color,
        Integer mileage,
        String transmission,
        String drivetrain
) {
}
