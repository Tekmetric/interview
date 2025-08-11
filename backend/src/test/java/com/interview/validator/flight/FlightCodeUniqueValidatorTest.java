package com.interview.validator.flight;

import com.interview.jpa.repository.FlightRepository;
import com.interview.service.validator.flight.FlightCodeUniqueValidator;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import com.interview.service.validator.flight.FlightValidatorDefinition.CodeUnique;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FlightCodeUniqueValidatorTest {

    private FlightRepository repo;
    private FlightCodeUniqueValidator validator;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(FlightRepository.class);
        validator = new FlightCodeUniqueValidator(repo);
    }

    @Test
    void ok_whenCodeIsUnique_onCreate() {
        when(repo.existsByCode("TM100")).thenReturn(false);

        CodeUnique cmd = new CodeUnique("TM100", null);
        Errors errors = new BeanPropertyBindingResult(cmd, "codeUnique");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void error_whenCodeExists_onCreate() {
        when(repo.existsByCode("TM100")).thenReturn(true);

        CodeUnique cmd = new CodeUnique("TM100", null);
        Errors errors = new BeanPropertyBindingResult(cmd, "codeUnique");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("code.duplicate");
    }

    @Test
    void ok_whenCodeExistsButDifferentFlight_onUpdate() {
        when(repo.existsByCodeAndIdNot("TM100", 1)).thenReturn(false);

        CodeUnique cmd = new CodeUnique("TM100", 1);
        Errors errors = new BeanPropertyBindingResult(cmd, "codeUnique");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }
}
