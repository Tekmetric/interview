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

class VinTest {

    private final Validator validator;

    VinTest() {
        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testHappyPath() {
        final Set<ConstraintViolation<Vin>> violations = validator.validate(new Vin("1HGBH41JXMN109186"));
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidVins")
    void testValidationErrors(String reason, String vinString, String expectedMessage) {
        final Set<ConstraintViolation<Vin>> violations = validator.validate(new Vin(vinString));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains(expectedMessage);
    }

    static Stream<Arguments> invalidVins() {
        return Stream.of(
                Arguments.of("null", null, "VIN must not be blank"),
                Arguments.of("empty", "", "VIN must not be blank"),
                Arguments.of("blank", "   ", "VIN must not be blank"),
                Arguments.of("too short", "1HGBH41JXMN", "VIN must be exactly 17 characters"),
                Arguments.of("too long", "1HGBH41JXMN109186A", "VIN must be exactly 17 characters"),
                Arguments.of("contains I", "1HGBH41JXMNI09186", "VIN contains invalid characters"),
                Arguments.of("contains O", "1HGBH41JXMNO09186", "VIN contains invalid characters"),
                Arguments.of("contains Q", "1HGBH41JXMNQ09186", "VIN contains invalid characters"));
    }
}
