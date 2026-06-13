package com.interview.exception;

public class DuplicateVinException extends RuntimeException {

    public DuplicateVinException(String vin) {
        super("A car with VIN '" + vin + "' already exists");
    }
}
