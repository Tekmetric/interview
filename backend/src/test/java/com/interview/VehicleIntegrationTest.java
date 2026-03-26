package com.interview;

import com.interview.dto.VehicleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_API_KEY = "test-secret-123";
    private static final String VALID_VIN = "4T1B11HK0KU800001";
    private static final String ANOTHER_VIN = "2HGFC2F59KH800002";

    private VehicleRequest validRequest(String vin) {
        return new VehicleRequest("Toyota", "Camry", 2020, 15000, null, null, null, vin, null);
    }

    @Nested
    class CreateVehicle {

        @Test
        void shouldCreateVehicle() throws Exception {
            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Camry"))
                    .andExpect(jsonPath("$.year").value(2020))
                    .andExpect(jsonPath("$.vin").value(VALID_VIN))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.createdAt").exists())
                    .andExpect(header().exists("Location"));
        }

        @Test
        void shouldCreateVehicleWithFlatMetadata() throws Exception {
            VehicleRequest withMeta = new VehicleRequest(
                    "Toyota", "Camry", 2020, 15000, null, null, null, VALID_VIN, Map.of("shopTag", "vip"));

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(withMeta)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.metadata.shopTag").value("vip"));
        }

        @Test
        void shouldReturnBadRequestForInvalidVehicle() throws Exception {
            VehicleRequest invalid = new VehicleRequest(null, null, null, null, null, null, null, null, null);
            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(invalid)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnConflictForDuplicateVin() throws Exception {
            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class GetVehicles {

        @Test
        void shouldGetVehicles() throws Exception {
            mockMvc.perform(get("/api/vehicles")
                            .header("X-API-Key", TEST_API_KEY)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        void shouldFilterVehiclesByMake() throws Exception {
            mockMvc.perform(get("/api/vehicles")
                            .header("X-API-Key", TEST_API_KEY)
                            .param("make", "Toyota")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        void shouldFilterVehiclesByCustomerName() throws Exception {
            mockMvc.perform(get("/api/vehicles")
                            .header("X-API-Key", TEST_API_KEY)
                            .param("customerName", "Alice Smith")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].make").value("Toyota"))
                    .andExpect(jsonPath("$.content[0].customerName").value("Alice Smith"));
        }
    }

    @Nested
    class GetVehicleById {

        @Test
        void shouldGetVehicleById() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated())
                    .andReturn();

            String location = result.getResponse().getHeader("Location");

            mockMvc.perform(get(location)
                            .header("X-API-Key", TEST_API_KEY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.vin").value(VALID_VIN));
        }

        @Test
        void shouldReturnNotFoundForMissingVehicle() throws Exception {
            mockMvc.perform(get("/api/vehicles/{id}", UUID.randomUUID())
                            .header("X-API-Key", TEST_API_KEY))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class UpdateVehicle {

        @Test
        void shouldUpdateVehicle() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated())
                    .andReturn();

            String location = result.getResponse().getHeader("Location");

            VehicleRequest updated =
                    new VehicleRequest("Honda", "Accord", 2021, 20000, null, null, null, ANOTHER_VIN, null);

            mockMvc.perform(put(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.make").value("Honda"))
                    .andExpect(jsonPath("$.model").value("Accord"))
                    .andExpect(jsonPath("$.vin").value(ANOTHER_VIN));
        }

        @Test
        void shouldReturnBadRequestWhenRemovingVin() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated())
                    .andReturn();

            String location = result.getResponse().getHeader("Location");

            VehicleRequest withoutVin =
                    new VehicleRequest("Toyota", "Camry", 2020, 15000, null, null, null, null, null);

            mockMvc.perform(put(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(withoutVin)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Cannot remove VIN once set"));
        }
    }

    @Nested
    class DeleteVehicle {

        @Test
        void shouldSoftDeleteVehicle() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-API-Key", TEST_API_KEY)
                            .content(objectMapper.writeValueAsString(validRequest(VALID_VIN))))
                    .andExpect(status().isCreated())
                    .andReturn();

            String location = result.getResponse().getHeader("Location");

            mockMvc.perform(delete(location)
                            .header("X-API-Key", TEST_API_KEY))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(location)
                            .header("X-API-Key", TEST_API_KEY))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class Security {

        @Test
        void shouldReturnUnauthorizedWithNoApiKey() throws Exception {
            mockMvc.perform(get("/api/vehicles"))
                    .andExpect(status().isUnauthorized());
        }
    }
}