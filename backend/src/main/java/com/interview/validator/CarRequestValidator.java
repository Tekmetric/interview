package com.interview.validator;

import java.time.LocalDate;

import com.interview.dto.CarRequest;
import com.interview.exception.InvalidCarDataException;
import com.interview.model.CarStatus;
import org.springframework.stereotype.Component;

@Component
public class CarRequestValidator {

    public void validate(CarRequest request) {
        CarStatus status = request.status() != null ? request.status() : CarStatus.AVAILABLE;

        switch (status) {
            case RESERVED, SOLD -> {
                if (request.sellingPrice() == null) {
                    throw new InvalidCarDataException(
                            "Selling price is required when status is " + status);
                }
            }
            case AVAILABLE -> {
                if (request.sellingPrice() != null) {
                    throw new InvalidCarDataException(
                            "Selling price must be null when status is AVAILABLE");
                }
            }
        }

        if (request.manufacturedYear() > LocalDate.now().getYear()){
            throw new InvalidCarDataException(
                "Manufactured year can not be in the future");
        };
    }
}
