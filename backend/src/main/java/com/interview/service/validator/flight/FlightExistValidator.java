package com.interview.service.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightExistValidator implements BusinessValidator {

    private final FlightRepository flightRepository;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.Exist.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.Exist flightExist = (FlightValidatorDefinition.Exist) target;

        if (!flightRepository.existsById(flightExist.flightId())) {
            errors.reject( "flight.notFound", "Flight does not exist");
        }
    }
}
