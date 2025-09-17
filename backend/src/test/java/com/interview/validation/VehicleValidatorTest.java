package com.interview.validation;

import com.interview.dto.VehicleRequest;
import com.interview.exception.ValidationException;
import com.interview.test.data.TestData;
import com.interview.test.data.VehicleTestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleValidatorTest {

    @Test
    void shouldValidate() {
        // given
        VehicleRequest vehicleRequest = VehicleTestData.vehicleRequest();

        // then
        VehicleValidator.validate(vehicleRequest);
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidFields() {
        // given
        VehicleRequest vehicleRequest = VehicleTestData.vehicleRequest();
        vehicleRequest.setBrand(TestData.LONG_STRING);

        // then
        assertThrows(
                ValidationException.class,
                () -> VehicleValidator.validate(vehicleRequest));
    }
}
