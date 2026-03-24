package com.interview.exception;

import lombok.Getter;

public class NotFoundException extends BaseException {

    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getCode() {
        return this.errorCode.getCode();
    }

    @Getter
    public enum ErrorCode {

        MECHANIC_NOT_FOUND("not.found.mechanic"),
        MECHANIC_SHOP_NOT_FOUND("not.found.mechanic.shop"),
        MECHANICS_NOT_FOUND_IN_MECHANIC_SHOP("not.found.mechanics.in.mechanic.shop");

        private final String code;

        ErrorCode(String code) {
            this.code = code;
        }
    }
}
