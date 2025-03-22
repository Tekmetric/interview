package com.interview.runningevents.application.exception;

/**
 * Exception thrown when validation fails for a running event.
 */
public class ValidationException extends RuntimeException {

    /**
     * Creates a new ValidationException with the specified message.
     *
     * @param message The error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates a new ValidationException with the specified message and cause.
     *
     * @param message The error message
     * @param cause The cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
