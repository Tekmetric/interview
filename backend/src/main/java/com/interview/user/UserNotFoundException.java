package com.interview.user;

public class UserNotFoundException extends RuntimeException {

    UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
