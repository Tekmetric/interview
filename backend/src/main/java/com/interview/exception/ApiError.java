package com.interview.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Object used for api response when exception is thrown
 */
@Data
public class ApiError {

    private HttpStatus status;
    private String message;

    public ApiError(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }
}
