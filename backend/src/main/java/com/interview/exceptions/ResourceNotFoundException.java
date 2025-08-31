package com.interview.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Serializable id) {
        super("%s with id: %s not found".formatted(resource, id));
    }
}
