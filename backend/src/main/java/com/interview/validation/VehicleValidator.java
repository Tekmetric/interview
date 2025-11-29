package com.interview.validation;

import com.interview.domain.Vehicle;
import com.interview.dto.VehicleRequest;
import com.interview.exception.ValidationException;

public class VehicleValidator {

    private VehicleValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void validate(VehicleRequest vehicleRequest) {
        validateString(vehicleRequest.getBrand(), Vehicle.BRAND_MAX_LENGTH);
        validateString(vehicleRequest.getModel(), Vehicle.MODEL_MAX_LENGTH);
        validateString(vehicleRequest.getColor(), Vehicle.COLOR_MAX_LENGTH);
    }

    private static void validateString(String s, int maxLength) {
        if (s != null && s.length() > maxLength) {
            throw new ValidationException(
                    "The field exceeds the max of " + maxLength + " characters");
        }
    }
}
