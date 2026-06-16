package com.interview.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class AutoPartServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1949969486218931238L;
    private final HttpStatus httpStatus;

    public AutoPartServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
