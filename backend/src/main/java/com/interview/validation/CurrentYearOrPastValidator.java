package com.interview.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

/**
 * Validator implementation for CurrentYearOrPast annotation.
 *
 * <p>Validates that the year is not in the future (current year or earlier).
 * Uses Java's Year.now() to get the current year dynamically.
 */
public class CurrentYearOrPastValidator implements ConstraintValidator<CurrentYearOrPast, Integer> {

    private int allowFutureYears;

    @Override
    public void initialize(CurrentYearOrPast constraintAnnotation) {
        this.allowFutureYears = constraintAnnotation.allowFutureYears();
    }

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) {
            return true; // Let @NotNull handle null validation
        }

        int currentYear = Year.now().getValue();
        int maxAllowedYear = currentYear + allowFutureYears;

        return year <= maxAllowedYear;
    }
}