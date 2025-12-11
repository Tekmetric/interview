package com.interview.exception;

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
import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = null;
            
            // Get the default message which might be a message key like {validation.account.pageNumber.min}
            String defaultMessage = error.getDefaultMessage();
            
            // Check if default message is a message key (wrapped in {})
            if (defaultMessage != null && defaultMessage.startsWith("{") && defaultMessage.endsWith("}")) {
                // Extract key from {validation.account.pageNumber.min}
                String key = defaultMessage.substring(1, defaultMessage.length() - 1);
                errorMessage = Translator.getMessage(key, error.getArguments());
            } else {
                // Default message is already resolved, but try to resolve using error codes
                // Spring generates codes like: Min.accountListRequestDTO.pageNumber, Min.pageNumber, Min
                // Try to construct message key from field name and constraint
                String[] codes = error.getCodes();
                if (codes != null && codes.length > 0) {
                    // Extract constraint name (e.g., "Min" from "Min.accountListRequestDTO.pageNumber")
                    String constraintName = codes[0].split("\\.")[0];
                    // Construct message key: validation.account.{fieldName}.{constraintName.toLowerCase()}
                    String key = "validation.account." + fieldName + "." + constraintName.toLowerCase();
                    try {
                        errorMessage = Translator.getMessage(key, error.getArguments());
                    } catch (Exception e) {
                        // If key not found, use default message
                        errorMessage = defaultMessage;
                    }
                } else {
                    errorMessage = defaultMessage;
                }
            }
            
            errors.put(fieldName, errorMessage != null ? errorMessage : "");
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

