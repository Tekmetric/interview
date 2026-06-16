package com.interview.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class SSNValidatorTest {

    private SSNValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SSNValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"123-45-6789", "000-00-0000", "999-99-9999"})
    void isValid_validSsn_returnsTrue(String ssn) {
        assertThat(validator.isValid(ssn, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789",
        "12-345-6789",
        "123-4-56789",
        "abc-de-fghi",
        "123-45-678",
        "123-45-67890"
    })
    void isValid_invalidSsn_returnsFalse(String ssn) {
        assertThat(validator.isValid(ssn, null)).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void isValid_null_returnsTrue(String ssn) {
        assertThat(validator.isValid(ssn, null)).isTrue();
    }
}
