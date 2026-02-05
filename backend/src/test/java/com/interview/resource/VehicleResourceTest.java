package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.dto.VehicleDTO;
import com.interview.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleResource.class)
class VehicleResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @Test
    void getByVin_ShouldReturnVehicle() throws Exception {
        VehicleDTO dto = new VehicleDTO("VIN123", "Ford", "F150", 2020);
        when(vehicleService.getVehicleByVin("VIN123")).thenReturn(dto);

        mockMvc.perform(get("/api/vehicles/VIN123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Ford"));
    }

    @Test
    void addVehicleToCustomer_ShouldReturnCreated() throws Exception {
        VehicleDTO dto = new VehicleDTO("VIN123", "Ford", "F150", 2020);
        when(vehicleService.addVehicleToCustomer(eq(1L), any(VehicleDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/vehicles/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void removeVehicleFromCustomer_ShouldReturnNoContent() throws Exception {
        doNothing().when(vehicleService).removeVehicleFromCustomer(1L, "VIN123");

        mockMvc.perform(delete("/api/vehicles/VIN123/customer/1"))
                .andExpect(status().isNoContent());
    }
}