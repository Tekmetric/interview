package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class InvalidInventoryQuantityException extends TekmetricDomainException {

    public InvalidInventoryQuantityException(String code, String message) {
        super(code, message);
    }
}
