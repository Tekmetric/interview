package com.interview.common.validation;

import com.interview.common.exception.AutoPartServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AutoPartValidator {
    public UUID validateAutoPartId(String id) {
        try {
            return UUID.fromString(id.trim());
        } catch (IllegalArgumentException ex) {
            throw new AutoPartServiceException("'" + id + "' is not a valid AutoPart ID format. Please provide a valid UUID",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // You could also hadd other validation stuff here, like validating AutoPart object fields (the ones not already validated at the time of request
}
