package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class InvalidInventoryIdsException extends TekmetricDomainException {

    public InvalidInventoryIdsException(String code, String message) {
        super(code, message);
    }
}
