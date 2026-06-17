package com.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a vehicle is not found.
 */
public class VehicleNotFoundException extends BusinessException {

    public VehicleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "VEHICLE_NOT_FOUND");
    }

    public VehicleNotFoundException(Long vehicleId) {
        super("Vehicle not found with ID: " + vehicleId, HttpStatus.NOT_FOUND, "VEHICLE_NOT_FOUND");
    }
}