package com.interview.vehicle.model;

import java.time.Year;

public interface Vehicle {

    VehicleId id();

    VehicleType type();

    Year fabricationYear();

    String make();

    String model();

    void applyUpdate(VehicleUpdate update);
}
