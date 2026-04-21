package com.interview.exception;

public class VehicleNotFoundException extends RuntimeException {

    private final long id;

    public VehicleNotFoundException(long id) {
        super("Vehicle %d not found".formatted(id));
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
