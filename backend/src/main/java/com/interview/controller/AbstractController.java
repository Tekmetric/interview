package com.interview.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractController {

    protected ResponseEntity<APIResponse> getErrorApiResponseEntity(String logError, Exception e, APIResponse response) {

        log.error(logError, e);

        response.setMessage(e.getMessage());
        response.setSuccess(false);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

}
