package com.interview.service;

import com.interview.exception.ValidationException;
import com.interview.model.VehicleRecall;
import com.interview.repository.VehicleRecallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleRecallServiceTest {

    @Mock
    private VehicleRecallRepository vehicleRecallRepository;

    @InjectMocks
    private VehicleRecallService vehicleRecallService;

    private VehicleRecall validRecall;

    @BeforeEach
    void setUp() {
        validRecall = new VehicleRecall("Toyota", "Corolla", 2020, "Brake issue", LocalDate.now());
    }

    @Test
    void getAllRecallsShouldReturnAllRecalls() {
        List<VehicleRecall> expectedRecalls = Arrays.asList(
                new VehicleRecall("Toyota", "Corolla", 2020, "Brake issue", LocalDate.now()),
                new VehicleRecall("Honda", "Civic", 2019, "Airbag issue", LocalDate.now())
        );
        when(vehicleRecallRepository.findAll()).thenReturn(expectedRecalls);

        List<VehicleRecall> actualRecalls = vehicleRecallService.getAllRecalls();

        assertEquals(expectedRecalls, actualRecalls);
        verify(vehicleRecallRepository).findAll();
    }

    @Test
    void getRecallByIdExistingIdShouldReturnRecall() {
        Long id = 1L;
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.of(validRecall));

        Optional<VehicleRecall> result = vehicleRecallService.getRecallById(id);

        assertTrue(result.isPresent());
        assertEquals(validRecall, result.get());
        verify(vehicleRecallRepository).findById(id);
    }

    @Test
    void getRecallByIdNonExistingIdShouldReturnEmpty() {
        Long id = 1L;
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.empty());

        Optional<VehicleRecall> result = vehicleRecallService.getRecallById(id);

        assertFalse(result.isPresent());
        verify(vehicleRecallRepository).findById(id);
    }

    @Test
    void createRecallValidRecallShouldSaveAndReturnRecall() {
        when(vehicleRecallRepository.save(validRecall)).thenReturn(validRecall);

        VehicleRecall result = vehicleRecallService.createRecall(validRecall);

        assertEquals(validRecall, result);
        verify(vehicleRecallRepository).save(validRecall);
    }

    @Test
    void createRecallInvalidRecallShouldThrowValidationException() {
        VehicleRecall invalidRecall = new VehicleRecall("", "", 1800, "", null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> vehicleRecallService.createRecall(invalidRecall));

        assertTrue(exception.getMessage().contains("Manufacturer is required"));
        assertTrue(exception.getMessage().contains("Model is required"));
        assertTrue(exception.getMessage().contains("Recall description is required"));
        assertTrue(exception.getMessage().contains("Recall date is required"));
        assertTrue(exception.getMessage().contains("Vehicle year must be between 1900 and"));

        verify(vehicleRecallRepository, never()).save(any());
    }

    @Test
    void updateRecallExistingIdAndValidRecallShouldUpdateAndReturnRecall() {
        Long id = 1L;
        VehicleRecall existingRecall = new VehicleRecall("Honda", "Civic", 2019, "Old issue", LocalDate.now().minusDays(1));
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.of(existingRecall));
        when(vehicleRecallRepository.save(any())).thenReturn(validRecall);

        Optional<VehicleRecall> result = vehicleRecallService.updateRecall(id, validRecall);

        assertTrue(result.isPresent());
        assertEquals(validRecall, result.get());
        verify(vehicleRecallRepository).findById(id);
        verify(vehicleRecallRepository).save(any());
    }

    @Test
    void updateRecallNonExistingIdShouldReturnEmpty() {
        Long id = 1L;
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.empty());

        Optional<VehicleRecall> result = vehicleRecallService.updateRecall(id, validRecall);

        assertFalse(result.isPresent());
        verify(vehicleRecallRepository).findById(id);
        verify(vehicleRecallRepository, never()).save(any());
    }

    @Test
    void deleteRecallExistingIdShouldDeleteAndReturnTrue() {
        Long id = 1L;
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.of(validRecall));

        boolean result = vehicleRecallService.deleteRecall(id);

        assertTrue(result);
        verify(vehicleRecallRepository).findById(id);
        verify(vehicleRecallRepository).delete(validRecall);
    }

    @Test
    void deleteRecallNonExistingIdShouldReturnFalse() {
        Long id = 1L;
        when(vehicleRecallRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = vehicleRecallService.deleteRecall(id);

        assertFalse(result);
        verify(vehicleRecallRepository).findById(id);
        verify(vehicleRecallRepository, never()).delete(any());
    }

    @Test
    void validateRecallInvalidFutureDateShouldThrowValidationException() {
        VehicleRecall invalidRecall = new VehicleRecall("Toyota", "Corolla", 2020, "Issue", LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> vehicleRecallService.createRecall(invalidRecall));

        assertTrue(exception.getMessage().contains("Recall date cannot be in the future"));
    }

    @Test
    void validateRecallInvalidFutureModelYearShouldThrowValidationException() {
        VehicleRecall invalidRecall = new VehicleRecall("Toyota", "Corolla", LocalDate.now().getYear() + 2, "Issue", LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> vehicleRecallService.createRecall(invalidRecall));

        assertTrue(exception.getMessage().contains("Cannot have a recall for a future model year vehicle"));
    }

    @Test
    void validateRecallTooLongManufacturerNameShouldThrowValidationException() {
        StringBuilder longNameBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            longNameBuilder.append("A");
        }
        String longName = longNameBuilder.toString();
        VehicleRecall invalidRecall = new VehicleRecall(longName, "Corolla", 2020, "Issue", LocalDate.now());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> vehicleRecallService.createRecall(invalidRecall));

        assertTrue(exception.getMessage().contains("Manufacturer name must be less than 255 characters"));
    }
}
