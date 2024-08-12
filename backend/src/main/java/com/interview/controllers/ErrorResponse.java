package com.interview.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class ErrorResponse {
    public HttpStatus status;
    public String errorMessage;

    @Autowired
    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.errorMessage = message;
    }
}
