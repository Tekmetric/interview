package com.interview._infrastructure.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg) {
        super(msg);
    }
}
