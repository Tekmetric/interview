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

@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
@ReportAsSingleViolation
@Constraint(validatedBy = {})
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface ValidPhone {

    String message() default "Phone must be in E.164 format (e.g. +15555550100)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
