package com.interview.service.validator.flight;

import com.interview.service.validator.BusinessValidator;
import com.interview.service.validator.ValidatorProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightDeleteValidator implements BusinessValidator {

    private final ValidatorProvider validatorProvider;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.Delete.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.Delete flightDelete = (FlightValidatorDefinition.Delete) target;

        validatorProvider.validate(new FlightValidatorDefinition.Exist(flightDelete.flightId()));
    }
}
