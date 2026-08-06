package com.interview.exception;

public class CarUpdateIdMismatchException extends RuntimeException {
    public CarUpdateIdMismatchException(String message) {
        super(message);
    }
}
