package com.interview.exception;

public class ShopUniqueConstraintViolationException extends RuntimeException {

    public ShopUniqueConstraintViolationException(String message) {
        super(message);
    }
}
