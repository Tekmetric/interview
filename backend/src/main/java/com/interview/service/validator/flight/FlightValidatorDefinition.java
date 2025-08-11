package com.interview.service.validator.flight;

import com.interview.common.dto.FlightRequestDto;
import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
 * Typed command definitions used by flight-related {@code BusinessValidator}s.
 * <p>
 * Each nested record represents a specific validation scenario (e.g. create, update,
 * existence check, time order, plane availability, code uniqueness) and carries the
 * minimum data required for that rule. These records are dispatched to matching
 * validators via {@code ValidatorProvider}.
 */
public interface FlightValidatorDefinition {

    record Update(int flightId, @Valid FlightRequestDto flightRequestDto) {}

    record Create(@Valid FlightRequestDto flightRequestDto) {}

    record Delete(int flightId) {}

    record Exist(int flightId) {}

    record CodeUnique(String code, Integer excludeFlightId) {}

    record TimeOrder(java.time.LocalDateTime departureTime, java.time.LocalDateTime arrivalTime) {}

    record PlaneAvailable(Integer excludeFlightId, int planeId, LocalDateTime newDeparture, LocalDateTime newArrival) {}
}
