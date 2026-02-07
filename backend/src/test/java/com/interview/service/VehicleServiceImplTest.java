package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.dto.VehicleRequest;
import com.interview.model.dto.VehicleResponse;
import com.interview.model.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import com.interview.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private VehicleRequest request;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2021);
        vehicle.setVin("1HGBH41JXMN109186");
        vehicle.setOwnerName("John Smith");

        request = new VehicleRequest();
        request.setMake("Toyota");
        request.setModel("Camry");
        request.setYear(2021);
        request.setVin("1HGBH41JXMN109186");
        request.setOwnerName("John Smith");
    }

    @Test
    @DisplayName("Should create vehicle successfully")
    void createVehicle_Success() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.createVehicle(request);

        assertThat(response.make()).isEqualTo("Toyota");
        assertThat(response.model()).isEqualTo("Camry");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should return vehicle when found by id")
    void getVehicleById_Found() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        VehicleResponse response = vehicleService.getVehicleById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.make()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found")
    void getVehicleById_NotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }

    @Test
    @DisplayName("Should return all vehicles")
    void getAllVehicles_Success() {
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setMake("Honda");
        vehicle2.setModel("Civic");
        vehicle2.setYear(2020);
        vehicle2.setOwnerName("Jane Doe");

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle, vehicle2));

        List<VehicleResponse> responses = vehicleService.getAllVehicles();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).make()).isEqualTo("Toyota");
        assertThat(responses.get(1).make()).isEqualTo("Honda");
    }

    @Test
    @DisplayName("Should update vehicle successfully")
    void updateVehicle_Success() {
        request.setColor("Red");
        request.setMileage(40000);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.updateVehicle(1L, request);

        assertThat(response).isNotNull();
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent vehicle")
    void updateVehicle_NotFound() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.updateVehicle(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should delete vehicle successfully")
    void deleteVehicle_Success() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent vehicle")
    void deleteVehicle_NotFound() {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.deleteVehicle(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}