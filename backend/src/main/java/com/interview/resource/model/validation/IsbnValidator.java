package com.interview.resource.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String cleaned = value.replaceAll("[\\s-]", "");
        if (cleaned.length() != 13) {
            return false;
        }

        return isValidIsbn13(cleaned);
    }

    // From wikipedia, maybe incorrect
    private boolean isValidIsbn13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(isbn.charAt(i)))
                return false;
            sum += (i % 2 == 0) ? (isbn.charAt(i) - '0') : (isbn.charAt(i) - '0') * 3;
        }
        return sum % 10 == 0;
    }
}