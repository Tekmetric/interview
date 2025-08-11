package com.interview.service.mapper.flight;

import com.interview.common.dto.FlightRequestDto;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.Plane;

/**
 * Mapping Flight request records used by the ModelMapperProvider.
 **/
public interface FlightMapperDefinition {

    record FlightToFlightDto(Flight flight) {}

    record FlightRequestDtoToFlight(FlightRequestDto flight, Plane plane) {}
}
