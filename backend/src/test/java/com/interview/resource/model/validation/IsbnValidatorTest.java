package com.interview.resource.model.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsbnValidatorTest {

    private IsbnValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IsbnValidator();
    }

    @Test
    void validIsbn_returnsTrue() {
        assertThat(validator.isValid("978-0-13-235088-4", null)).isTrue();
        assertThat(validator.isValid("9780132350884", null)).isTrue();
        assertThat(validator.isValid("978 0 13 235088 4", null)).isTrue();
    }

    @Test
    void nullOrBlankIsValid() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void invalidIsbn_returnsFalse() {
        assertThat(validator.isValid("1234567890123", null)).isFalse();
        assertThat(validator.isValid("978-0-13-235088-5", null)).isFalse();
        assertThat(validator.isValid("978013235088", null)).isFalse();
        assertThat(validator.isValid("97801323508844", null)).isFalse();
        assertThat(validator.isValid("97801323508A4", null)).isFalse();
    }
}