package com.interview.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class VehicleSearchCriteriaDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValidYearRange_ValidRange_ReturnsTrue() {
        VehicleSearchCriteriaDTO dto = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2020)
                .yearTo(2024)
                .build();
        assertTrue(dto.isValidYearRange(), "Year from 2020 to 2024 should be valid");
    }

    @Test
    void isValidYearRange_EqualYears_ReturnsTrue() {
        VehicleSearchCriteriaDTO dto = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2022)
                .yearTo(2022)
                .build();
        assertTrue(dto.isValidYearRange(), "Year from 2022 to 2022 should be valid");
    }

    @Test
    void isValidYearRange_OneYearIsNull_ReturnsTrue() {
        VehicleSearchCriteriaDTO dto1 = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2020)
                .build();
        assertTrue(dto1.isValidYearRange(), "Only year from should be valid");

        VehicleSearchCriteriaDTO dto2 = VehicleSearchCriteriaDTO.builder()
                .yearTo(2024)
                .build();
        assertTrue(dto2.isValidYearRange(), "Only year to should be valid");
    }

    @Test
    void isValidYearRange_InvalidRange_ReturnsFalse() {
        VehicleSearchCriteriaDTO dto = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2024)
                .yearTo(2020)
                .build();
        assertFalse(dto.isValidYearRange(), "Year from 2024 to 2020 should be invalid");
    }

    @Test
    void isValidYearRange_BothNull_ReturnsTrue() {
        VehicleSearchCriteriaDTO dto = VehicleSearchCriteriaDTO.builder().build();
        assertTrue(dto.isValidYearRange(), "Both null years should be valid");
    }

    @Test
    void hasYearRange_ReturnsCorrectValue() {
        VehicleSearchCriteriaDTO dto1 = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2020)
                .yearTo(2024)
                .build();
        assertTrue(dto1.hasYearRange(), "Should have a year range");

        VehicleSearchCriteriaDTO dto2 = VehicleSearchCriteriaDTO.builder()
                .yearTo(2024)
                .build();
        assertTrue(dto2.hasYearRange(), "Should have a year range with only year to");

        VehicleSearchCriteriaDTO dto3 = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2020)
                .build();
        assertTrue(dto3.hasYearRange(), "Should have a year range with only year from");

        VehicleSearchCriteriaDTO dto4 = VehicleSearchCriteriaDTO.builder()
                .manufactureYear(2023)
                .build();
        assertFalse(dto4.hasYearRange(), "Should not have a year range");
    }

    @Test
    void hasExactYear_ReturnsCorrectValue() {
        VehicleSearchCriteriaDTO dto1 = VehicleSearchCriteriaDTO.builder()
                .manufactureYear(2023)
                .build();
        assertTrue(dto1.hasExactYear(), "Should have an exact year");

        VehicleSearchCriteriaDTO dto2 = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2020)
                .yearTo(2024)
                .build();
        assertFalse(dto2.hasExactYear(), "Should not have an exact year");
    }

    @Test
    void testValidator_InvalidYearRange_ReturnsConstraintViolation() {
        VehicleSearchCriteriaDTO dto = VehicleSearchCriteriaDTO.builder()
                .vin("1HGCM82633A123456")
                .make("Ford")
                .model("Focus")
                .yearFrom(2024)
                .yearTo(2020)
                .licensePlate("ABC1234")
                .ownerName("John Doe")
                .models(Collections.emptySet())
                .makes(Collections.emptySet())
                .hasLicensePlate(true)
                .isLuxuryVehicle(true)
                .build();

        var violations = validator.validate(dto);
        assertEquals(1, violations.size(), "There should be exactly one violation");
        var violation = violations.iterator().next();
        assertEquals("Year from cannot be greater than year to", violation.getMessage());
        assertEquals("validYearRange", violation.getPropertyPath().toString());
    }
}
