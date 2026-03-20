package com.interview.exception;

public class ConflictException extends RuntimeException {

    private final String resource;
    private final Object identifier;

    public ConflictException(String resource, Object identifier, String message) {
        super(message);
        this.resource = resource;
        this.identifier = identifier;
    }

    public String getResource() {
        return resource;
    }

    public Object getIdentifier() {
        return identifier;
    }
}
