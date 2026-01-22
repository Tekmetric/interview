package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.ServiceJobDTO;
import com.interview.dto.VehicleDTO;
import com.interview.service.ServiceJobService;
import com.interview.service.VehicleService;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleResource.class)
class VehicleResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private ServiceJobService serviceJobService;

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        vehicleDTO.setVin("12345678901234567");
        vehicleDTO.setMake("Toyota");
        vehicleDTO.setModel("Camry");
        vehicleDTO.setModelYear(2022);
        vehicleDTO.setCustomerId(1L);
    }

    @Test
    void createVehicle_shouldReturnCreated() throws Exception {
        VehicleDTO createDto = new VehicleDTO();
        createDto.setVin("12345678901234567");
        createDto.setMake("Toyota");
        createDto.setModel("Camry");
        createDto.setModelYear(2022);
        createDto.setCustomerId(1L);

        when(vehicleService.create(any(VehicleDTO.class))).thenReturn(vehicleDTO);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createVehicle_withInvalidData_shouldReturnBadRequest() throws Exception {
        VehicleDTO invalidVehicle = new VehicleDTO();
        invalidVehicle.setVin("123"); // Invalid VIN
        invalidVehicle.setMake("Test");
        invalidVehicle.setModel("Test");
        invalidVehicle.setModelYear(2022);
        invalidVehicle.setCustomerId(1L);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVehicle)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllVehicles_shouldReturnOk() throws Exception {
        Page<VehicleDTO> page = new PageImpl<>(Collections.singletonList(vehicleDTO));
        when(vehicleService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getVehicle_whenNotExists_shouldReturnNotFound() throws Exception {
        when(vehicleService.findOne(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/vehicles/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getServiceJobsForVehicle_shouldReturnOk() throws Exception {
        Page<ServiceJobDTO> page = new PageImpl<>(Collections.singletonList(new ServiceJobDTO()));
        when(serviceJobService.findByVehicleId(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles/1/service-jobs?page=0&size=5"))
                .andExpect(status().isOk());
    }

    @Test
    void updateVehicle_shouldReturnOk() throws Exception {
        when(vehicleService.update(any(VehicleDTO.class))).thenReturn(vehicleDTO);

        mockMvc.perform(put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deleteVehicle_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1"))
            .andExpect(status().isNoContent());
    }
}
