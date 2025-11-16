package com.interview.exception;

/**
 * Exception for conflicting data insert
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
