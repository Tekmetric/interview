package com.interview.exception;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    public abstract String getCode();

    public abstract HttpStatus getStatus();

    public ApplicationException(String message) {
        super(message);
    }
}
