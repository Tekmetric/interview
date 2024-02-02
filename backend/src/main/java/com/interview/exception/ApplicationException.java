package com.interview.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApplicationException extends RuntimeException {

    private HttpStatus status;
    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
