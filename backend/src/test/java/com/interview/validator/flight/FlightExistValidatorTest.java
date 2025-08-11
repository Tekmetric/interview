package com.interview.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.flight.FlightExistValidator;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import com.interview.service.validator.flight.FlightValidatorDefinition.Exist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FlightExistValidatorTest {

    private FlightRepository repo;
    private FlightExistValidator validator;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(FlightRepository.class);
        validator = new FlightExistValidator(repo);
    }

    @Test
    void ok_whenFlightExists() {
        when(repo.existsById(10)).thenReturn(true);

        Exist cmd = new Exist(10);
        Errors errors = new BeanPropertyBindingResult(cmd, "exist");

        validator.validate(cmd, errors);
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void error_whenFlightMissing() {
        when(repo.existsById(10)).thenReturn(false);

        Exist cmd = new Exist(10);
        Errors errors = new BeanPropertyBindingResult(cmd, "exist");

        validator.validate(cmd, errors);
        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("flight.notFound");
    }
}
