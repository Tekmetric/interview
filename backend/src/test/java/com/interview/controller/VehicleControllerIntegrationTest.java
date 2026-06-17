package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehicleRequest;
import com.interview.entity.Customer;
import com.interview.entity.Vehicle;
import com.interview.mapper.VehicleMapper;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full‑stack integration tests for {@link VehicleController} covering *all* public endpoints.
 * These run against a real Spring context with H2, JPA & the full MVC stack. Security filters are
 * disabled (addFilters = false) so we can focus on functional behaviour, validation and persistence.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("VehicleController – Integration")
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private CustomerRepository customerRepository;

    private Long customerId; // foreign‑key for vehicles
    private VehicleRequest validRequest;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
        customerRepository.deleteAll();

        // Persist a customer – the FK must be non‑null for every Vehicle row
        String uniqueEmail = "john.doe+" + System.nanoTime() + "@example.com";
        Customer cust = new Customer(null, null, "John", "Doe", uniqueEmail, "555‑0101", null, List.of(), java.util.Set.of());
        customerId = customerRepository.save(cust).getId();

        validRequest = new VehicleRequest(
            customerId,
            "1HGCM82633A004352",
            "Honda",
            "Accord",
            2020
        );
    }

    /* Utility to persist a vehicle with the mandatory customer FK */
    private Vehicle persistVehicle(VehicleRequest req) {
        Vehicle v = vehicleMapper.toEntity(req);
        v.setCustomer(customerRepository.getReferenceById(customerId));
        return vehicleRepository.save(v);
    }

    @Nested
    @DisplayName("POST /api/v1/vehicles")
    class CreateVehicle {
        @Test
        @DisplayName("should create vehicle and return 201")
        void shouldCreateVehicle() throws Exception {
            mockMvc.perform(post("/api/v1/vehicles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.vin").value(validRequest.vin()));
        }

        @Test
        @DisplayName("should fail validation and return 400 when VIN length != 17")
        void shouldFailValidation() throws Exception {
            VehicleRequest bad = new VehicleRequest(customerId, "SHORTVIN123", "Ford", "Focus", 2019);

            mockMvc.perform(post("/api/v1/vehicles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("should return 400 when VIN already exists")
        void shouldReturn400WhenDuplicateVin() throws Exception {
            // first create via controller
            mockMvc.perform(post("/api/v1/vehicles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

            // duplicate VIN
            mockMvc.perform(post("/api/v1/vehicles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/{id}")
    class GetVehicleById {
        private Long savedId;

        @BeforeEach
        void initVehicle() {
            savedId = persistVehicle(validRequest).getId();
        }

        @Test
        @DisplayName("should return vehicle when found – 200")
        void shouldReturnVehicle() throws Exception {
            mockMvc.perform(get("/api/v1/vehicles/{id}", savedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedId))
                .andExpect(jsonPath("$.vin").value(validRequest.vin()));
        }

        @Test
        @DisplayName("should return 404 when not found")
        void shouldReturn404() throws Exception {
            mockMvc.perform(get("/api/v1/vehicles/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("VEHICLE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles")
    class GetAllVehicles {
        @Test
        @DisplayName("should return empty list when no vehicles exist")
        void shouldReturnEmpty() throws Exception {
            mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should return list with vehicles when present")
        void shouldReturnVehicles() throws Exception {
            persistVehicle(validRequest);

            mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles paginated")
    class GetVehiclesPaginated {
        @Test
        @DisplayName("should return paginated result with metadata")
        void shouldReturnPaginated() throws Exception {
            persistVehicle(validRequest);
            persistVehicle(new VehicleRequest(
                customerId,
                "2HGCM82633A004353",
                "Tesla",
                "Model 3",
                2022));

            mockMvc.perform(get("/api/v1/vehicles/paginated")
                    .param("page", "0")
                    .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/vehicles/{id}")
    class UpdateVehicle {
        private Long id;

        @BeforeEach
        void initVehicle() {
            id = persistVehicle(validRequest).getId();
        }

        @Test
        @DisplayName("should update vehicle – 200")
        void shouldUpdateVehicle() throws Exception {
            VehicleRequest update = new VehicleRequest(customerId, validRequest.vin(), "Honda", "Civic", 2021);

            mockMvc.perform(put("/api/v1/vehicles/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Civic"))
                .andExpect(jsonPath("$.year").value(2021));
        }

        @Test
        @DisplayName("should return 400 when duplicate VIN on other vehicle")
        void shouldReturn400OnDuplicateVinOnUpdate() throws Exception {
            // another vehicle with different VIN already exists
            persistVehicle(new VehicleRequest(
                customerId,
                "2HGCM82633A004353",
                "Ford",
                "Focus",
                2018));

            VehicleRequest conflict = new VehicleRequest(customerId, "2HGCM82633A004353", "Honda", "Civic", 2021);

            mockMvc.perform(put("/api/v1/vehicles/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(conflict)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/vehicles/{id}")
    class DeleteVehicle {
        @Test
        @DisplayName("should delete vehicle and return 204")
        void shouldDeleteVehicle() throws Exception {
            Long id = persistVehicle(validRequest).getId();

            mockMvc.perform(delete("/api/v1/vehicles/{id}", id))
                .andExpect(status().isNoContent());

            assertFalse(vehicleRepository.findById(id).isPresent(), "Vehicle should be removed from DB");
        }

        @Test
        @DisplayName("should return 404 when deleting non‑existent vehicle")
        void shouldReturn404() throws Exception {
            mockMvc.perform(delete("/api/v1/vehicles/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("VEHICLE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/vehicles/search")
    class SearchVehicles {
        @BeforeEach
        void seedData() {
            persistVehicle(validRequest); // Honda Accord 2020 VIN 1HG...
            persistVehicle(new VehicleRequest(customerId, "2HGCM82633A004353", "Tesla", "Model 3", 2022));
            persistVehicle(new VehicleRequest(customerId, "3HGCM82633A004354", "Honda", "Civic", 2018));
        }

        @Test
        @DisplayName("should return matching vehicles by make filter")
        void shouldSearchByMake() throws Exception {
            mockMvc.perform(get("/api/v1/vehicles/search")
                    .param("make", "Honda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].make", anyOf(is("Honda"), is("Honda"))));
        }

        @Test
        @DisplayName("should return empty page when no vehicles match filters")
        void shouldReturnEmptyWhenNoMatch() throws Exception {
            mockMvc.perform(get("/api/v1/vehicles/search")
                    .param("vin", "NONEXISTENTVIN123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements").value(0));
        }
    }
}
