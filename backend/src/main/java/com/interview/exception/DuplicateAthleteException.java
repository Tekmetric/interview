package com.interview.exception;

/**
 * Thrown when attempting to create an athlete that already exists.
 */
public class DuplicateAthleteException extends RuntimeException {
    public DuplicateAthleteException(final String firstName, final String lastName) {
        super(String.format("Athlete '%s %s' already exists", firstName, lastName));
    }
}
