package com.interview.validator.flight;

import com.interview.service.validator.flight.FlightTimeOrderValidator;
import com.interview.service.validator.flight.FlightValidatorDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FlightTimeOrderValidatorTest {

    private final FlightTimeOrderValidator validator = new FlightTimeOrderValidator();

    @Test
    void ok_whenArrivalAfterDeparture() {
        var cmd = new FlightValidatorDefinition.TimeOrder(
                LocalDateTime.parse("2025-08-11T09:00:00"),
                LocalDateTime.parse("2025-08-11T10:00:00")
        );
        Errors errors = new BeanPropertyBindingResult(cmd, "timeOrder");
        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void error_whenArrivalNotAfterDeparture() {
        var cmd = new FlightValidatorDefinition.TimeOrder(
                LocalDateTime.parse("2025-08-11T09:00:00"),
                LocalDateTime.parse("2025-08-11T09:00:00")
        );
        Errors errors = new BeanPropertyBindingResult(cmd, "timeOrder");
        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("time.order");
    }
}
