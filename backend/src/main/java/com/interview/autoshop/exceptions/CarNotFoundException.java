package com.interview.autoshop.exceptions;

public class CarNotFoundException extends RuntimeException{

    public CarNotFoundException() {
        super("Car with id not found");
    }

}
