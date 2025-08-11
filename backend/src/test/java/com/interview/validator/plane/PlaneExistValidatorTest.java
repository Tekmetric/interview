package com.interview.service.validator.plane;

import com.interview.jpa.repository.PlaneRepository;
import com.interview.service.validator.plane.PlaneValidatorDefinition.Exist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PlaneExistValidatorTest {

    private PlaneRepository repo;
    private PlaneExistValidator validator;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(PlaneRepository.class);
        validator = new PlaneExistValidator(repo);
    }

    @Test
    void ok_whenPlaneExists() {
        when(repo.existsById(5)).thenReturn(true);

        Exist cmd = new Exist(5);
        Errors errors = new BeanPropertyBindingResult(cmd, "exist");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    void error_whenPlaneMissing() {
        when(repo.existsById(5)).thenReturn(false);

        Exist cmd = new Exist(5);
        Errors errors = new BeanPropertyBindingResult(cmd, "exist");

        validator.validate(cmd, errors);

        assertThat(errors.hasErrors()).isTrue();
        assertThat(errors.hasFieldErrors("planeId")).isTrue();
        var fe = errors.getFieldError("planeId");
        assertThat(fe).isNotNull();
        assertThat(fe.getCode()).isEqualTo("plane.notFound");
        assertThat(fe.getDefaultMessage()).isEqualTo("Plane does not exist");
    }
}
