package com.interview.vehicle.model;

import lombok.Builder;

import java.time.Year;

@Builder
public record VehicleUpdate(VehicleType type,
                            Year fabricationYear,
                            String make,
                            String model) {}
