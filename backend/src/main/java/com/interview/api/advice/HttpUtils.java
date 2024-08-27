package com.interview.api.advice;

import com.interview.exception.ExceptionReason;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class HttpUtils {
    static HttpStatusCode fromExceptionReason(ExceptionReason reason) {
        return switch (reason) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
        };
    }
}
