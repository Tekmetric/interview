package com.interview.service.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class FlightPlaneAvailableValidator implements BusinessValidator {

    private final FlightRepository flightRepository;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.PlaneAvailable.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.PlaneAvailable flightPlaneAvailable = (FlightValidatorDefinition.PlaneAvailable) target;

        boolean overlap = (flightPlaneAvailable.excludeFlightId() == null)
                ? flightRepository.existsOverlap(flightPlaneAvailable.planeId(), flightPlaneAvailable.newDeparture(), flightPlaneAvailable.newArrival())
                : flightRepository.existsOverlapExcluding(flightPlaneAvailable.planeId(), flightPlaneAvailable.excludeFlightId(), flightPlaneAvailable.newDeparture(),
                                                          flightPlaneAvailable.newArrival());

        if (overlap) {
            errors.reject("plane.occupied", "Plane is already assigned to another flight in this time window");
        }
    }
}
