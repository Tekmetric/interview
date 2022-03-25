package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class InvalidInventoryTypeException extends TekmetricDomainException {

    public InvalidInventoryTypeException(String code, String message) {
        super(code, message);
    }
}
