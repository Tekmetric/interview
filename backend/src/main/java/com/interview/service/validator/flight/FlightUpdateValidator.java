package com.interview.service.validator.flight;

import com.interview.common.dto.FlightRequestDto;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.enums.FlightEnum;
import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.BusinessValidator;
import com.interview.service.validator.ValidatorProvider;
import com.interview.service.validator.plane.PlaneValidatorDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FlightUpdateValidator implements BusinessValidator {

    private static final List<FlightEnum.Status> CANNOT_CHANGE_SET_STATUS_LIST = Arrays.asList(FlightEnum.Status.BOARDING,
                                                                                               FlightEnum.Status.IN_AIR,
                                                                                               FlightEnum.Status.DELAYED,
                                                                                               FlightEnum.Status.LANDED,
                                                                                               FlightEnum.Status.CANCELLED);

    private final FlightRepository flightRepository;
    private final ValidatorProvider validatorProvider;

    @Override
    public boolean supports(Class<?> paramClass) {
        return FlightValidatorDefinition.Update.class.equals(paramClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightValidatorDefinition.Update flightUpdate = (FlightValidatorDefinition.Update) target;
        FlightRequestDto requestDto = flightUpdate.flightRequestDto();

        validatorProvider.validate(new PlaneValidatorDefinition.Exist(requestDto.getPlaneId()));
        validatorProvider.validate(new FlightValidatorDefinition.Exist(flightUpdate.flightId()));
        validatorProvider.validate(new FlightValidatorDefinition.CodeUnique(requestDto.getCode(), flightUpdate.flightId()));
        validatorProvider.validate(new FlightValidatorDefinition.TimeOrder(requestDto.getDepartureTime(), requestDto.getArrivalTime()));
        validatorProvider.validate(new FlightValidatorDefinition.PlaneAvailable(flightUpdate.flightId(), requestDto.getPlaneId(), requestDto.getDepartureTime(),
                                                                                requestDto.getArrivalTime()));

        validateSeatsChangeWhenScheduled(flightUpdate.flightId(), requestDto, errors);

    }

    private void validateSeatsChangeWhenScheduled(int flightId, FlightRequestDto dto, Errors errors) {
        Optional<Flight> existingOpt = flightRepository.findById(flightId);
        if (existingOpt.isEmpty()) {
            return;
        }
        Flight existing = existingOpt.get();

        if (CANNOT_CHANGE_SET_STATUS_LIST.contains(existing.getStatus())
                && !Objects.equals(existing.getAvailableSeats(), dto.getAvailableSeats())) {
            errors.reject("seats.locked",
                          "availableSeats cannot be changed when status is " + FlightEnum.Status.SCHEDULED);
        }
    }
}
