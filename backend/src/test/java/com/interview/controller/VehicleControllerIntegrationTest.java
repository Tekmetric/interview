package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.dto.VehicleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/vehicles - Should return all vehicles from data.sql")
    void getAllVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} - Should return single vehicle")
    void getVehicleById() throws Exception {
        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.ownerName").value("John Smith"));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} - Should return 404 for non-existent vehicle")
    void getVehicleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    @DisplayName("POST /api/vehicles - Should create new vehicle")
    void createVehicle() throws Exception {
        VehicleRequest request = new VehicleRequest();
        request.setMake("Tesla");
        request.setModel("Model 3");
        request.setYear(2024);
        request.setVin("5YJ3E1EA5LF123456");
        request.setColor("White");
        request.setMileage(5000);
        request.setOwnerName("Alice Johnson");
        request.setOwnerPhone("555-0999");

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.make").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"));
    }

    @Test
    @DisplayName("POST /api/vehicles - Should return 400 for invalid request")
    void createVehicle_ValidationError() throws Exception {
        VehicleRequest request = new VehicleRequest();
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("PUT /api/vehicles/{id} - Should update existing vehicle")
    void updateVehicle() throws Exception {
        VehicleRequest request = new VehicleRequest();
        request.setMake("Toyota");
        request.setModel("Camry");
        request.setYear(2021);
        request.setColor("Red");
        request.setMileage(45000);
        request.setOwnerName("John Smith Updated");

        mockMvc.perform(put("/api/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Red"))
                .andExpect(jsonPath("$.ownerName").value("John Smith Updated"));
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} - Should delete vehicle")
    void deleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/vehicles/5"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/vehicles/5"))
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("GET /api/vehicles/paged - Should return paginated results")
    void getAllVehiclesPaged() throws Exception {
        mockMvc.perform(get("/api/vehicles/paged?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @DisplayName("GET /api/vehicles/paged - Should return last page")
    void getAllVehiclesPaged_LastPage() throws Exception {
        mockMvc.perform(get("/api/vehicles/paged?page=2&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.last").value(true));
    }
}