package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairServiceDTO;
import com.interview.dto.RepairServiceStatus;
import com.interview.exception.GlobalExceptionHandler;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.RepairServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RepairServiceControllerTest {

    private final Long testId = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private RepairServiceService repairServiceService;

    @InjectMocks
    private RepairServiceController repairServiceController;

    private RepairServiceDTO repairServiceDTO;

    @BeforeEach
    void setUp() {
        var pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders
                .standaloneSetup(repairServiceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(pageableResolver)
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
    void createRepairService_ShouldReturnCreatedResponse() throws Exception {
        // Arrange
        when(repairServiceService.createRepairService(any(RepairServiceDTO.class)))
                .thenReturn(repairServiceDTO);

        // Act & Assert
        mockMvc.perform(post("/api/repair-services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repairServiceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(testId.intValue())))
                .andExpect(jsonPath("$.data.customerName", is("Test Customer")));

        verify(repairServiceService, times(1)).createRepairService(any(RepairServiceDTO.class));
    }

    @Test
    void getRepairServiceById_WhenServiceExists_ShouldReturnService() throws Exception {
        // Arrange
        when(repairServiceService.getRepairServiceById(testId)).thenReturn(repairServiceDTO);

        // Act & Assert
        mockMvc.perform(get("/api/repair-services/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(testId.intValue())))
                .andExpect(jsonPath("$.data.customerName", is("Test Customer")));

        verify(repairServiceService, times(1)).getRepairServiceById(testId);
    }

    @Test
    void getRepairServiceById_WhenServiceDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(repairServiceService.getRepairServiceById(testId))
                .thenThrow(new ResourceNotFoundException("Repair service not found with ID: " + testId));

        // Act & Assert
        mockMvc.perform(get("/api/repair-services/{id}", testId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(repairServiceService, times(1)).getRepairServiceById(testId);
    }

    @Test
    void getAllRepairServices_ShouldReturnPageOfServices() throws Exception {
        // Arrange
        var repairServices = Collections.singletonList(repairServiceDTO);

        var pageRequest = PageRequest.of(0, 10);
        var page = new PageImpl<>(repairServices, pageRequest, repairServices.size());

        when(repairServiceService.getAllRepairServices(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/repair-services")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content[0].id", is(testId.intValue())))
                .andExpect(jsonPath("$.data.content[0].customerName", is("Test Customer")));

        verify(repairServiceService, times(1)).getAllRepairServices(any(Pageable.class));
    }

    @Test
    void updateRepairService_WhenServiceExists_ShouldReturnUpdatedService() throws Exception {
        // Arrange
        when(repairServiceService.updateRepairService(eq(testId), any(RepairServiceDTO.class)))
                .thenReturn(repairServiceDTO);

        // Act & Assert
        mockMvc.perform(put("/api/repair-services/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repairServiceDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(testId.intValue())))
                .andExpect(jsonPath("$.data.customerName", is("Test Customer")));

        verify(repairServiceService, times(1)).updateRepairService(eq(testId), any(RepairServiceDTO.class));
    }

    @Test
    void updateRepairService_WhenServiceDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(repairServiceService.updateRepairService(eq(testId), any(RepairServiceDTO.class)))
                .thenThrow(new ResourceNotFoundException("Repair service not found with ID: " + testId));

        // Act & Assert
        mockMvc.perform(put("/api/repair-services/{id}", testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(repairServiceDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(repairServiceService, times(1)).updateRepairService(eq(testId), any(RepairServiceDTO.class));
    }

    @Test
    void deleteRepairService_WhenServiceExists_ShouldReturnSuccess() throws Exception {
        // Arrange
        doNothing().when(repairServiceService).deleteRepairService(testId);

        // Act & Assert
        mockMvc.perform(delete("/api/repair-services/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(repairServiceService, times(1)).deleteRepairService(testId);
    }

    @Test
    void deleteRepairService_WhenServiceDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Repair service not found with ID: " + testId))
                .when(repairServiceService).deleteRepairService(testId);

        // Act & Assert
        mockMvc.perform(delete("/api/repair-services/{id}", testId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));

        verify(repairServiceService, times(1)).deleteRepairService(testId);
    }
}
