package com.interview.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = LoanAmountValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoanAmount {

    String message() default "Requested loan amount exceeds the maximum allowed (5× annual income)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
