package com.interview.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AdultAgeValidator implements ConstraintValidator<ValidAdultAge, LocalDate> {

    private static final int MINIMUM_AGE = 18;

    @Override
    public boolean isValid(final LocalDate dateOfBirth, final ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true;
        }
        return !LocalDate.now().isBefore(dateOfBirth.plusYears(MINIMUM_AGE));
    }
}
