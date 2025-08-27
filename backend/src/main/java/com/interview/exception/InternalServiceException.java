package com.interview.exception;

import lombok.Getter;

@Getter
public class InternalServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String developerMessage;

    public InternalServiceException(final ErrorCode errorCode, final String developerMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.developerMessage = developerMessage;
    }

    public InternalServiceException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.developerMessage = errorCode.getMessage();
    }

}
