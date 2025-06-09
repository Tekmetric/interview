package com.interview.exception;

/**
 * Exception thrown when there is an error during mapping between DTOs and entities.
 */
public class MappingException extends RuntimeException {

    public MappingException(String message) {
        super(message);
    }
}
