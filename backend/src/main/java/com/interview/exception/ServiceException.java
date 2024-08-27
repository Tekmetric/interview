package com.interview.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ExceptionReason reason;

    public ServiceException(String errorMessage, ExceptionReason reason) {
        super(errorMessage);
        this.reason = reason;
    }

}
