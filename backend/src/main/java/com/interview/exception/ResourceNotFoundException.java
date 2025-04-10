package com.interview.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new resource not found exception with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new resource not found exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new resource not found exception with a formatted message for an entity with ID.
     *
     * @param resourceName the name of the resource
     * @param id the ID of the resource
     * @return a new ResourceNotFoundException with a formatted message
     */
    public static ResourceNotFoundException forId(String resourceName, Long id) {
        return new ResourceNotFoundException(resourceName + " not found with ID: " + id);
    }
}