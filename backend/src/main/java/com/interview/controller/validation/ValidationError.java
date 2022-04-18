package com.interview.controller.validation;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class ValidationError {
    private String field;
    private String error;
}
