package com.interview.validator.flight;

import com.interview.common.dto.FlightRequestDto;
import com.interview.jpa.entity.Flight;
import com.interview.jpa.entity.enums.FlightEnum;
import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.ValidatorProvider;
import com.interview.service.validator.flight.FlightUpdateValidator;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import com.interview.service.validator.flight.FlightValidatorDefinition.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightUpdateValidatorTest {

    @Mock
    FlightRepository flightRepository;

    @Mock
    ValidatorProvider validatorProvider;

    @InjectMocks
    FlightUpdateValidator validator;

    private static FlightRequestDto sampleDto(int planeId, int seats) {
        FlightRequestDto dto = new FlightRequestDto();
        dto.setCode("ANY123");
        dto.setDepartureAirport("OTP");
        dto.setArrivalAirport("LHR");
        dto.setDepartureTime(LocalDateTime.of(2025, 8, 11, 9, 30));
        dto.setArrivalTime(LocalDateTime.of(2025, 8, 11, 11, 50));
        dto.setStatus(FlightEnum.Status.SCHEDULED);
        dto.setAvailableSeats(seats);
        dto.setCurrency("EUR");
        dto.setPlaneId(planeId);
        return dto;
    }

    @Test
    @DisplayName("Seats change + status locked (e.g., IN_AIR) -> rejects with seats.locked")
    void seatsChangedAndLockedStatus_rejects() {
        int flightId = 1;

        Flight existing = new Flight();
        existing.setId(flightId);
        existing.setStatus(FlightEnum.Status.IN_AIR);
        existing.setAvailableSeats(150);

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(existing));
        doNothing().when(validatorProvider).validate(any());

        FlightRequestDto dto = sampleDto(1, 140);

        Update cmd = new Update(flightId, dto);
        Errors errors = new BeanPropertyBindingResult(cmd, "update");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getAllErrors())
                .anySatisfy(err -> assertThat(err.getCode()).isEqualTo("seats.locked"));
    }

    @Test
    @DisplayName("Seats change + status not locked (SCHEDULED) -> no error")
    void seatsChangedAndNotLockedStatus_ok() {
        int flightId = 2;

        Flight existing = new Flight();
        existing.setId(flightId);
        existing.setStatus(FlightEnum.Status.SCHEDULED);
        existing.setAvailableSeats(150);

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(existing));
        doNothing().when(validatorProvider).validate(any());

        FlightRequestDto dto = sampleDto(1, 140);

        Update cmd = new Update(flightId, dto);
        Errors errors = new BeanPropertyBindingResult(cmd, "update");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    @DisplayName("Seats unchanged + status locked -> no error")
    void seatsUnchangedAndLockedStatus_ok() {
        int flightId = 3;

        Flight existing = new Flight();
        existing.setId(flightId);
        existing.setStatus(FlightEnum.Status.BOARDING);
        existing.setAvailableSeats(150);

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(existing));
        doNothing().when(validatorProvider).validate(any());

        FlightRequestDto dto = sampleDto(1, 150);

        Update cmd = new Update(flightId, dto);
        Errors errors = new BeanPropertyBindingResult(cmd, "update");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }
}