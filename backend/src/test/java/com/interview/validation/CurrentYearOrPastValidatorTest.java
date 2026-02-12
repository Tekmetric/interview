package com.interview.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrentYearOrPastValidator Unit Tests")
class CurrentYearOrPastValidatorTest {

    @Mock
    private CurrentYearOrPast mockAnnotation;

    @Mock
    private ConstraintValidatorContext mockContext;

    private CurrentYearOrPastValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CurrentYearOrPastValidator();
    }

    @Test
    @DisplayName("Should accept null values (let @NotNull handle null validation)")
    void shouldAcceptNullValues() {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);

        boolean result = validator.isValid(null, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept current year")
    void shouldAcceptCurrentYear() {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);
        int currentYear = Year.now().getValue();

        boolean result = validator.isValid(currentYear, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept past years")
    void shouldAcceptPastYears() {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);
        int pastYear = Year.now().getValue() - 5;

        boolean result = validator.isValid(pastYear, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should accept allowed future years (default 1 year)")
    void shouldAcceptAllowedFutureYears() {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);
        int nextYear = Year.now().getValue() + 1;

        boolean result = validator.isValid(nextYear, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject years beyond allowed future years")
    void shouldRejectYearsBeyondAllowed() {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);
        int farFutureYear = Year.now().getValue() + 2;

        boolean result = validator.isValid(farFutureYear, mockContext);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should accept multiple future years when configured")
    void shouldAcceptMultipleFutureYears() {
        when(mockAnnotation.allowFutureYears()).thenReturn(3);
        validator.initialize(mockAnnotation);
        int futureYear = Year.now().getValue() + 3;

        boolean result = validator.isValid(futureYear, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should reject years beyond multiple allowed future years")
    void shouldRejectYearsBeyondMultipleAllowed() {
        when(mockAnnotation.allowFutureYears()).thenReturn(3);
        validator.initialize(mockAnnotation);
        int farFutureYear = Year.now().getValue() + 4;

        boolean result = validator.isValid(farFutureYear, mockContext);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should work with zero future years allowed")
    void shouldWorkWithZeroFutureYears() {
        when(mockAnnotation.allowFutureYears()).thenReturn(0);
        validator.initialize(mockAnnotation);
        int currentYear = Year.now().getValue();
        int nextYear = currentYear + 1;

        boolean currentYearResult = validator.isValid(currentYear, mockContext);
        boolean nextYearResult = validator.isValid(nextYear, mockContext);

        assertThat(currentYearResult).isTrue();
        assertThat(nextYearResult).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {1900, 1950, 2000, 2010})
    @DisplayName("Should accept various historical years")
    void shouldAcceptHistoricalYears(int year) {
        when(mockAnnotation.allowFutureYears()).thenReturn(1);
        validator.initialize(mockAnnotation);

        boolean result = validator.isValid(year, mockContext);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle edge case of exact boundary year")
    void shouldHandleExactBoundaryYear() {
        when(mockAnnotation.allowFutureYears()).thenReturn(2);
        validator.initialize(mockAnnotation);
        int exactBoundaryYear = Year.now().getValue() + 2;

        boolean result = validator.isValid(exactBoundaryYear, mockContext);

        assertThat(result).isTrue();
    }
}