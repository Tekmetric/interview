package com.interview.transactions.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TransactionNotFound extends RuntimeException {

    public TransactionNotFound() {
    }
}
