package com.interview.runningevents.application.exception;

/**
 * Exception thrown when a requested running event is not found.
 */
public class RunningEventNotFoundException extends RuntimeException {

    /**
     * Creates a new RunningEventNotFoundException with a default message.
     *
     * @param id The ID of the running event that was not found
     */
    public RunningEventNotFoundException(Long id) {
        super("Running event not found with ID: " + id);
    }

    /**
     * Creates a new RunningEventNotFoundException with the specified message.
     *
     * @param message The error message
     */
    public RunningEventNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new RunningEventNotFoundException with the specified message and cause.
     *
     * @param message The error message
     * @param cause The cause of the exception
     */
    public RunningEventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
