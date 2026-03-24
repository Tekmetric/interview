package com.interview.service;

import com.interview.entity.FuelType;
import com.interview.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void findByIdReturnsVehicleWhenPresent() {
        Vehicle vehicle = vehicleWithId(42L, "JTDB4MEE9L1234567");
        when(vehicleRepository.findById(42L)).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findById(42L);

        assertSame(vehicle, result);
        verify(vehicleRepository, times(1)).findById(42L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void findByIdThrowsWhenVehicleMissing() {
        when(vehicleRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.findById(42L));

        verify(vehicleRepository, times(1)).findById(42L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void updateCopiesIncomingFieldsOntoExistingVehicle() {
        Vehicle existingVehicle = vehicleWithId(7L, "JTDB4MEE9L1234567");
        existingVehicle.setVersion(3L);
        existingVehicle.setCreatedAt(Instant.parse("2026-03-20T12:00:00Z"));

        Vehicle incomingVehicle = vehicleWithId(null, "5YJ3E1EA0MF123456");
        incomingVehicle.setModelYear(2024);
        incomingVehicle.setMake("Tesla");
        incomingVehicle.setModel("Model 3");
        incomingVehicle.setColor("Red");
        incomingVehicle.setLicensePlate("TKMTRC");
        incomingVehicle.setFuelType(FuelType.ELECTRIC);
        incomingVehicle.setDoors(4);
        incomingVehicle.setMileage(15000);

        when(vehicleRepository.findById(7L)).thenReturn(Optional.of(existingVehicle));

        Vehicle result = vehicleService.update(7L, incomingVehicle);

        assertSame(existingVehicle, result);
        assertEquals(7L, result.getId());
        assertEquals(3L, result.getVersion());
        assertEquals(Instant.parse("2026-03-20T12:00:00Z"), result.getCreatedAt());
        assertEquals(2024, result.getModelYear());
        assertEquals("Tesla", result.getMake());
        assertEquals("Model 3", result.getModel());
        assertEquals("Red", result.getColor());
        assertEquals("TKMTRC", result.getLicensePlate());
        assertEquals("5YJ3E1EA0MF123456", result.getVin());
        assertEquals(FuelType.ELECTRIC, result.getFuelType());
        assertEquals(4, result.getDoors());
        assertEquals(15000, result.getMileage());
        verify(vehicleRepository, times(1)).findById(7L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void updateThrowsWhenVehicleMissing() {
        when(vehicleRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.update(7L, new Vehicle()));

        verify(vehicleRepository, times(1)).findById(7L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void deleteRemovesVehicleWhenPresent() {
        when(vehicleRepository.existsById(9L)).thenReturn(true);

        vehicleService.delete(9L);

        verify(vehicleRepository, times(1)).existsById(9L);
        verify(vehicleRepository, times(1)).deleteById(9L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    @Test
    void deleteThrowsWhenVehicleMissing() {
        when(vehicleRepository.existsById(9L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> vehicleService.delete(9L));

        verify(vehicleRepository, times(1)).existsById(9L);
        verifyNoMoreInteractions(vehicleRepository);
    }

    private static Vehicle vehicleWithId(Long id, String vin) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        vehicle.setModelYear(2020);
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setColor("Silver");
        vehicle.setLicensePlate("ABC123");
        vehicle.setVin(vin);
        vehicle.setFuelType(FuelType.GASOLINE);
        vehicle.setDoors(4);
        vehicle.setMileage(45000);
        return vehicle;
    }
}
