package com.interview.common.error;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class<?> resourceClass, Object id) {
        super(resourceClass.getSimpleName() + " with id " + id + " was not found");
    }
}
