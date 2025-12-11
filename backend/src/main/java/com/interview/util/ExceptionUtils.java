package com.interview.util;

import com.interview.dto.error.ApiErrorResponseDTO;
import com.interview.i18n.Translator;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for exception handling and error response building.
 * Provides methods for processing validation errors and building error responses.
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        // Utility class
    }

    /**
     * Resolves validation error message from a FieldError with i18n support.
     * Uses Spring's built-in message code resolution mechanism via MessageSourceResolvable.
     * Falls back to standard pattern: <module>.<field>.<constraint> (e.g., account.name.notblank)
     * Handles message key resolution and includes rejected values in error messages.
     * 
     * @param fieldError The field error from validation (implements MessageSourceResolvable)
     * @return Resolved error message
     */
    public static String resolveValidationErrorMessage(FieldError fieldError) {
        Object rejectedValue = fieldError.getRejectedValue();
        String fieldName = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();
        String[] codes = fieldError.getCodes();
        
        // Prepare arguments including rejected value
        Object[] messageArgs = prepareMessageArguments(fieldError.getArguments(), rejectedValue);
        
        // Check if default message is a message key (wrapped in {})
        if (defaultMessage != null && defaultMessage.startsWith("{") && defaultMessage.endsWith("}")) {
            // Extract key from {account.name.notblank}
            String key = defaultMessage.substring(1, defaultMessage.length() - 1);
            try {
                return Translator.getMessage(key, messageArgs);
            } catch (Exception e) {
                // Fall through to try Spring's codes
            }
        }
        
        // Use Spring's MessageSourceResolvable mechanism
        // FieldError implements MessageSourceResolvable, which provides getCodes() and getArguments()
        // Spring's MessageSource will automatically try each code in order until one resolves
        
        // Create a resolvable wrapper that includes rejected value in arguments
        MessageSourceResolvable resolvable = new MessageSourceResolvable() {
            @Override
            public String[] getCodes() {
                // Add standard pattern codes: account.<field>.<constraint>
                String[] standardCodes = constructStandardMessageCodes(codes, fieldName);
                // Combine Spring's codes with standard pattern codes
                String[] allCodes = new String[standardCodes.length + (codes != null ? codes.length : 0)];
                int index = 0;
                if (codes != null) {
                    System.arraycopy(codes, 0, allCodes, index, codes.length);
                    index += codes.length;
                }
                System.arraycopy(standardCodes, 0, allCodes, index, standardCodes.length);
                return allCodes;
            }
            
            @Override
            public Object[] getArguments() {
                return messageArgs;
            }
            
            @Override
            public String getDefaultMessage() {
                if (defaultMessage != null && defaultMessage.startsWith("{") && defaultMessage.endsWith("}")) {
                    return defaultMessage.substring(1, defaultMessage.length() - 1);
                }
                return defaultMessage;
            }
        };
        
        // Resolve using Translator's MessageSource
        String resolved = Translator.resolveMessage(resolvable);
        
        // If resolution failed and we have a default message, try to construct standard pattern key
        if (resolved == null || resolved.isEmpty() || resolved.equals(defaultMessage)) {
            String standardKey = constructStandardMessageKey(codes, fieldName);
            if (standardKey != null) {
                try {
                    return Translator.getMessage(standardKey, messageArgs);
                } catch (Exception e) {
                    // Fall through to return resolved or default
                }
            }
        }
        
        return resolved != null && !resolved.isEmpty() ? resolved : (defaultMessage != null ? defaultMessage : "");
    }
    
    /**
     * Constructs standard message codes following pattern: <module>.<field>.<constraint>
     * Example: account.name.notblank, account.email.email, account.countryCode.invalid
     * 
     * @param springCodes Spring's generated codes (e.g., NotBlank.accountCreateRequestDTO.accountName)
     * @param fieldName The field name (e.g., accountName)
     * @return Array of standard pattern message codes
     */
    private static String[] constructStandardMessageCodes(String[] springCodes, String fieldName) {
        if (springCodes == null || springCodes.length == 0 || fieldName == null) {
            return new String[0];
        }
        
        // Extract constraint name from first code (e.g., "NotBlank" from "NotBlank.accountCreateRequestDTO.accountName")
        String constraintName = springCodes[0].split("\\.")[0];
        if (constraintName == null || constraintName.isEmpty()) {
            return new String[0];
        }
        
        // Normalize constraint name to lowercase (e.g., NotBlank -> notblank, Email -> email)
        String normalizedConstraint = normalizeConstraintName(constraintName);
        
        // Construct standard pattern: account.<field>.<constraint>
        // Handle field name variations (e.g., accountName -> name, currencyCode -> currencyCode)
        String normalizedField = normalizeFieldName(fieldName);
        String standardKey = "account." + normalizedField + "." + normalizedConstraint;
        
        return new String[]{standardKey};
    }
    
    /**
     * Constructs a standard message key following pattern: <module>.<field>.<constraint>
     * 
     * @param springCodes Spring's generated codes
     * @param fieldName The field name
     * @return Standard pattern message key or null
     */
    private static String constructStandardMessageKey(String[] springCodes, String fieldName) {
        String[] codes = constructStandardMessageCodes(springCodes, fieldName);
        return codes.length > 0 ? codes[0] : null;
    }
    
    /**
     * Normalizes constraint name to lowercase and handles special cases.
     * Examples: NotBlank -> notblank, Email -> email, ValidCountryCode -> invalid
     * 
     * @param constraintName The constraint name
     * @return Normalized constraint name
     */
    private static String normalizeConstraintName(String constraintName) {
        if (constraintName == null || constraintName.isEmpty()) {
            return "";
        }
        
        // Handle custom validators (ValidCountryCode, ValidCurrencyCode) -> invalid
        if (constraintName.startsWith("Valid")) {
            return "invalid";
        }
        
        // Convert to lowercase (NotBlank -> notblank, Email -> email, Size -> size, Min -> min)
        return constraintName.toLowerCase();
    }
    
    /**
     * Normalizes field name for standard pattern.
     * Examples: accountName -> name, currencyCode -> currencyCode, email -> email
     * 
     * @param fieldName The field name
     * @return Normalized field name
     */
    private static String normalizeFieldName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return "";
        }
        
        // Remove "account" prefix if present (accountName -> name)
        // Handle camelCase: accountName -> name, accountId -> id
        if (fieldName.length() > 7 && fieldName.startsWith("account")) {
            String remainder = fieldName.substring(7);
            if (!remainder.isEmpty()) {
                // Convert first letter to lowercase (accountName -> Name -> name)
                return remainder.substring(0, 1).toLowerCase() + (remainder.length() > 1 ? remainder.substring(1) : "");
            }
        }
        
        // Return field name as-is (email -> email, currencyCode -> currencyCode)
        return fieldName;
    }

    /**
     * Processes validation errors and returns a map of field names to error messages.
     * 
     * @param fieldErrors Array of field errors from validation
     * @return Map of field names to resolved error messages
     */
    public static Map<String, String> processValidationErrors(org.springframework.validation.ObjectError[] fieldErrors) {
        Map<String, String> errors = new HashMap<>();
        for (org.springframework.validation.ObjectError error : fieldErrors) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                String fieldName = fieldError.getField();
                String errorMessage = resolveValidationErrorMessage(fieldError);
                errors.put(fieldName, errorMessage != null ? errorMessage : "");
            }
        }
        return errors;
    }

    /**
     * Prepares message arguments by including the rejected value as the first argument.
     * This allows error messages to display the invalid value.
     * 
     * @param originalArgs Original arguments from the validation error
     * @param rejectedValue The rejected value that failed validation
     * @return Array of arguments with rejected value as first element
     */
    private static Object[] prepareMessageArguments(Object[] originalArgs, Object rejectedValue) {
        if (rejectedValue == null) {
            return originalArgs != null ? originalArgs : new Object[0];
        }
        
        // Create new array with rejected value as first argument
        Object[] newArgs = new Object[(originalArgs != null ? originalArgs.length : 0) + 1];
        newArgs[0] = rejectedValue;
        if (originalArgs != null) {
            System.arraycopy(originalArgs, 0, newArgs, 1, originalArgs.length);
        }
        return newArgs;
    }


    /**
     * Builds an ApiErrorResponseDTO with the given parameters.
     * 
     * @param status HTTP status code
     * @param message Error message
     * @param errors Map of field errors (can be null)
     * @return ApiErrorResponseDTO instance
     */
    public static ApiErrorResponseDTO buildErrorResponse(HttpStatus status, String message, Map<String, String> errors) {
        ApiErrorResponseDTO.ApiErrorResponseDTOBuilder builder = ApiErrorResponseDTO.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(status.value())
                .message(message);
        
        if (errors != null && !errors.isEmpty()) {
            builder.errors(errors);
        }
        
        return builder.build();
    }

    /**
     * Extracts account identifier from AccountNotFoundException.
     * 
     * @param ex The AccountNotFoundException
     * @return Account identifier as string
     */
    public static String extractAccountIdentifier(com.interview.exception.AccountNotFoundException ex) {
        if (ex.getAccountId() != null) {
            return String.valueOf(ex.getAccountId());
        } else if (ex.getAccountReferenceId() != null) {
            return ex.getAccountReferenceId();
        } else {
            return ex.getMessage();
        }
    }
}

