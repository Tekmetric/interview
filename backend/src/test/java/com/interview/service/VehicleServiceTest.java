package com.interview.service;

import com.interview.dto.PagedResponse;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import com.interview.exception.ResourceConflictException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequest request;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle("Toyota", "Camry", 2020, "ANYVIN12345678901", "ABC-1234", 15000);
        vehicle.setId(1L);

        request = new VehicleRequest();
        request.setMake("Toyota");
        request.setModel("Camry");
        request.setYear(2020);
        request.setVin("ANYVIN12345678901");
        request.setLicensePlate("ABC-1234");
        request.setMileage(15000);
    }

    @Test
    void findAll_returnsPagedResponse_withVehicles() {
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle), PageRequest.of(0, 10, Sort.by("id").ascending()), 1);
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(page);

        PagedResponse<VehicleResponse> result = vehicleService.findAll(0, 10);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getId()).isEqualTo(1L);
        assertThat(result.getItems().get(0).getMake()).isEqualTo("Toyota");
        assertThat(result.getItems().get(0).getModel()).isEqualTo("Camry");
        assertThat(result.getItems().get(0).getYear()).isEqualTo(2020);
        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    void findAll_returnsEmptyPage_whenNoVehicles() {
        Page<Vehicle> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10, Sort.by("id").ascending()), 0);
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        PagedResponse<VehicleResponse> result = vehicleService.findAll(0, 10);

        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalItems()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }

    @Test
    void findAll_passesCorrectPageRequest_toRepository() {
        Page<Vehicle> page = new PageImpl<>(List.of(), PageRequest.of(2, 5, Sort.by("id").ascending()), 0);
        when(vehicleRepository.findAll(any(PageRequest.class))).thenReturn(page);

        vehicleService.findAll(2, 5);

        verify(vehicleRepository).findAll(PageRequest.of(2, 5, Sort.by("id").ascending()));
    }

    @Test
    void findById_returnsVehicleResponse_whenFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        VehicleResponse result = vehicleService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getMake()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getYear()).isEqualTo(2020);
        assertThat(result.getVin()).isEqualTo("ANYVIN12345678901");
        assertThat(result.getLicensePlate()).isEqualTo("ABC-1234");
        assertThat(result.getMileage()).isEqualTo(15000);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_returnsVehicleResponse_onSuccess() {
        when(vehicleRepository.saveAndFlush(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse result = vehicleService.create(request);

        assertThat(result.getMake()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getYear()).isEqualTo(2020);
        verify(vehicleRepository).saveAndFlush(any(Vehicle.class));
    }

    @Test
    void create_mapsAllRequestFieldsToEntity() {
        when(vehicleRepository.saveAndFlush(any(Vehicle.class))).thenAnswer(invocation -> {
            Vehicle saved = invocation.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        VehicleResponse result = vehicleService.create(request);

        assertThat(result.getMake()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getYear()).isEqualTo(2020);
        assertThat(result.getVin()).isEqualTo("ANYVIN12345678901");
        assertThat(result.getLicensePlate()).isEqualTo("ABC-1234");
        assertThat(result.getMileage()).isEqualTo(15000);
    }

    @Test
    void create_throwsResourceConflictException_onDuplicateVin() {
        when(vehicleRepository.saveAndFlush(any(Vehicle.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

        assertThatThrownBy(() -> vehicleService.create(request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("uniqueness constraint");
    }

    @Test
    void update_returnsUpdatedVehicleResponse_whenFound() {
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setMake("Honda");
        updateRequest.setModel("Accord");
        updateRequest.setYear(2021);
        updateRequest.setVin("NEWVIN12345678901");
        updateRequest.setLicensePlate("XYZ-9999");
        updateRequest.setMileage(5000);

        Vehicle updated = new Vehicle("Honda", "Accord", 2021, "NEWVIN12345678901", "XYZ-9999", 5000);
        updated.setId(1L);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.saveAndFlush(any(Vehicle.class))).thenReturn(updated);

        VehicleResponse result = vehicleService.update(1L, updateRequest);

        assertThat(result.getMake()).isEqualTo("Honda");
        assertThat(result.getModel()).isEqualTo("Accord");
        assertThat(result.getYear()).isEqualTo(2021);
        assertThat(result.getVin()).isEqualTo("NEWVIN12345678901");
        assertThat(result.getLicensePlate()).isEqualTo("XYZ-9999");
        assertThat(result.getMileage()).isEqualTo(5000);
    }

    @Test
    void update_mutatesExistingEntityFields_beforeSaving() {
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setMake("Honda");
        updateRequest.setModel("Accord");
        updateRequest.setYear(2022);
        updateRequest.setVin(null);
        updateRequest.setLicensePlate(null);
        updateRequest.setMileage(null);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.saveAndFlush(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehicleResponse result = vehicleService.update(1L, updateRequest);

        assertThat(result.getMake()).isEqualTo("Honda");
        assertThat(result.getModel()).isEqualTo("Accord");
        assertThat(result.getYear()).isEqualTo(2022);
        assertThat(result.getVin()).isNull();
        assertThat(result.getLicensePlate()).isNull();
        assertThat(result.getMileage()).isNull();
    }

    @Test
    void update_throwsResourceNotFoundException_whenVehicleNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.update(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_throwsResourceConflictException_onConstraintViolation() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.saveAndFlush(any(Vehicle.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

        assertThatThrownBy(() -> vehicleService.update(1L, request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("uniqueness constraint");
    }

    @Test
    void delete_deletesVehicle_whenFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.delete(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
