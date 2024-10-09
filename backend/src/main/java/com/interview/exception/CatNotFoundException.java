package com.interview.exception;

public class CatNotFoundException extends RuntimeException {
    public CatNotFoundException(Long missingCatId) {
        super("Cat with ID '" + missingCatId + "' has gone missing!");
    }
}
