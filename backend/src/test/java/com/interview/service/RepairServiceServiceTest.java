package com.interview.service;

import com.interview.db.entity.RepairService;
import com.interview.db.repository.RepairServiceRepository;
import com.interview.dto.RepairServiceDTO;
import com.interview.dto.RepairServiceStatus;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.RepairServiceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairServiceServiceTest {
    private final Long testId = 1L;

    @Mock
    private RepairServiceRepository repairServiceRepository;

    @Mock
    private RepairServiceMapper repairServiceMapper;

    @InjectMocks
    private RepairServiceService repairServiceService;

    private RepairService repairService;
    private RepairServiceDTO repairServiceDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        repairService = RepairService.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription("Test Service")
                .odometerReading(10000)
                .status("PENDING")
                .build();

        repairServiceDTO = RepairServiceDTO.builder()
                .id(testId)
                .customerName("Test Customer")
                .customerPhone("1234567890")
                .vehicleMake("Test Make")
                .vehicleModel("Test Model")
                .vehicleYear(2020)
                .licensePlate("TEST123")
                .serviceDescription("Test Service")
                .odometerReading(10000)
                .status(RepairServiceStatus.PENDING)
                .build();
    }

    @Test
    void createRepairService_ShouldReturnCreatedDTO() {
        // Arrange
        when(repairServiceMapper.toEntity(any(RepairServiceDTO.class))).thenReturn(repairService);
        when(repairServiceRepository.save(any(RepairService.class))).thenReturn(repairService);
        when(repairServiceMapper.toDto(any(RepairService.class))).thenReturn(repairServiceDTO);

        // Act
        var result = repairServiceService.createRepairService(repairServiceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Customer", result.getCustomerName());
        verify(repairServiceMapper, times(1)).toEntity(any(RepairServiceDTO.class));
        verify(repairServiceRepository, times(1)).save(any(RepairService.class));
        verify(repairServiceMapper, times(1)).toDto(any(RepairService.class));
    }

    @Test
    void getRepairServiceById_WhenServiceExists_ShouldReturnDTO() {
        // Arrange
        when(repairServiceRepository.findById(testId)).thenReturn(Optional.of(repairService));
        when(repairServiceMapper.toDto(repairService)).thenReturn(repairServiceDTO);

        // Act
        var result = repairServiceService.getRepairServiceById(testId);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Customer", result.getCustomerName());
        verify(repairServiceRepository, times(1)).findById(testId);
        verify(repairServiceMapper, times(1)).toDto(repairService);
    }

    @Test
    void getRepairServiceById_WhenServiceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(repairServiceRepository.findById(testId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> repairServiceService.getRepairServiceById(testId));
        verify(repairServiceRepository, times(1)).findById(testId);
        verify(repairServiceMapper, never()).toDto(any(RepairService.class));
    }

    @Test
    void getAllRepairServices_ShouldReturnPageOfDTOs() {
        // Arrange
        var pageable = PageRequest.of(0, 10);
        var repairServices = Collections.singletonList(repairService);
        var repairServicePage = new PageImpl<>(repairServices, pageable, repairServices.size());
        
        when(repairServiceRepository.findAll(pageable)).thenReturn(repairServicePage);
        when(repairServiceMapper.toDto(repairService)).thenReturn(repairServiceDTO);

        // Act
        var result = repairServiceService.getAllRepairServices(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(repairServiceDTO, result.getContent().getFirst());
        verify(repairServiceRepository, times(1)).findAll(pageable);
        verify(repairServiceMapper, times(1)).toDto(repairService);
    }

    @Test
    void updateRepairService_WhenServiceExists_ShouldReturnUpdatedDTO() {
        // Arrange
        when(repairServiceRepository.existsById(testId)).thenReturn(true);
        when(repairServiceMapper.toEntity(repairServiceDTO)).thenReturn(repairService);
        when(repairServiceRepository.save(repairService)).thenReturn(repairService);
        when(repairServiceMapper.toDto(repairService)).thenReturn(repairServiceDTO);

        // Act
        var result = repairServiceService.updateRepairService(testId, repairServiceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Test Customer", result.getCustomerName());
        verify(repairServiceRepository, times(1)).existsById(testId);
        verify(repairServiceMapper, times(1)).toEntity(repairServiceDTO);
        verify(repairServiceRepository, times(1)).save(repairService);
        verify(repairServiceMapper, times(1)).toDto(repairService);
    }

    @Test
    void updateRepairService_WhenServiceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(repairServiceRepository.existsById(testId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> repairServiceService.updateRepairService(testId, repairServiceDTO));
        verify(repairServiceRepository, times(1)).existsById(testId);
        verify(repairServiceMapper, never()).toEntity(any(RepairServiceDTO.class));
        verify(repairServiceRepository, never()).save(any(RepairService.class));
    }

    @Test
    void deleteRepairService_WhenServiceExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(repairServiceRepository.existsById(testId)).thenReturn(true);
        doNothing().when(repairServiceRepository).deleteById(testId);

        // Act
        repairServiceService.deleteRepairService(testId);

        // Assert
        verify(repairServiceRepository, times(1)).existsById(testId);
        verify(repairServiceRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteRepairService_WhenServiceDoesNotExist_ShouldThrowException() {
        // Arrange
        when(repairServiceRepository.existsById(testId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> repairServiceService.deleteRepairService(testId));
        verify(repairServiceRepository, times(1)).existsById(testId);
        verify(repairServiceRepository, never()).deleteById(any(Long.class));
    }
}
