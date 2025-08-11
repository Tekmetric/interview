package com.interview.service.validator.flight;

import com.interview.common.dto.FlightRequestDto;
import com.interview.service.validator.BusinessValidator;
import com.interview.service.validator.ValidatorProvider;
import com.interview.service.validator.plane.PlaneValidatorDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightCreateValidator implements BusinessValidator {

    private final ValidatorProvider validatorProvider;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.Create.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.Create flightCreate = (FlightValidatorDefinition.Create) target;
        FlightRequestDto flightRequestDto = flightCreate.flightRequestDto();

        validatorProvider.validate(new PlaneValidatorDefinition.Exist(flightRequestDto.getPlaneId()));
        validatorProvider.validate(new FlightValidatorDefinition.CodeUnique(flightRequestDto.getCode(), null));
        validatorProvider.validate(new FlightValidatorDefinition.TimeOrder(flightRequestDto.getDepartureTime(), flightRequestDto.getArrivalTime()));
        validatorProvider.validate(new FlightValidatorDefinition.PlaneAvailable(null, flightRequestDto.getPlaneId(), flightRequestDto.getDepartureTime(),
                                                                                flightRequestDto.getArrivalTime()));
    }
}
