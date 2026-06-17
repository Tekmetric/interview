package com.interview.service;

import com.interview.entity.AppUser;
import com.interview.entity.FuelType;
import com.interview.entity.UserRole;
import com.interview.entity.Vehicle;
import com.interview.repository.AppUserRepository;
import com.interview.repository.VehicleRepository;
import com.interview.security.AuthenticatedUser;
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
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    private static final AuthenticatedUser ADMIN = new AuthenticatedUser(1L, "admin@example.com", UserRole.ADMIN);
    private static final AuthenticatedUser OWNER = new AuthenticatedUser(2L, "owner1@example.com", UserRole.VEHICLE_OWNER);

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void findByIdReturnsVehicleForAdminWhenPresent() {
        Vehicle vehicle = vehicleWithId(42L, "JTDB4MEE9L1234567");
        when(vehicleRepository.findById(42L)).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findById(42L, ADMIN);

        assertSame(vehicle, result);
        verify(vehicleRepository, times(1)).findById(42L);
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void findByIdReturnsVehicleForOwnerWhenPresent() {
        Vehicle vehicle = vehicleWithId(42L, "JTDB4MEE9L1234567");
        when(vehicleRepository.findByIdAndOwnerId(42L, OWNER.id())).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findById(42L, OWNER);

        assertSame(vehicle, result);
        verify(vehicleRepository, times(1)).findByIdAndOwnerId(42L, OWNER.id());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void findByIdThrowsWhenOwnerDoesNotOwnVehicle() {
        when(vehicleRepository.findByIdAndOwnerId(42L, OWNER.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.findById(42L, OWNER));

        verify(vehicleRepository, times(1)).findByIdAndOwnerId(42L, OWNER.id());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void createAssignsAuthenticatedOwnerBeforeSaving() {
        AppUser owner = appUser(OWNER.id(), OWNER.email(), OWNER.role());
        Vehicle vehicle = vehicleWithId(null, "JTDB4MEE9L1234567");
        when(appUserRepository.getReferenceById(OWNER.id())).thenReturn(owner);
        when(vehicleRepository.saveAndFlush(same(vehicle))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle result = vehicleService.create(vehicle, OWNER);

        assertSame(vehicle, result);
        assertSame(owner, result.getOwner());
        verify(appUserRepository, times(1)).getReferenceById(OWNER.id());
        verify(vehicleRepository, times(1)).saveAndFlush(vehicle);
        verifyNoMoreInteractions(vehicleRepository, appUserRepository);
    }

    @Test
    void updateCopiesIncomingFieldsOntoOwnedVehicle() {
        Vehicle existingVehicle = vehicleWithId(7L, "JTDB4MEE9L1234567");
        existingVehicle.setVersion(3L);
        existingVehicle.setCreatedAt(Instant.parse("2026-03-20T12:00:00Z"));
        existingVehicle.setOwner(appUser(OWNER.id(), OWNER.email(), OWNER.role()));

        Vehicle incomingVehicle = vehicleWithId(null, "5YJ3E1EA0MF123456");
        incomingVehicle.setModelYear(2024);
        incomingVehicle.setMake("Tesla");
        incomingVehicle.setModel("Model 3");
        incomingVehicle.setColor("Red");
        incomingVehicle.setLicensePlate("TKMTRC");
        incomingVehicle.setFuelType(FuelType.ELECTRIC);
        incomingVehicle.setDoors(4);
        incomingVehicle.setMileage(15000);

        when(vehicleRepository.findByIdAndOwnerId(7L, OWNER.id())).thenReturn(Optional.of(existingVehicle));

        Vehicle result = vehicleService.update(7L, incomingVehicle, OWNER);

        assertSame(existingVehicle, result);
        assertEquals(7L, result.getId());
        assertEquals(3L, result.getVersion());
        assertEquals(Instant.parse("2026-03-20T12:00:00Z"), result.getCreatedAt());
        assertEquals(OWNER.id(), result.getOwner().getId());
        assertEquals(2024, result.getModelYear());
        assertEquals("Tesla", result.getMake());
        assertEquals("Model 3", result.getModel());
        assertEquals("Red", result.getColor());
        assertEquals("TKMTRC", result.getLicensePlate());
        assertEquals("5YJ3E1EA0MF123456", result.getVin());
        assertEquals(FuelType.ELECTRIC, result.getFuelType());
        assertEquals(4, result.getDoors());
        assertEquals(15000, result.getMileage());
        verify(vehicleRepository, times(1)).findByIdAndOwnerId(7L, OWNER.id());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void updateThrowsWhenOwnerCannotAccessVehicle() {
        when(vehicleRepository.findByIdAndOwnerId(7L, OWNER.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.update(7L, new Vehicle(), OWNER));

        verify(vehicleRepository, times(1)).findByIdAndOwnerId(7L, OWNER.id());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void deleteRemovesOwnedVehicleWhenPresent() {
        Vehicle existingVehicle = vehicleWithId(9L, "JTDB4MEE9L1234567");
        existingVehicle.setOwner(appUser(OWNER.id(), OWNER.email(), OWNER.role()));
        when(vehicleRepository.findByIdAndOwnerId(9L, OWNER.id())).thenReturn(Optional.of(existingVehicle));

        vehicleService.delete(9L, OWNER);

        verify(vehicleRepository, times(1)).findByIdAndOwnerId(9L, OWNER.id());
        verify(vehicleRepository, times(1)).delete(existingVehicle);
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    @Test
    void deleteThrowsWhenOwnerCannotAccessVehicle() {
        when(vehicleRepository.findByIdAndOwnerId(9L, OWNER.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.delete(9L, OWNER));

        verify(vehicleRepository, times(1)).findByIdAndOwnerId(9L, OWNER.id());
        verifyNoMoreInteractions(vehicleRepository);
        verifyNoInteractions(appUserRepository);
    }

    private static AppUser appUser(Long id, String email, UserRole role) {
        AppUser appUser = new AppUser();
        appUser.setId(id);
        appUser.setEmail(email);
        appUser.setRole(role);
        appUser.setEnabled(true);
        appUser.setPasswordHash("encoded");
        return appUser;
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

