package com.interview.transactions.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TransactionConflict extends RuntimeException {

    public TransactionConflict() {
    }
}
