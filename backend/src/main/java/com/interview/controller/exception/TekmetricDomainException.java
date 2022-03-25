package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class TekmetricDomainException extends RuntimeException {

    private final String code;
    private final String message;

    public TekmetricDomainException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
