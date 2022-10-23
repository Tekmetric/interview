package com.interview.domain.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Wraps the needed information to handle the exception.
 */
@Getter
@Setter
public class ErrorDetail  {

    private String description;
    private ErrorCode errorCode;
    private List<FieldErrorDto> fieldErrors;

    /**
     * Public constructor.
     * @param errorCode the {@link ErrorCode} instance
     */
    public ErrorDetail(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Public constructor.
     * @param errorCode the {@link ErrorCode} instance
     * @param fieldErrors list of field errors.
     */
    public ErrorDetail(ErrorCode errorCode, List<FieldErrorDto> fieldErrors) {
        this.errorCode = errorCode;
        this.fieldErrors = fieldErrors;
    }
}
