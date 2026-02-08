package com.interview.mapper;

import com.interview.dto.ServiceJobDTO;
import com.interview.model.ServiceJob;
import com.interview.model.Vehicle;
import com.interview.model.enumeration.ServiceJobStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceJobMapperTest {

    @Test
    void testToDto() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        ServiceJob serviceJob = new ServiceJob();
        serviceJob.setId(100L);
        serviceJob.setDescription("Oil Change");
        serviceJob.setCreationDate(Instant.now());
        serviceJob.setStatus(ServiceJobStatus.PENDING);
        serviceJob.setCost(new BigDecimal("120.50"));
        serviceJob.setVehicle(vehicle);

        // Act
        ServiceJobDTO dto = ServiceJobMapper.toDto(serviceJob);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(serviceJob.getId());
        assertThat(dto.getDescription()).isEqualTo(serviceJob.getDescription());
        assertThat(dto.getCreationDate()).isEqualTo(serviceJob.getCreationDate());
        assertThat(dto.getStatus()).isEqualTo(serviceJob.getStatus());
        assertThat(dto.getCost()).isEqualTo(serviceJob.getCost());
        assertThat(dto.getVehicleId()).isEqualTo(serviceJob.getVehicle().getId());
    }

    @Test
    void testToDto_withNullVehicle_shouldMapCorrectly() {
        // Arrange
        ServiceJob serviceJob = new ServiceJob();
        serviceJob.setId(100L);
        serviceJob.setVehicle(null);

        // Act
        ServiceJobDTO dto = ServiceJobMapper.toDto(serviceJob);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(serviceJob.getId());
        assertThat(dto.getVehicleId()).isNull();
    }

    @Test
    void testToDto_withNullInput_shouldReturnNull() {
        // Act
        ServiceJobDTO dto = ServiceJobMapper.toDto(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void testToEntity() {
        // Arrange
        ServiceJobDTO dto = new ServiceJobDTO();
        dto.setId(100L);
        dto.setDescription("Oil Change");
        dto.setCreationDate(Instant.now());
        dto.setStatus(ServiceJobStatus.PENDING);
        dto.setCost(new BigDecimal("120.50"));
        dto.setVehicleId(1L);

        // Act
        ServiceJob serviceJob = ServiceJobMapper.toEntity(dto);

        // Assert
        assertThat(serviceJob).isNotNull();
        assertThat(serviceJob.getId()).isEqualTo(dto.getId());
        assertThat(serviceJob.getDescription()).isEqualTo(dto.getDescription());
        assertThat(serviceJob.getCreationDate()).isEqualTo(dto.getCreationDate());
        assertThat(serviceJob.getStatus()).isEqualTo(dto.getStatus());
        assertThat(serviceJob.getCost()).isEqualTo(dto.getCost());
        // The vehicle is not expected to be mapped here, this is the service's responsibility
        assertThat(serviceJob.getVehicle()).isNull();
    }

    @Test
    void testToEntity_withNullInput_shouldReturnNull() {
        // Act
        ServiceJob serviceJob = ServiceJobMapper.toEntity(null);

        // Assert
        assertThat(serviceJob).isNull();
    }
}
