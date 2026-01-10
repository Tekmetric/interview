package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehicleRequest;
import com.interview.model.Vehicle;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @Autowired VehicleRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    @Test
    void createVehicle_shouldReturn201() throws Exception {
        VehicleRequest req = new VehicleRequest(
                "12345678901234567",
                "Toyota",
                "Camry",
                2020
        );

        mockMvc.perform(post("/api/v1/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vin").value("12345678901234567"));
    }

    @Test
    void createVehicle_shouldReturn400_whenVinInvalid() throws Exception {
        VehicleRequest req = new VehicleRequest(
                "SHORTVIN",
                "Toyota",
                "Camry",
                2020
        );

        mockMvc.perform(post("/api/v1/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getVehicleById_shouldReturn200() throws Exception {
        Vehicle v = new Vehicle();
        v.setVin("12345678901234567");
        v.setMake("Honda");
        v.setModel("Accord");
        v.setYear(2021);
        v = repo.save(v);

        mockMvc.perform(get("/api/v1/vehicles/" + v.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Honda"));
    }

    @Test
    void getVehicleById_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateVehicle_shouldReturn200() throws Exception {
        Vehicle v = new Vehicle();
        v.setVin("12345678901234567");
        v.setMake("Toyota");
        v.setModel("Camry");
        v.setYear(2020);
        v = repo.save(v);

        VehicleRequest req = new VehicleRequest(
                "12345678901234567",
                "Honda",
                "Civic",
                2022
        );

        mockMvc.perform(put("/api/v1/vehicles/" + v.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.model").value("Civic"));
    }

    @Test
    void deleteVehicle_shouldReturn204() throws Exception {
        Vehicle v = new Vehicle();
        v.setVin("12345678901234567");
        v.setMake("Toyota");
        v.setModel("Camry");
        v.setYear(2020);
        v = repo.save(v);

        mockMvc.perform(delete("/api/v1/vehicles/" + v.getId()))
                .andExpect(status().isNoContent());

        assertThat(repo.findById(v.getId())).isEmpty();
    }
}
