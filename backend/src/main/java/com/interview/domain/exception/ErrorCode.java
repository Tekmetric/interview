package com.interview.domain.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static com.interview.domain.exception.ErrorI18nKey.*;

/**
 * Enum to define error codes and additional info.
 */
@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /** Http bad request default error. */
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), 40001, BAD_REQUEST_ERROR_I18N_KEY),
    /** User email already exists. */
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), 40002, USER_EMAIL_ALREADY_EXISTS_ERROR_I18N_KEY),
    /** Http unauthorized request default error. */
    AUTHORIZATION_ERROR(HttpStatus.UNAUTHORIZED.value(), 40101, AUTHORIZATION_I18N_KEY),
    /** Http unauthorized request default error. */
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN.value(), 40301, FORBIDDEN_I18N_KEY),
    /** Http not found request default error. */
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), 40401, NOT_FOUND_KEY),
    /** Http internal server error request default error. */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), 40501, BAD_REQUEST_ERROR_I18N_KEY),
    /** Http internal server error request default error. */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 50001, SERVER_ERROR_I18N_KEY);

    /** Http status code. */
    private final int httpCode;
    /** Internal status code. */
    @JsonValue
    private final int internalCode;
    /** Error i18n key. */
    private final String i18nKey;
}