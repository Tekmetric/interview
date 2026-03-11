package com.interview.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PhoneNumberTest {

    private final Validator validator;

    PhoneNumberTest() {
        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testHappyPath() {
        final Set<ConstraintViolation<PhoneNumber>> violations =
                validator.validate(new PhoneNumber("+1 (555) 867-5309"));
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validPhoneNumbers")
    void testValidFormats(String reason, String phoneNumber) {
        final Set<ConstraintViolation<PhoneNumber>> violations = validator.validate(new PhoneNumber(phoneNumber));
        assertThat(violations).isEmpty();
    }

    static Stream<Arguments> validPhoneNumbers() {
        return Stream.of(
                Arguments.of("digits only", "5558675309"),
                Arguments.of("with dashes", "555-867-5309"),
                Arguments.of("with spaces", "555 867 5309"),
                Arguments.of("with parens", "(555) 867-5309"),
                Arguments.of("international", "+15558675309"),
                Arguments.of("minimum length", "5558675"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidPhoneNumbers")
    void testValidationErrors(String reason, String phoneNumber, String expectedMessage) {
        final Set<ConstraintViolation<PhoneNumber>> violations = validator.validate(new PhoneNumber(phoneNumber));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains(expectedMessage);
    }

    static Stream<Arguments> invalidPhoneNumbers() {
        return Stream.of(
                Arguments.of("null", null, "Phone number must not be blank"),
                Arguments.of("empty", "", "Phone number must not be blank"),
                Arguments.of("blank", "   ", "Phone number must not be blank"),
                Arguments.of("too short", "555867", "Phone number contains invalid characters"),
                Arguments.of("too long", "123456789012345678901", "Phone number must not exceed 20 characters"),
                Arguments.of("contains letters", "555-ABC-5309", "Phone number contains invalid characters"),
                Arguments.of("contains special chars", "555@867#5309", "Phone number contains invalid characters"));
    }
}
