package com.interview.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure text fields are safe from XSS attacks.
 *
 * <p>Validates that the input contains only safe characters and patterns
 * appropriate for the specified field type.
 */
@Documented
@Constraint(validatedBy = SafeTextValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeText {

    String message() default "Input contains potentially unsafe characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Type of text field for specific validation rules.
     */
    TextType type() default TextType.GENERAL;

    /**
     * Enumeration of text field types for targeted validation rules.
     *
     * <p>Each type applies specific character restrictions and validation patterns
     * appropriate for the field's intended use case.
     */
    enum TextType {
        GENERAL,    // General text with basic XSS protection
        NAME,       // Names (letters, spaces, apostrophes, hyphens)
        EMAIL,      // Email format validation
        PHONE,      // Phone number format
        ADDRESS     // Address with extended character support
    }
}