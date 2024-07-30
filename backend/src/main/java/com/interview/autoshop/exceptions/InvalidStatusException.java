package com.interview.autoshop.exceptions;

public class InvalidStatusException extends RuntimeException{

    public InvalidStatusException() {
        super("Invalid status, Please use one of the following : {not_started, in_progress, in_review, completed}");
    }
}
