package com.interview.security.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String username) {
        super(String.format("User %s not found", username));
    }
}
