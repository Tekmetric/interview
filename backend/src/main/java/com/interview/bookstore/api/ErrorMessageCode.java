package com.interview.bookstore.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorMessageCode {
    VALIDATION_ERROR_MESSAGE("api.validation.reason.message"),
    DUPLICATE_FIELD_ERROR("api.duplicate.field.error"),
    RESOURCE_NOT_FOUND_ERROR("api.resource.not.found.error"),
    INVALID_REQUEST_BODY_SYNTAX("api.invalid.request.syntax"),
    UNEXPECTED_ERROR("api.generic.error");

    private final String code;

    public String code() {
        return this.code;
    }
}
