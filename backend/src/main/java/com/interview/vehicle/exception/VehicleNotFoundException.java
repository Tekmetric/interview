package com.interview.vehicle.exception;

import com.interview.vehicle.model.VehicleId;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(VehicleId vehicleId) {
        super("Could not find vehicle with id: " + vehicleId);
    }

}
