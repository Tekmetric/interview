package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehicleDTO;
import com.interview.service.VehicleService;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    private ObjectMapper objectMapper;

    private VehicleDTO vehicleDTO;

    @BeforeEach
    void setUp() {
        vehicleDTO = new VehicleDTO();
        vehicleDTO.setId(1L);
        vehicleDTO.setMake("Toyota");
        vehicleDTO.setModel("Camry");
        vehicleDTO.setModelYear(2022); // Use modelYear
    }

    @Test
    void createVehicle_shouldReturnCreated() throws Exception {
        when(vehicleService.create(any(VehicleDTO.class))).thenReturn(vehicleDTO);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new VehicleDTO())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getVehicle_whenNotExists_shouldReturnNotFound() throws Exception {
        when(vehicleService.findOne(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/vehicles/99"))
            .andExpect(status().isNotFound());
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
