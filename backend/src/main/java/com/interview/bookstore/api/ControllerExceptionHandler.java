package com.interview.bookstore.api;

import com.interview.bookstore.domain.exception.DuplicateFieldException;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;

import static com.interview.bookstore.api.ErrorMessageCode.*;
import static com.interview.bookstore.api.ErrorReasonCode.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Log4j2
@AllArgsConstructor
@RestControllerAdvice
public class ControllerExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, Locale locale) {
        List<ApiValidationError> validations = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new ApiValidationError(field.getField(), field.getDefaultMessage()))
                .toList();

        var reason = messageSource.getMessage(VALIDATION_ERROR_MESSAGE.code(), null, locale);
        var errorResponse = new ApiErrorResponse(FIELD_VALIDATION, reason);
        errorResponse.addProperty("validations", validations);

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(DuplicateFieldException.class)
    public ResponseEntity<ApiErrorResponse> duplicateFieldException(DuplicateFieldException ex, Locale locale) {
        var errorMessage =  messageSource.getMessage(DUPLICATE_FIELD_ERROR.code(),
                new Object[] { ex.getFieldName() }, locale);
        var errorResponse = new ApiErrorResponse(DUPLICATE_FIELD_VALUE, errorMessage);
        errorResponse.addProperty("resource", ex.getResourceType().getSimpleName());
        errorResponse.addProperty("field", ex.getFieldName());

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, Locale locale) {
        var errorMessage = messageSource.getMessage(RESOURCE_NOT_FOUND_ERROR.code(),
                        new Object[]{ ex.getResourceType().getSimpleName(), ex.getResourceId() }, locale);
        var errorResponse = new ApiErrorResponse(RESOURCE_NOT_FOUND, errorMessage);

        return new ResponseEntity(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> invalidRequestBody(Locale locale) {
        var errorMessage = messageSource.getMessage(INVALID_REQUEST_BODY_SYNTAX.code(), null, locale);
        var errorResponse = new ApiErrorResponse(INVALID_REQUEST_BODY, errorMessage);

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> uncaughtExceptionHandler(Exception ex, Locale locale) {
        log.error("Unexpected exception while processing request: ", ex);
        var errorMessage = messageSource.getMessage(UNEXPECTED_ERROR.code(), null, locale);
        var errorResponse = new ApiErrorResponse(GENERIC_ERROR, errorMessage);

        return ResponseEntity.internalServerError()
                .body(errorResponse);
    }

}
