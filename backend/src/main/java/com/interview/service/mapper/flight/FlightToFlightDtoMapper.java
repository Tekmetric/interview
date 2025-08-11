package com.interview.service.mapper.flight;

import com.interview.common.dto.FlightDto;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.Plane;
import com.interview.service.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlightToFlightDtoMapper implements ModelMapper<FlightMapperDefinition.FlightToFlightDto, FlightDto> {

    @Override
    public boolean supports(Class<?> clazz) {
        return FlightMapperDefinition.FlightToFlightDto.class.equals(clazz);
    }

    @Override
    public FlightDto mapTo(FlightMapperDefinition.FlightToFlightDto flightToFlightDto) {
        FlightDto dto = new FlightDto();
        mapTo(flightToFlightDto, dto);

        return dto;
    }

    @Override
    public void mapTo(FlightMapperDefinition.FlightToFlightDto flightToFlightDto, FlightDto dto) {
        Flight flight = flightToFlightDto.flight();

        dto.setId(flight.getId());
        dto.setVersion(flight.getVersion());
        dto.setCode(flight.getCode());
        dto.setDepartureAirport(flight.getDepartureAirport());
        dto.setArrivalAirport(flight.getArrivalAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setStatus(flight.getStatus());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setPrice(flight.getPrice());
        dto.setCurrency(flight.getCurrency());
        dto.setTerminal(flight.getTerminal());
        dto.setGate(flight.getGate());

        Plane plane = flight.getPlane();
        dto.setPlaneId(plane.getId());
        dto.setPlaneRegistration(plane.getRegistrationNumber());
        dto.setPlaneModel(plane.getManufacturer() + " " + plane.getModel());

        dto.setCreatedDate(flight.getCreatedDate());
        dto.setUpdatedDate(flight.getUpdatedDate());
        dto.setCreatedByUsername(flight.getCreatedBy().getUsername());
        dto.setUpdatedByUsername(flight.getUpdatedBy().getUsername());
    }
}
