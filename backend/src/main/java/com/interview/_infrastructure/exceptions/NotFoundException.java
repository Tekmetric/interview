package com.interview._infrastructure.exceptions;

public class NotFoundException  extends RuntimeException {

    public NotFoundException(String msg) {
        super(msg);
    }
}
