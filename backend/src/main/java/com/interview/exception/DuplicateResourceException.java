package com.interview.exception;

import lombok.Getter;

public class DuplicateResourceException extends BaseException {

    private final ErrorCode errorCode;

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getCode() {
        return this.errorCode.getCode();
    }

    @Getter
    public enum ErrorCode {

        EMAIL_ALREADY_EXISTS_IN_DB("email.already.exists.in.db"),
        PHONE_NUMBER_ALREADY_EXISTS_IN_DB("phone.number.already.exists.in.db");

        private final String code;

        ErrorCode(String code) {
            this.code = code;
        }
    }
}

