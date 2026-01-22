package com.interview.service;

import com.interview.dto.ServiceJobDTO;
import com.interview.model.ServiceJob;
import com.interview.model.Vehicle;
import com.interview.model.enumeration.ServiceJobStatus;
import com.interview.repository.ServiceJobRepository;
import com.interview.repository.VehicleRepository;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceJobServiceTest {

    @Mock
    private ServiceJobRepository serviceJobRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ServiceJobService serviceJobService;

    private Vehicle vehicle;
    private ServiceJob serviceJob;
    private ServiceJobDTO serviceJobDTO;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(10L);

        serviceJob = new ServiceJob();
        serviceJob.setId(100L);
        serviceJob.setDescription("Oil Change");
        serviceJob.setStatus(ServiceJobStatus.PENDING);
        serviceJob.setVehicle(vehicle);

        serviceJobDTO = new ServiceJobDTO();
        serviceJobDTO.setId(100L);
        serviceJobDTO.setDescription("Oil Change");
        serviceJobDTO.setStatus(ServiceJobStatus.PENDING);
        serviceJobDTO.setVehicleId(10L);
    }

    @Test
    void testSave_whenVehicleExists() {
        // Arrange
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(serviceJobRepository.save(any(ServiceJob.class))).thenReturn(serviceJob);

        // Act
        ServiceJobDTO result = serviceJobService.save(serviceJobDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getVehicleId()).isEqualTo(10L);
        verify(vehicleRepository).findById(10L);
        verify(serviceJobRepository).save(any(ServiceJob.class));
    }

    @Test
    void testSave_whenVehicleDoesNotExist_shouldThrowException() {
        // Arrange
        serviceJobDTO.setVehicleId(99L); // Non-existent vehicle
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceJobService.save(serviceJobDTO);
        });

        verify(vehicleRepository).findById(99L);
        verify(serviceJobRepository, never()).save(any(ServiceJob.class));
    }

    @Test
    void testPartialUpdate_whenServiceJobExists() {
        // Arrange
        ServiceJobDTO updateDto = new ServiceJobDTO();
        updateDto.setId(100L);
        updateDto.setStatus(ServiceJobStatus.COMPLETED);
        updateDto.setCost(new BigDecimal("150.00"));

        ServiceJob updatedServiceJob = new ServiceJob();
        updatedServiceJob.setId(100L);
        updatedServiceJob.setDescription("Oil Change");
        updatedServiceJob.setStatus(ServiceJobStatus.COMPLETED);
        updatedServiceJob.setCost(new BigDecimal("150.00"));
        updatedServiceJob.setVehicle(vehicle);

        when(serviceJobRepository.findById(100L)).thenReturn(Optional.of(serviceJob));
        when(serviceJobRepository.save(any(ServiceJob.class))).thenReturn(updatedServiceJob);

        // Act
        ServiceJobDTO result = serviceJobService.partialUpdate(updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ServiceJobStatus.COMPLETED);
        assertThat(result.getCost()).isEqualTo(new BigDecimal("150.00"));
        verify(serviceJobRepository).findById(100L);
        verify(serviceJobRepository).save(any(ServiceJob.class));
    }

    @Test
    void testPartialUpdate_whenServiceJobDoesNotExist_shouldThrowException() {
        // Arrange
        when(serviceJobRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            serviceJobService.partialUpdate(serviceJobDTO);
        });
        verify(serviceJobRepository).findById(100L);
        verify(serviceJobRepository, never()).save(any(ServiceJob.class));
    }

    @Test
    void testDelete() {
        // Arrange
        doNothing().when(serviceJobRepository).deleteById(100L);

        // Act
        serviceJobService.delete(100L);

        // Assert
        verify(serviceJobRepository, times(1)).deleteById(100L);
    }
}
