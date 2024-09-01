package com.interview.exception;

public class CarAlreadyExists extends RuntimeException {
    public CarAlreadyExists(String carName) {
        super(String.format("Car with name %s already exists", carName));
    }
}
