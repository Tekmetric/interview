package com.interview.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public abstract class BaseException extends RuntimeException {

    protected String message;

    public abstract String getCode();

}
