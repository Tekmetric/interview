package com.interview.service.validator.flight;

import com.interview.service.validator.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightTimeOrderValidator implements BusinessValidator {

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.TimeOrder.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.TimeOrder FlightTimeOrder = (FlightValidatorDefinition.TimeOrder) target;

        if (FlightTimeOrder.departureTime() != null && FlightTimeOrder.arrivalTime() != null
                && !FlightTimeOrder.arrivalTime().isAfter(FlightTimeOrder.departureTime())) {
            errors.reject( "time.order", "arrivalTime must be after departureTime");
        }
    }
}
