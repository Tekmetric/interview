package com.interview.exception;

public class DuplicateException extends InternalServiceException {

    public DuplicateException(ErrorCode errorCode, String developerMessage) {
        super(errorCode, developerMessage);
    }

}
