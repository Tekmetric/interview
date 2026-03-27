package com.tekmetric.util;

import com.tekmetric.ValidationException;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ValidationUtil {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

  private static final int MAX_AGE_YEARS = 120;

  public static void validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new ValidationException("Email cannot be empty");
    }
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException("Email has invalid format: " + email);
    }
  }

  public static void validateBirthDate(LocalDate birthDate) {
    if (birthDate == null) {
      throw new ValidationException("Birth date cannot be null");
    }

    LocalDate today = LocalDate.now();

    if (birthDate.isAfter(today)) {
      throw new ValidationException("Birth date cannot be in the future");
    }

    int years = Period.between(birthDate, today).getYears();
    if (years > MAX_AGE_YEARS) {
      throw new ValidationException("User cannot be older than " + MAX_AGE_YEARS + " years");
    }
  }

  public static Year validateYearFormat(String year) {
    String yearStr = year != null ? year.trim() : "";
    if (!yearStr.matches("\\d{4}")) {
      throw new ValidationException(
          "Year must be a 4-digit number, e.g. '2021'. Provided: '" + year + "'");
    }
    int result = Integer.parseInt(yearStr);

    int currentYear = Year.now().getValue();
    if (result > currentYear) {
      throw new ValidationException(
          "Year cannot be in the future. Provided: %d, current year: %d"
              .formatted(result, currentYear));
    }

    return Year.of(result);
  }
}
