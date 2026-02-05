package com.interview.service;

import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.VehicleMapper;
import com.interview.model.dto.VehicleDTO;
import com.interview.model.entity.Customer;
import com.interview.model.entity.Vehicle;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleDTO vehicleDto;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");

        vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setVin("VIN1234567890ABCD");
        vehicle.setMake("Toyota");

        vehicleDto = new VehicleDTO("VIN1234567890ABCD", "Toyota", "Camry", 2022);
    }

    @Test
    void getVehicleByVin_ShouldReturnDto_WhenFound() {
        when(vehicleRepository.findByVin(vehicle.getVin())).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDTO(vehicle)).thenReturn(vehicleDto);

        VehicleDTO result = vehicleService.getVehicleByVin(vehicle.getVin());

        assertThat(result).isNotNull();
        assertThat(result.getVin()).isEqualTo(vehicle.getVin());
        verify(vehicleRepository).findByVin(vehicle.getVin());
    }

    @Test
    void getVehicleByVin_ShouldThrow_WhenNotFound() {
        when(vehicleRepository.findByVin("NONAME")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleByVin("NONAME"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addVehicleToCustomer_ShouldSaveCustomer_WhenBothExist() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleMapper.toEntity(vehicleDto)).thenReturn(vehicle);

        vehicleService.addVehicleToCustomer(customerId, vehicleDto);

        assertThat(customer.getVehicles()).contains(vehicle);
        assertThat(vehicle.getCustomer()).isEqualTo(customer);
        verify(customerRepository).save(customer);
    }

    @Test
    void removeVehicleFromCustomer_ShouldUnlinkAndSave_WhenBothExist() {
        Long customerId = 1L;
        customer.addVehicle(vehicle);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByVin(vehicle.getVin())).thenReturn(Optional.of(vehicle));

        vehicleService.removeVehicleFromCustomer(customerId, vehicle.getVin());

        assertThat(customer.getVehicles()).doesNotContain(vehicle);
        verify(customerRepository).save(customer);
    }

    @Test
    void removeVehicleFromCustomer_ShouldThrow_WhenVehicleNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByVin("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.removeVehicleFromCustomer(1L, "MISSING"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(customerRepository, never()).save(any());
    }
}