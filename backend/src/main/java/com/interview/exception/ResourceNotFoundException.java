package com.interview.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forId(String resourceName, Long id) {
        return new ResourceNotFoundException(resourceName + " not found with ID: " + id);
    }
}
