package com.interview.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class VehicleNotFoundException extends ApplicationException {
    
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    private final String code = "VEHICLE_NOT_FOUND";

    public VehicleNotFoundException() {
        super("Vehicle not found");
    }

    public VehicleNotFoundException(Long id) {
        super(String.format("Vehicle %s not found", id));
    }
}
