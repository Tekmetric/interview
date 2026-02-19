package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SnowReportNotFoundException extends RuntimeException {

    public SnowReportNotFoundException(Long id) {
        super("Snow report not found with id: " + id);
    }
}
