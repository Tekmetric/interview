package com.interview.service.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightCodeUniqueValidator implements BusinessValidator {

    private final FlightRepository flightRepository;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.CodeUnique.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.CodeUnique flightCodeUnique = (FlightValidatorDefinition.CodeUnique) target;

        boolean exists = (flightCodeUnique.excludeFlightId() == null)
                ? flightRepository.existsByCode(flightCodeUnique.code())
                : flightRepository.existsByCodeAndIdNot(flightCodeUnique.code(), flightCodeUnique.excludeFlightId());
        if (exists) {
            errors.reject("code.duplicate", "A flight with this code already exists");
        }
    }
}
