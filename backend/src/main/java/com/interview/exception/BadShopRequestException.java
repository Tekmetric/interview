package com.interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadShopRequestException extends RuntimeException {
	public BadShopRequestException(String message) {
		super(message);
	}
}
