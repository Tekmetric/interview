package com.interview.exception;

/**
 * Thrown when an athlete with the specified ID does not exist in the database.
 */
public class AthleteNotFoundException extends RuntimeException {
    public AthleteNotFoundException(Long id) {
        super("Athlete with ID " + id + " not found.");
    }
}
