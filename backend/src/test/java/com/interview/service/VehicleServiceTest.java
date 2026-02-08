package com.interview.service;

import com.interview.dto.VehicleDTO;
import com.interview.model.Customer;
import com.interview.model.Vehicle;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Customer customer;
    private Vehicle vehicle;
    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setVin("VIN123");
        vehicle.setMake("TestMake");
        vehicle.setModelYear(2022); // Use modelYear
        vehicle.setCustomer(customer);

        vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(10L);
        vehicleDTO.setVin("VIN123");
        vehicleDTO.setMake("TestMake");
        vehicleDTO.setModelYear(2022); // Use modelYear
        vehicleDTO.setCustomerId(1L);
    }

    @Test
    void testCreate_whenCustomerExists() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        VehicleDTO result = vehicleService.create(vehicleDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(1L);
        verify(customerRepository).findById(1L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void testCreate_whenCustomerDoesNotExist_shouldThrowException() {
        // Arrange
        vehicleDTO.setCustomerId(99L);
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.create(vehicleDTO);
        });

        verify(customerRepository).findById(99L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        VehicleDTO result = vehicleService.update(vehicleDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(1L);
        verify(vehicleRepository).findById(10L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }


    @Test
    void testPartialUpdate_whenVehicleAndCustomerExist() {
        // Arrange
        VehicleDTO updateDto = new VehicleDTO();
        updateDto.setId(10L);
        updateDto.setMake("NewMake");

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(10L);
        updatedVehicle.setMake("NewMake");
        updatedVehicle.setCustomer(customer);
        
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);

        // Act
        VehicleDTO result = vehicleService.partialUpdate(updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMake()).isEqualTo("NewMake");
        verify(vehicleRepository).findById(10L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void testPartialUpdate_withNewCustomer() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setId(2L);

        VehicleDTO updateDto = new VehicleDTO();
        updateDto.setId(10L);
        updateDto.setCustomerId(2L);
        
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(newCustomer));
        
        // The entity passed to save will have the new customer
        vehicle.setCustomer(newCustomer);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        VehicleDTO result = vehicleService.partialUpdate(updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(2L);
        verify(vehicleRepository).findById(10L);
        verify(customerRepository).findById(2L);
        verify(vehicleRepository).save(any(Vehicle.class));
    }


    @Test
    void testPartialUpdate_whenVehicleDoesNotExist_shouldThrowException() {
        // Arrange
        when(vehicleRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.partialUpdate(vehicleDTO);
        });
        verify(vehicleRepository).findById(10L);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void findAll_shouldReturnPageOfVehicles() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(Collections.singletonList(vehicle), pageable, 1);
        when(vehicleRepository.findAll(pageable)).thenReturn(vehiclePage);

        // Act
        Page<VehicleDTO> result = vehicleService.findAll(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(vehicle.getId());
        verify(vehicleRepository).findAll(pageable);
    }

    @Test
    void findByCustomerId_shouldReturnVehicles() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vehicle> vehiclePage = new PageImpl<>(Collections.singletonList(vehicle), pageable, 1);
        when(vehicleRepository.findByCustomerId(1L, pageable)).thenReturn(vehiclePage);

        // Act
        Page<VehicleDTO> result = vehicleService.findByCustomerId(1L, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(vehicle.getId());
        verify(vehicleRepository).findByCustomerId(1L, pageable);
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(vehicleRepository).deleteById(10L);

        // Act
        vehicleService.delete(10L);

        // Assert
        verify(vehicleRepository, times(1)).deleteById(10L);
    }
}
