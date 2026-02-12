package com.interview.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure year is not in the future.
 *
 * <p>Validates that the year is the current year or earlier, with optional
 * allowance for future years (useful for pre-orders or model years).
 */
@Documented
@Constraint(validatedBy = CurrentYearOrPastValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentYearOrPast {

    String message() default "Year cannot be in the future";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Number of future years to allow beyond current year.
     * Default is 1 to allow next model year vehicles.
     */
    int allowFutureYears() default 1;
}