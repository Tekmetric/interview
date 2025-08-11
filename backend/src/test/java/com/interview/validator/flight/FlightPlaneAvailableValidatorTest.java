package com.interview.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.flight.FlightPlaneAvailableValidator;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import com.interview.service.validator.flight.FlightValidatorDefinition.PlaneAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FlightPlaneAvailableValidatorTest {

    private FlightRepository repo;
    private FlightPlaneAvailableValidator validator;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(FlightRepository.class);
        validator = new FlightPlaneAvailableValidator(repo);
    }

    @Test
    void ok_whenNoOverlap_onCreate() {
        LocalDateTime dep = LocalDateTime.parse("2025-08-11T09:00:00");
        LocalDateTime arr = LocalDateTime.parse("2025-08-11T11:00:00");
        when(repo.existsOverlap(1, dep, arr)).thenReturn(false);

        PlaneAvailable cmd = new PlaneAvailable(null, 1, dep, arr);
        Errors errors = new BeanPropertyBindingResult(cmd, "planeAvailable");

        validator.validate(cmd, errors);
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void error_whenOverlap_onUpdate() {
        LocalDateTime dep = LocalDateTime.parse("2025-08-11T09:00:00");
        LocalDateTime arr = LocalDateTime.parse("2025-08-11T11:00:00");
        when(repo.existsOverlapExcluding(1, 99, dep, arr)).thenReturn(true);

        PlaneAvailable cmd = new PlaneAvailable(99, 1, dep, arr);
        Errors errors = new BeanPropertyBindingResult(cmd, "planeAvailable");

        validator.validate(cmd, errors);
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("plane.occupied");
    }
}
