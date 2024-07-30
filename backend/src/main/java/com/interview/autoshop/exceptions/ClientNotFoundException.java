package com.interview.autoshop.exceptions;

public class ClientNotFoundException extends RuntimeException{

    public ClientNotFoundException() {
        super("Client with id not found");
    }

}
