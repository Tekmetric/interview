package com.interview.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
@Builder
public class FieldViolation {
    private String field;
    private String message;
    private String code;
    private Object rejectedValue;
}
