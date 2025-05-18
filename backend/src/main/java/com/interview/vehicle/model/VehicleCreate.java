package com.interview.vehicle.model;

import lombok.Builder;

import java.time.Year;

@Builder
public record VehicleCreate(VehicleType type,
                            Year fabricationYear,
                            String make,
                            String model) {}
