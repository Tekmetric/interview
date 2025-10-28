package com.interview.exception;

/**
 * Exception for row not found.
 */
public class RowNotFoundException extends RuntimeException {
    public RowNotFoundException(String message) {
        super(message);
    }
}
