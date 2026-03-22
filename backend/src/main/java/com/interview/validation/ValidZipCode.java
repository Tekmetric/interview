package com.interview.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Pattern(regexp = "^\\d{5}(-\\d{4})?$")
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface ValidZipCode {

    String message() default "Zip code must be 5 or 9 digits (e.g. 78701 or 78701-1234)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
