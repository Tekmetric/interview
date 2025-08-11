package com.interview.service.mapper.flight;

import com.interview.common.dto.FlightRequestDto;
import com.interview.jpa.entity.Flight;
import com.interview.service.mapper.ModelMapper;
import com.interview.service.mapper.flight.FlightMapperDefinition.FlightRequestDtoToFlight;
import com.interview.service.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.upperCase;

@Component
@RequiredArgsConstructor
public class FlightRequestDtoToFlightMapper implements ModelMapper<FlightRequestDtoToFlight, Flight> {

    private final UserContext userContext;

    @Override
    public boolean supports(Class<?> clazz) {
        return FlightRequestDtoToFlight.class.equals(clazz);
    }

    @Override
    public Flight mapTo(FlightRequestDtoToFlight flightRequestDtoToFlight) {
        Flight flight = new Flight();
        flight.setCreatedBy(userContext.getCurrentUser());

        mapTo(flightRequestDtoToFlight, flight);

        return flight;
    }

    @Override
    public void mapTo(FlightRequestDtoToFlight flightRequestDtoToFlight, Flight flight) {
        FlightRequestDto dto = flightRequestDtoToFlight.flight();

        flight.setCode(trim(dto.getCode()));
        flight.setPlane(flightRequestDtoToFlight.plane());
        flight.setDepartureAirport(upperCase(dto.getDepartureAirport()));
        flight.setArrivalAirport(upperCase(dto.getArrivalAirport()));
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setStatus(dto.getStatus());
        flight.setAvailableSeats(dto.getAvailableSeats());
        flight.setPrice(dto.getPrice());
        flight.setCurrency(upperCase(dto.getCurrency()));
        flight.setTerminal(trim(dto.getTerminal()));
        flight.setGate(trim(dto.getGate()));
        flight.setUpdatedBy(userContext.getCurrentUser());
    }
}
