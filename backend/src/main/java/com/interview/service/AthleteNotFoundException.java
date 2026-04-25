package com.interview.service;

public class AthleteNotFoundException extends RuntimeException {
    public AthleteNotFoundException(String message) {
        super(message);
    }
}
