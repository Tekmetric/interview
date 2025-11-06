package com.interview._infrastructure.exceptions;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(String msg) {
        super(msg);
    }
}
