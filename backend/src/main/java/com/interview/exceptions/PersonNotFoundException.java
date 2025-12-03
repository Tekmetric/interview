package com.interview.exceptions;

import com.interview.domain.dto.Email;

import java.util.UUID;

/**
 * Exception thrown when a {@code Person} does not exist for a specified ID or email.
 */
public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(UUID id) {
        super(String.format("Person of ID %s not found", id));
    }

    public PersonNotFoundException(Email email) {
        super(String.format("Person of email %s not found", email));
    }
}
