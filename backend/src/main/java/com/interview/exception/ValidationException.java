package com.interview.exception;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new validation exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new validation exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new validation exception for a null value.
     *
     * @param fieldName the name of the field that is null
     * @return a new ValidationException with a formatted message
     */
    public static ValidationException nullValue(String fieldName) {
        return new ValidationException(fieldName + " cannot be null");
    }
}