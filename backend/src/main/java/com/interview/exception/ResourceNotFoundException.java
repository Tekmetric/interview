package com.interview.exception;

public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s not found for identifier: %s", resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier != null ? identifier.toString() : null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
