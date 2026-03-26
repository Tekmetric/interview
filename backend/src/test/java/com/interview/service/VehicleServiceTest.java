package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleRequest validRequest() {
        return new VehicleRequest("Toyota", "Camry", 2020, "4T1B11HK0KU800001", 15000);
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {
        when(vehicleRepository.findByIdAndDeletedAtIsNull(any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.findById(UUID.randomUUID()));
    }

    @Test
    void shouldThrowWhenVinAlreadyExists() {
        when(vehicleRepository.existsByVinAndDeletedAtIsNull(any()))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vehicleService.create(validRequest()));
    }
}
