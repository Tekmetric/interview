package com.interview.exception;

public class InvalidDataException extends InternalServiceException {

    public InvalidDataException(ErrorCode errorCode, String developerMessage) {
        super(errorCode, developerMessage);
    }

}
