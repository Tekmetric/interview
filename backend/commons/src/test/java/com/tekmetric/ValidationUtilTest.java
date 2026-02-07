package com.tekmetric;

import static org.junit.jupiter.api.Assertions.*;

import com.tekmetric.util.ValidationUtil;
import java.time.LocalDate;
import java.time.Year;
import org.junit.jupiter.api.Test;

class ValidationUtilTest {

  // ---------- validateEmail ----------

  @Test
  void validateEmail_validEmail_doesNotThrow() {
    assertDoesNotThrow(() -> ValidationUtil.validateEmail("user@example.com"));
  }

  @Test
  void validateEmail_nullEmail_throwsValidationException() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail(null));
    assertEquals("Email cannot be empty", ex.getMessage());
  }

  @Test
  void validateEmail_blankEmail_throwsValidationException() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail("   "));
    assertEquals("Email cannot be empty", ex.getMessage());
  }

  @Test
  void validateEmail_invalidFormat_throwsValidationException() {
    String bad = "not-an-email";
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateEmail(bad));
    assertTrue(ex.getMessage().contains("Email has invalid format"));
    assertTrue(ex.getMessage().contains(bad));
  }

  // ---------- validateBirthDate ----------

  @Test
  void validateBirthDate_validDate_doesNotThrow() {
    LocalDate now = LocalDate.now();
    LocalDate valid = now.minusYears(30); // 30 years old
    assertDoesNotThrow(() -> ValidationUtil.validateBirthDate(valid));
  }

  @Test
  void validateBirthDate_null_throwsValidationException() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateBirthDate(null));
    assertEquals("Birth date cannot be null", ex.getMessage());
  }

  @Test
  void validateBirthDate_futureDate_throwsValidationException() {
    LocalDate future = LocalDate.now().plusDays(1);
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateBirthDate(future));
    assertEquals("Birth date cannot be in the future", ex.getMessage());
  }

  @Test
  void validateBirthDate_tooOld_throwsValidationException() {
    LocalDate now = LocalDate.now();
    LocalDate tooOld = now.minusYears(121);
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateBirthDate(tooOld));
    assertTrue(ex.getMessage().contains("User cannot be older than 120 years"));
  }

  // ---------- validateYearFormat ----------

  @Test
  void validateYearFormat_validYear_returnsYear() {
    Year year = ValidationUtil.validateYearFormat("2020");
    assertEquals(2020, year.getValue());
  }

  @Test
  void validateYearFormat_trimsInput() {
    Year year = ValidationUtil.validateYearFormat("  2019  ");
    assertEquals(2019, year.getValue());
  }

  @Test
  void validateYearFormat_nonFourDigits_throwsValidationException() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> ValidationUtil.validateYearFormat("20"));
    assertTrue(ex.getMessage().contains("Year must be a 4-digit number"));
  }

  @Test
  void validateYearFormat_futureYear_throwsValidationException() {
    int nextYear = Year.now().getValue() + 1;
    ValidationException ex =
        assertThrows(
            ValidationException.class,
            () -> ValidationUtil.validateYearFormat(String.valueOf(nextYear)));
    assertTrue(ex.getMessage().contains("Year cannot be in the future"));
  }
}
