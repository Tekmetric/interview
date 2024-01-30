package com.interview.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class LicensePlateExistException extends ApplicationException {
    
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    private final String code = "LICENSE_PLATE_EXIST";

    public LicensePlateExistException() {
        super("License plate already exists");
    }

    public LicensePlateExistException(String licensePlate) {
        super(String.format("License plate %s already exists", licensePlate));
    }
}
