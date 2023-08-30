package com.interview.exception;

/**
 * Exception to be thrown for player service related
 */
public class PlayerServiceException extends Exception {
    public PlayerServiceException(String message){
        super(message);
    }
}
