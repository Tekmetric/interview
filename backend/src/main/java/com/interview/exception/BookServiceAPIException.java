package com.interview.exception;

public class BookServiceAPIException extends RuntimeException {

    public BookServiceAPIException() {
    }

    public BookServiceAPIException(String message) {
        super(message);
    }

    public BookServiceAPIException(String message, Throwable cause) {
        super(message, cause);
    }
}
