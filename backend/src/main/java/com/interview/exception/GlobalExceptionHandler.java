package com.interview.exception;

import com.google.gson.JsonObject;
import com.interview.dto.error.ApiErrorResponseDTO;
import com.interview.i18n.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

/**
 * Global exception handler for the application with i18n support.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAccountNotFoundException(AccountNotFoundException ex) {
        log.error("Account not found: {}", ex.getMessage());
        String accountIdentifier = ex.getAccountId() != null 
                ? String.valueOf(ex.getAccountId()) 
                : (ex.getAccountReferenceId() != null ? ex.getAccountReferenceId() : ex.getMessage());
        String message = Translator.getMessage("error.account.notFound", accountIdentifier);
        
        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        JsonObject errors = new JsonObject();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // Try to resolve message from i18n if it's a key
            if (errorMessage != null && errorMessage.startsWith("{") && errorMessage.endsWith("}")) {
                String key = errorMessage.substring(1, errorMessage.length() - 1);
                errorMessage = Translator.getMessage(key);
            }
            errors.addProperty(fieldName, errorMessage != null ? errorMessage : "");
        });
        
        String message = Translator.getMessage("error.validation.failed");
        
        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .errors(errors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        String message = Translator.getMessage("error.internal.server");
        
        ApiErrorResponseDTO response = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

