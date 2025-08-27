package com.interview.exception;

public class NotFoundException extends InternalServiceException {

    public NotFoundException(ErrorCode errorCode, String developerMessage) {
        super(errorCode, developerMessage);
    }

}
