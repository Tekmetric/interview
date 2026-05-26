package com.interview.exception;

public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
