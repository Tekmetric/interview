package com.interview.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OwnerUpdateRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOwnerUpdateRequest {

  String message() default "Invalid owner update request";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
