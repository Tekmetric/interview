package com.interview.autoshop.exceptions;

public class ServiceRequestNotFoundException extends RuntimeException{

    public ServiceRequestNotFoundException() {
        super("Service request for the id is not present");
    }
}
