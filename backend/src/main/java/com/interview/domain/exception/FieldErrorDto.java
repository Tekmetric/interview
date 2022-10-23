package com.interview.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Field error dto.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
public class FieldErrorDto{
    private final String objectName;
    private String field;
    private final String message;
}
