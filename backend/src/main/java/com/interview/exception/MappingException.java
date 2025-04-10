package com.interview.exception;

/**
 * Exception thrown when there is an error during mapping between DTOs and entities.
 */
public class MappingException extends RuntimeException {

    /**
     * Constructs a new mapping exception with the specified detail message.
     *
     * @param message the detail message
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Constructs a new mapping exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
