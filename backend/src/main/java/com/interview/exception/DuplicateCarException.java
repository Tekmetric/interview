package com.interview.exception;

public class DuplicateCarException extends RuntimeException {
    public DuplicateCarException(String message, Throwable cause) {
        super(message, cause);
    }
}
