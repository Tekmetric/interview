package com.interview.service;

import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.exception.ResourceAlreadyExistsException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.FuelType;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleRequest validRequest() {
        return new VehicleRequest(
                "Toyota", "Camry", 2020, 15000, FuelType.GASOLINE, null, null, "4T1B11HK0KU800001", null);
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {
        when(vehicleRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.findById(UUID.randomUUID()));
    }

    @Test
    void shouldThrowWhenVinAlreadyExists() {
        when(vehicleRepository.existsByVin(any()))
                .thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> vehicleService.create(validRequest()));
    }

    @Test
    void shouldCreateVehicle() {
        VehicleRequest request = validRequest();
        Instant createdAt = Instant.parse("2020-01-01T00:00:00Z");
        Instant updatedAt = Instant.parse("2020-01-02T00:00:00Z");

        when(vehicleRepository.existsByVin(eq(request.vin()))).thenReturn(false);

        Vehicle saved = new Vehicle();
        saved.setId(UUID.randomUUID());
        saved.setMake(request.make());
        saved.setModel(request.model());
        saved.setYear(request.year());
        saved.setVin(request.vin());
        saved.setMileage(request.mileage());
        saved.setLicensePlate(request.licensePlate());
        saved.setCustomerName(request.customerName());
        saved.setFuelType(request.fuelType());
        saved.setMetadata(request.metadata());
        saved.setCreatedAt(createdAt);
        saved.setUpdatedAt(updatedAt);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

        VehicleResponse response = vehicleService.create(request);

        assertThat(response.id()).isEqualTo(saved.getId());
        assertThat(response.make()).isEqualTo(saved.getMake());
        assertThat(response.model()).isEqualTo(saved.getModel());
        assertThat(response.year()).isEqualTo(saved.getYear());
        assertThat(response.vin()).isEqualTo(saved.getVin());
        assertThat(response.mileage()).isEqualTo(saved.getMileage());
        assertThat(response.licensePlate()).isEqualTo(saved.getLicensePlate());
        assertThat(response.customerName()).isEqualTo(saved.getCustomerName());
        assertThat(response.fuelType()).isEqualTo(saved.getFuelType());
        assertThat(response.metadata()).isEqualTo(saved.getMetadata());
        assertThat(response.createdAt()).isEqualTo(saved.getCreatedAt());
        assertThat(response.updatedAt()).isEqualTo(saved.getUpdatedAt());

        ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
        verify(vehicleRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNotNull();
        assertThat(captor.getValue().getVin()).isEqualTo(request.vin());
    }

    @Test
    void shouldUpdateVehicle() {
        UUID id = UUID.randomUUID();
        String oldVin = "4T1B11HK0KU800001";
        String newVin = "2HGFC2F59KH800002";

        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setMake("Honda");
        existing.setModel("Civic");
        existing.setYear(2019);
        existing.setVin(oldVin);
        existing.setMileage(32000);

        VehicleRequest request =
                new VehicleRequest("Honda", "Accord", 2021, 20000, FuelType.HYBRID, null, null, newVin, null);

        Instant createdAt = Instant.parse("2020-01-01T00:00:00Z");
        Instant updatedAt = Instant.parse("2020-01-02T00:00:00Z");

        Vehicle saved = new Vehicle();
        saved.setId(id);
        saved.setMake(request.make());
        saved.setModel(request.model());
        saved.setYear(request.year());
        saved.setVin(request.vin());
        saved.setMileage(request.mileage());
        saved.setLicensePlate(request.licensePlate());
        saved.setCustomerName(request.customerName());
        saved.setFuelType(request.fuelType());
        saved.setMetadata(request.metadata());
        saved.setCreatedAt(createdAt);
        saved.setUpdatedAt(updatedAt);

        when(vehicleRepository.findById(eq(id))).thenReturn(Optional.of(existing));
        when(vehicleRepository.existsByVin(eq(newVin))).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

        VehicleResponse response = vehicleService.update(id, request);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.make()).isEqualTo(request.make());
        assertThat(response.model()).isEqualTo(request.model());
        assertThat(response.year()).isEqualTo(request.year());
        assertThat(response.vin()).isEqualTo(request.vin());
        assertThat(response.mileage()).isEqualTo(request.mileage());
        assertThat(response.licensePlate()).isEqualTo(request.licensePlate());
        assertThat(response.fuelType()).isEqualTo(request.fuelType());
    }

    @Test
    void shouldUpdateVehicleWhenVinUnchanged() {
        UUID id = UUID.randomUUID();
        String vin = "4T1B11HK0KU800001";

        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setVin(vin);
        existing.setMake("Toyota");
        existing.setModel("Camry");
        existing.setYear(2020);
        existing.setMileage(15000);

        VehicleRequest request =
                new VehicleRequest("Toyota", "Camry", 2021, 18000, null, null, null, vin, null);

        when(vehicleRepository.findById(eq(id))).thenReturn(Optional.of(existing));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse response = vehicleService.update(id, request);

        assertThat(response.year()).isEqualTo(2021);
        assertThat(response.mileage()).isEqualTo(18000);

        verify(vehicleRepository).save(any(Vehicle.class));
        verify(vehicleRepository, never()).existsByVin(any());
    }

    @Test
    void shouldThrowWhenVehicleNotFoundOnUpdate() {
        UUID id = UUID.randomUUID();
        when(vehicleRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleService.update(id, validRequest()));
    }

    @Test
    void shouldThrowWhenRemovingVin() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setVin("4T1B11HK0KU800001");
        existing.setMake("Toyota");
        existing.setModel("Camry");
        existing.setYear(2020);
        existing.setMileage(15000);

        VehicleRequest request =
                new VehicleRequest("Toyota", "Camry", 2020, 18000, null, null, null, null, null);

        when(vehicleRepository.findById(eq(id))).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> vehicleService.update(id, request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenMetadataKeyTooLong() {
        String longKey = "x".repeat(VehicleService.MAX_METADATA_KEY_LENGTH + 1);
        VehicleRequest request = new VehicleRequest(
                "Toyota", "Camry", 2020, 15000, null, null, null, null, Map.of(longKey, "v"));

        assertThrows(IllegalArgumentException.class, () -> vehicleService.create(request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void shouldDeleteVehicle() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);

        when(vehicleRepository.findById(eq(id))).thenReturn(Optional.of(existing));

        vehicleService.deleteById(id);

        verify(vehicleRepository).delete(existing);
    }

    @Test
    void shouldFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        String make = "Toyota";
        Integer year = 2020;

        Vehicle v1 = new Vehicle();
        v1.setId(UUID.randomUUID());
        v1.setMake(make);
        v1.setModel("Camry");
        v1.setYear(year);
        v1.setVin("1HGBH41JXMN109186");
        v1.setMileage(15000);

        Page<Vehicle> page = new PageImpl<>(List.of(v1), pageable, 1);
        when(vehicleRepository.findAll(eq(make), eq(year), eq(null), eq(pageable))).thenReturn(page);

        Page<VehicleResponse> response = vehicleService.findAll(make, year, null, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).vin()).isEqualTo("1HGBH41JXMN109186");
    }
}
