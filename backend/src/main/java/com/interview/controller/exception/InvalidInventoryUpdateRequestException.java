package com.interview.controller.exception;

import lombok.Getter;

@Getter
public class InvalidInventoryUpdateRequestException extends TekmetricDomainException {

    public InvalidInventoryUpdateRequestException(String code, String message) {
        super(code, message);
    }
}
