package com.interview.mapper;

import com.interview.dto.VehicleDTO;
import com.interview.model.Customer;
import com.interview.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleMapperTest {

    @Test
    void testToDto() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setVin("VIN123");
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setModelYear(2022);
        vehicle.setCustomer(customer);

        // Act
        VehicleDTO dto = VehicleMapper.toDto(vehicle);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(vehicle.getId());
        assertThat(dto.getVin()).isEqualTo(vehicle.getVin());
        assertThat(dto.getMake()).isEqualTo(vehicle.getMake());
        assertThat(dto.getModel()).isEqualTo(vehicle.getModel());
        assertThat(dto.getModelYear()).isEqualTo(vehicle.getModelYear());
        assertThat(dto.getCustomerId()).isEqualTo(vehicle.getCustomer().getId());
    }

    @Test
    void testToDto_withNullCustomer_shouldMapCorrectly() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setModelYear(2022);
        vehicle.setCustomer(null);

        // Act
        VehicleDTO dto = VehicleMapper.toDto(vehicle);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(vehicle.getId());
        assertThat(dto.getModelYear()).isEqualTo(vehicle.getModelYear());
        assertThat(dto.getCustomerId()).isNull();
    }

    @Test
    void testToDto_withNullInput_shouldReturnNull() {
        // Act
        VehicleDTO dto = VehicleMapper.toDto(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void testToEntity() {
        // Arrange
        VehicleDTO dto = new VehicleDTO();
        dto.setId(10L);
        dto.setVin("VIN123");
        dto.setMake("Toyota");
        dto.setModel("Camry");
        dto.setModelYear(2022);
        dto.setCustomerId(1L);

        // Act
        Vehicle vehicle = VehicleMapper.toEntity(dto);

        // Assert
        assertThat(vehicle).isNotNull();
        assertThat(vehicle.getId()).isEqualTo(dto.getId());
        assertThat(vehicle.getVin()).isEqualTo(dto.getVin());
        assertThat(vehicle.getMake()).isEqualTo(dto.getMake());
        assertThat(vehicle.getModel()).isEqualTo(dto.getModel());
        assertThat(vehicle.getModelYear()).isEqualTo(dto.getModelYear());
        // The customer is not expected to be mapped here, this is the service's responsibility
        assertThat(vehicle.getCustomer()).isNull();
    }

    @Test
    void testToEntity_withNullInput_shouldReturnNull() {
        // Act
        Vehicle vehicle = VehicleMapper.toEntity(null);

        // Assert
        assertThat(vehicle).isNull();
    }}
