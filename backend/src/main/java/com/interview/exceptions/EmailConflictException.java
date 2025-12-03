package com.interview.exceptions;

import com.interview.domain.dto.Email;

/**
 * Exception thrown when trying to create a {@code Person} with an email that is already in use.
 */
public class EmailConflictException extends RuntimeException {

    public EmailConflictException(Email email, Throwable cause) {
        super(String.format("Email %s is already in use", email), cause);
    }
}
