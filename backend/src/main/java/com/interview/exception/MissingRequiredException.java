package com.interview.exception;

/**
 * Exception for missing required data
 */
public class MissingRequiredException extends RuntimeException {
    public MissingRequiredException(String message) {
        super(message);
    }
}
