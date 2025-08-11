package com.interview.service.flight;

import com.interview.common.dto.FlightDto;
import com.interview.common.dto.FlightRequestDto;
import com.interview.common.exception.ValidationException;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.Plane;
import com.interview.jpa.repository.FlightRepository;
import com.interview.jpa.repository.PlaneRepository;
import com.interview.service.mapper.ModelMapperProvider;
import com.interview.service.mapper.flight.FlightMapperDefinition;
import com.interview.service.validator.ValidatorProvider;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightManagementService {

    private final ValidatorProvider validatorProvider;
    private final ModelMapperProvider modelMapperProvider;
    private final FlightRepository flightRepository;
    private final PlaneRepository planeRepository;

    /**
     * Validates input and creates a new flight.
     *
     * @param flightRequestDto input payload
     * @return created {@link FlightDto}
     * @throws ValidationException on invalid data
     */
    public FlightDto create(FlightRequestDto flightRequestDto) {
        validatorProvider.validate(new FlightValidatorDefinition.Create(flightRequestDto));

        Plane plane = planeRepository.getReferenceById(flightRequestDto.getPlaneId());

        Flight flight = modelMapperProvider.mapTo(new FlightMapperDefinition.FlightRequestDtoToFlight(flightRequestDto, plane), Flight.class);
        flight = flightRepository.save(flight);

        return modelMapperProvider.mapTo(new FlightMapperDefinition.FlightToFlightDto(flight), FlightDto.class);
    }

    /**
     * Validates input and updates an existing flight.
     *
     * @param flightId         flight identifier
     * @param flightRequestDto update payload
     * @return updated {@link FlightDto}
     * @throws ValidationException on invalid data or missing flight
     */
    public FlightDto update(int flightId, FlightRequestDto flightRequestDto) {
        validatorProvider.validate(new FlightValidatorDefinition.Update(flightId, flightRequestDto));

        Plane plane = planeRepository.getReferenceById(flightRequestDto.getPlaneId());
        Flight flight = flightRepository.getReferenceById(flightId);

        modelMapperProvider.mapTo(new FlightMapperDefinition.FlightRequestDtoToFlight(flightRequestDto, plane), flight);

        return modelMapperProvider.mapTo(new FlightMapperDefinition.FlightToFlightDto(flight), FlightDto.class);
    }

    /**
     * Validates input and deletes a flight by id.
     *
     * @param flightId flight identifier
     * @throws ValidationException if the flight does not exist
     */
    public void delete(int flightId) {
        validatorProvider.validate(new FlightValidatorDefinition.Exist(flightId));

        flightRepository.deleteById(flightId);
    }
}
