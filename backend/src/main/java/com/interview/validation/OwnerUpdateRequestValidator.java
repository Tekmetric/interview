package com.interview.validation;

import com.interview.dto.owner.OwnerUpdateRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;

public class OwnerUpdateRequestValidator
    implements ConstraintValidator<ValidOwnerUpdateRequest, OwnerUpdateRequestDTO> {

  @Override
  public boolean isValid(
      final OwnerUpdateRequestDTO dto, final ConstraintValidatorContext context) {
    if (dto.getName() != null) {
      if (!dto.getName().matches("^[A-Za-z ]+$")) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Name must contain only letters and spaces")
            .addConstraintViolation();
        return false;
      }
    }

    if (dto.getPersonalNumber() != null) {
      if (!dto.getPersonalNumber().matches("^[0-9]+$")) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Personal number must contain only digits")
            .addConstraintViolation();
        return false;
      }
    }

    if (dto.getAddress() != null) {
      if (dto.getAddress().trim().isEmpty()) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Address must not be empty")
            .addConstraintViolation();
        return false;
      }
    }

    if (dto.getBirthDate() != null) {
      if (dto.getBirthDate().isAfter(Instant.now())) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("Birth date must not be in the future")
            .addConstraintViolation();
        return false;
      }
    }

    return true;
  }
}
