package com.interview.model.exception;

public class FieldNotAllowedInSortException extends RuntimeException {
    public FieldNotAllowedInSortException(String message) {
        super(message);
    }
}
