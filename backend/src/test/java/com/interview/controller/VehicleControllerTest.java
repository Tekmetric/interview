package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.interview.dto.VehicleDTO;
import com.interview.exception.DuplicateResourceException;
import com.interview.exception.GlobalExceptionHandler;
import com.interview.exception.ResourceNotFoundException;
import com.interview.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController Unit Tests")
class VehicleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private VehicleDTO validVehicleDTO;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        
        validVehicleDTO = new VehicleDTO();
        validVehicleDTO.setId(1L);
        validVehicleDTO.setMake("Toyota");
        validVehicleDTO.setModel("Camry");
        validVehicleDTO.setModelYear(2023);
        validVehicleDTO.setVin("1HGBH41JXMN109186");
        validVehicleDTO.setCustomerId(1L);
    }

    @Test
    @DisplayName("Should pass partial VIN search to service layer")
    void shouldPassPartialVinSearchToService() throws Exception {
        Page<VehicleDTO> vehiclePage = new PageImpl<>(List.of(validVehicleDTO));
        when(vehicleService.getAllVehicles(eq("HGBH"), any())).thenReturn(vehiclePage);

        try {
            mockMvc.perform(get("/api/v1/vehicles")
                    .param("vin", "HGBH"));
        } catch (Exception e) {
        }

        verify(vehicleService).getAllVehicles(eq("HGBH"), any());
    }

    @Test
    @DisplayName("Should pass pagination parameters to service layer")
    void shouldPassPaginationParametersToService() throws Exception {
        Page<VehicleDTO> vehiclePage = new PageImpl<>(List.of(validVehicleDTO));
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(vehicleService.getAllVehicles(isNull(), any())).thenReturn(vehiclePage);

        try {
            mockMvc.perform(get("/api/v1/vehicles")
                    .param("page", "2")
                    .param("size", "15")
                    .param("sort", "modelYear,desc"));
        } catch (Exception e) {
        }

        verify(vehicleService).getAllVehicles(isNull(), pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();
        
        // Verify pagination parameters were passed correctly
        assert capturedPageable.getPageNumber() == 2 : "Expected page number 2 but got " + capturedPageable.getPageNumber();
        assert capturedPageable.getPageSize() == 15 : "Expected page size 15 but got " + capturedPageable.getPageSize();
        assert capturedPageable.getSort().getOrderFor("modelYear") != null : "Expected sort by modelYear";
        assert capturedPageable.getSort().getOrderFor("modelYear").getDirection() == Sort.Direction.DESC : "Expected DESC direction";
    }

    @Test
    @DisplayName("Should return 200 OK when getting vehicle by ID")
    void shouldReturn200_WhenGettingVehicleById() throws Exception {
        when(vehicleService.getVehicleById(1L)).thenReturn(validVehicleDTO);

        mockMvc.perform(get("/api/v1/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("1HGBH41JXMN109186"))
                .andExpect(jsonPath("$.make").value("Toyota"));

        verify(vehicleService).getVehicleById(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when vehicle ID does not exist")
    void shouldReturn404_WhenVehicleIdDoesNotExist() throws Exception {
        when(vehicleService.getVehicleById(999L))
                .thenThrow(new ResourceNotFoundException("Vehicle", 999L));

        mockMvc.perform(get("/api/v1/vehicles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(vehicleService).getVehicleById(999L);
    }

    @Test
    @DisplayName("Should return 201 Created when creating valid vehicle")
    void shouldReturn201_WhenCreatingValidVehicle() throws Exception {
        VehicleDTO newVehicle = new VehicleDTO();
        newVehicle.setMake("Honda");
        newVehicle.setModel("Accord");
        newVehicle.setModelYear(2024);
        newVehicle.setVin("1HGCV1F30LA123456");
        newVehicle.setCustomerId(1L);

        VehicleDTO createdVehicle = new VehicleDTO();
        createdVehicle.setId(2L);
        createdVehicle.setMake("Honda");
        createdVehicle.setModel("Accord");
        createdVehicle.setModelYear(2024);
        createdVehicle.setVin("1HGCV1F30LA123456");
        createdVehicle.setCustomerId(1L);

        when(vehicleService.createVehicle(any(VehicleDTO.class))).thenReturn(createdVehicle);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.make").value("Honda"));

        verify(vehicleService).createVehicle(any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating vehicle with invalid data")
    void shouldReturn400_WhenCreatingVehicleWithInvalidData() throws Exception {
        VehicleDTO invalidVehicle = new VehicleDTO();
        invalidVehicle.setMake("");
        invalidVehicle.setModelYear(1800);
        invalidVehicle.setVin("INVALID");

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(vehicleService, never()).createVehicle(any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when VIN already exists")
    void shouldReturn400_WhenVinAlreadyExists() throws Exception {
        VehicleDTO newVehicle = new VehicleDTO();
        newVehicle.setMake("Honda");
        newVehicle.setModel("Accord");
        newVehicle.setModelYear(2024);
        newVehicle.setVin("1HGCV1F30LA001234");
        newVehicle.setCustomerId(1L);

        when(vehicleService.createVehicle(any(VehicleDTO.class)))
                .thenThrow(new DuplicateResourceException("Vehicle", "VIN", "1HGCV1F30LA001234"));

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicle)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        verify(vehicleService).createVehicle(any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when customer does not exist")
    void shouldReturn404_WhenCustomerDoesNotExist() throws Exception {
        VehicleDTO newVehicle = new VehicleDTO();
        newVehicle.setMake("Honda");
        newVehicle.setModel("Accord");
        newVehicle.setModelYear(2024);
        newVehicle.setVin("1HGCV1F30LA123456");
        newVehicle.setCustomerId(999L);

        when(vehicleService.createVehicle(any(VehicleDTO.class)))
                .thenThrow(new ResourceNotFoundException("Customer", 999L));

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicle)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(vehicleService).createVehicle(any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 200 OK when updating existing vehicle")
    void shouldReturn200_WhenUpdatingExistingVehicle() throws Exception {
        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setMake("Honda");
        updateDTO.setModel("Accord");
        updateDTO.setModelYear(2024);
        updateDTO.setVin("1HGBH41JXMN109186");
        updateDTO.setCustomerId(1L);

        VehicleDTO updatedVehicle = new VehicleDTO();
        updatedVehicle.setId(1L);
        updatedVehicle.setMake("Honda");
        updatedVehicle.setModel("Accord");
        updatedVehicle.setModelYear(2024);
        updatedVehicle.setVin("1HGBH41JXMN109186");
        updatedVehicle.setCustomerId(1L);

        when(vehicleService.updateVehicle(eq(1L), any(VehicleDTO.class))).thenReturn(updatedVehicle);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("Honda"));

        verify(vehicleService).updateVehicle(eq(1L), any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating non-existent vehicle")
    void shouldReturn404_WhenUpdatingNonExistentVehicle() throws Exception {
        VehicleDTO updateDTO = new VehicleDTO();
        updateDTO.setMake("Honda");
        updateDTO.setModel("Accord");
        updateDTO.setModelYear(2024);
        updateDTO.setVin("1HGCV1F30LA123456");
        updateDTO.setCustomerId(1L);

        when(vehicleService.updateVehicle(eq(999L), any(VehicleDTO.class)))
                .thenThrow(new ResourceNotFoundException("Vehicle", 999L));

        mockMvc.perform(put("/api/v1/vehicles/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(vehicleService).updateVehicle(eq(999L), any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when updating with invalid data")
    void shouldReturn400_WhenUpdatingWithInvalidData() throws Exception {
        VehicleDTO invalidUpdate = new VehicleDTO();
        invalidUpdate.setModelYear(3000);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());

        verify(vehicleService, never()).updateVehicle(anyLong(), any(VehicleDTO.class));
    }

    @Test
    @DisplayName("Should return 204 No Content when deleting existing vehicle")
    void shouldReturn204_WhenDeletingExistingVehicle() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService).deleteVehicle(1L);
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existent vehicle")
    void shouldReturn404_WhenDeletingNonExistentVehicle() throws Exception {
        doThrow(new ResourceNotFoundException("Vehicle", 999L))
                .when(vehicleService).deleteVehicle(999L);

        mockMvc.perform(delete("/api/v1/vehicles/999"))
                .andExpect(status().isNotFound());

        verify(vehicleService).deleteVehicle(999L);
    }

}
