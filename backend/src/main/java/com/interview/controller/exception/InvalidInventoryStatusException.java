package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class InvalidInventoryStatusException extends TekmetricDomainException {

    public InvalidInventoryStatusException(String code, String message) {
        super(code, message);
    }
}
