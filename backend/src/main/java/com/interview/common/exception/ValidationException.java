package com.interview.common.exception;


import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String errorCode;

    public ValidationException(String message) {
        super(message);
        this.errorCode = null;
    }

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
