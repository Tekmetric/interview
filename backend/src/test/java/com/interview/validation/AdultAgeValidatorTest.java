package com.interview.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class AdultAgeValidatorTest {

    private final AdultAgeValidator validator = new AdultAgeValidator();

    @Test
    void isValid_exactlyEighteen_returnsTrue() {
        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
        assertThat(validator.isValid(eighteenYearsAgo, null)).isTrue();
    }

    @Test
    void isValid_overEighteen_returnsTrue() {
        LocalDate thirtyYearsAgo = LocalDate.now().minusYears(30);
        assertThat(validator.isValid(thirtyYearsAgo, null)).isTrue();
    }

    @Test
    void isValid_underEighteen_returnsFalse() {
        LocalDate seventeenYearsAgo = LocalDate.now().minusYears(17).plusDays(1);
        assertThat(validator.isValid(seventeenYearsAgo, null)).isFalse();
    }

    @Test
    void isValid_today_returnsFalse() {
        assertThat(validator.isValid(LocalDate.now(), null)).isFalse();
    }

    @Test
    void isValid_null_returnsTrue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }
}
