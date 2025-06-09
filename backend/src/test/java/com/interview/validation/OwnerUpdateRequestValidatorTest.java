package com.interview.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.interview.dto.owner.OwnerUpdateRequestDTO;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnerUpdateRequestValidatorTest {

  private OwnerUpdateRequestValidator validator;

  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder builder;

  @BeforeEach
  void setUp() {
    validator = new OwnerUpdateRequestValidator();
  }

  @Test
  void validFieldsShouldPass() {
    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    dto.setName("John Doe");
    dto.setPersonalNumber("123456");
    dto.setAddress("Some Street");
    dto.setBirthDate(Instant.parse("1990-01-01T00:00:00Z"));

    final boolean result = validator.isValid(dto, context);
    assertTrue(result);
    verify(context, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void invalidNameShouldFail() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);

    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    dto.setName("John123");

    final boolean result = validator.isValid(dto, context);
    assertFalse(result);
    verify(context)
        .buildConstraintViolationWithTemplate(ArgumentMatchers.contains("letters and spaces"));
  }

  @Test
  void invalidPersonalNumberShouldFail() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);

    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    dto.setPersonalNumber("abc123");

    final boolean result = validator.isValid(dto, context);
    assertFalse(result);
    verify(context).buildConstraintViolationWithTemplate(ArgumentMatchers.contains("only digits"));
  }

  @Test
  void emptyAddressShouldFail() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);

    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    dto.setAddress("   ");

    final boolean result = validator.isValid(dto, context);
    assertFalse(result);
    verify(context).buildConstraintViolationWithTemplate(ArgumentMatchers.contains("not be empty"));
  }

  @Test
  void futureBirthDateShouldFail() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    when(builder.addConstraintViolation()).thenReturn(context);

    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    dto.setBirthDate(Instant.now().plusSeconds(3600));

    final boolean result = validator.isValid(dto, context);
    assertFalse(result);
    verify(context)
        .buildConstraintViolationWithTemplate(ArgumentMatchers.contains("not be in the future"));
  }

  @Test
  void nullFieldsShouldPass() {
    final OwnerUpdateRequestDTO dto = new OwnerUpdateRequestDTO();
    final boolean result = validator.isValid(dto, context);
    assertTrue(result);
    verify(context, never()).buildConstraintViolationWithTemplate(anyString());
  }
}
