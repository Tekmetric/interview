package com.interview.validation;

import com.interview.util.InputSanitizer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for SafeText annotation.
 *
 * <p>Uses Spring's HtmlUtils for sanitization and validates input based on
 * the specified text type to prevent XSS attacks and ensure data quality.
 */
public class SafeTextValidator implements ConstraintValidator<SafeText, String> {

    private SafeText.TextType textType;

    @Override
    public void initialize(SafeText constraintAnnotation) {
        this.textType = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotBlank handle null validation
        }

        return switch (textType) {
            case NAME -> InputSanitizer.isValidName(value);
            case EMAIL -> InputSanitizer.isValidEmail(value);
            case PHONE -> InputSanitizer.isValidPhone(value);
            case ADDRESS, GENERAL -> !InputSanitizer.containsUnsafeContent(value);
        };
    }
}