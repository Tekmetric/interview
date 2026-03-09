package com.interview.api.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vin;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;

class VehicleRequestTest {

    private final Validator validator;

    VehicleRequestTest() {
        try (final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testHappyPath() {
        final Set<ConstraintViolation<VehicleRequest>> violations =
                validator.validate(new VehicleRequest(new Vin("1HGBH41JXMN109186")));
        assertThat(violations).isEmpty();
    }

    @Test
    void testNullVin() {
        final Set<ConstraintViolation<VehicleRequest>> violations = validator.validate(new VehicleRequest(null));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("must not be null");
    }

    @Test
    void testInvalidVinPropagatesViolation() {
        final Set<ConstraintViolation<VehicleRequest>> violations = validator.validate(new VehicleRequest(new Vin("")));
        assertThat(violations).extracting(ConstraintViolation::getMessage).contains("VIN must not be blank");
    }
}
