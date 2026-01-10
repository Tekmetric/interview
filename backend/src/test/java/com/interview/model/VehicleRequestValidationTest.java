package com.interview.model;

import com.interview.dto.VehicleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class VehicleRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void vin_shouldFailValidation_whenNot17Characters() {
        VehicleRequest req = new VehicleRequest(
                "SHORTVIN",
                "Toyota",
                "Camry",
                2020
        );

        Set<ConstraintViolation<VehicleRequest>> violations = validator.validate(req);

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("VIN must be exactly 17 characters");
    }

    @Test
    void vin_shouldPassValidation_whenExactly17Characters() {
        VehicleRequest req = new VehicleRequest(
                "12345678901234567",
                "Toyota",
                "Camry",
                2020
        );

        Set<ConstraintViolation<VehicleRequest>> violations = validator.validate(req);

        assertThat(violations).isEmpty();
    }
}
