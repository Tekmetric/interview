package com.interview.bookstore.api;

import com.interview.bookstore.domain.exception.DuplicateISBNException;
import com.interview.bookstore.domain.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.interview.bookstore.api.ErrorMessageCode.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@AllArgsConstructor
@RestControllerAdvice
public class ControllerExceptionHandler {

    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, Locale locale) {
        List<ApiValidationError> validations = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new ApiValidationError(field.getField(), field.getDefaultMessage()))
                .toList();

        var reason = messageSource.getMessage(VALIDATION_ERROR_MESSAGE.code(), null, locale);
        return ResponseEntity.badRequest()
                .body(new ApiValidationErrorResponse(reason, validations));
    }

    @ExceptionHandler(DuplicateISBNException.class)
    public ResponseEntity<?> duplicateISBNException(Locale locale) {
        var errorMessage = Map.of(
                "reason", messageSource.getMessage(DUPLICATE_ISBN_ERROR.code(),
                        null, locale)
        );
        return ResponseEntity.badRequest()
                .body(Map.of("reason", errorMessage));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex, Locale locale) {
        var errorMessage = Map.of(
                "reason", messageSource.getMessage(RESOURCE_NOT_FOUND_ERROR.code(),
                        new Object[]{ ex.getResourceType().getSimpleName(), ex.getResourceId() }, locale)
        );
        return new ResponseEntity(errorMessage, NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> invalidRequestBody(Locale locale) {
        var errorMessage = messageSource.getMessage(INVALID_REQUEST_BODY_SYNTAX.code(), null, locale);
        return ResponseEntity.badRequest()
                .body(Map.of("reason", errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> uncaughtExceptionHandler(Exception ex, Locale locale) {
        var errorMessage = messageSource.getMessage(UNEXPECTED_ERROR.code(), null, locale);
        return ResponseEntity.internalServerError()
                .body(Map.of("reason",  errorMessage));
    }

}
