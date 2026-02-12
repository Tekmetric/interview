package com.interview.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Serializable id, String attribute) {
        super("%s with %s: %s not found".formatted(resource, attribute, id));
    }

    public ResourceNotFoundException(String resource, Serializable id) {
        this(resource, id, "id");
    }
}
