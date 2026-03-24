package com.interview.controller;

import com.interview.entity.FuelType;
import com.interview.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void getVehicleReturnsExistingVehicle() throws Exception {
        mockMvc.perform(get("/api/vehicles/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.vin").value("JTDB4MEE9L1234566"));
    }

    @Test
    void getVehicleReturnsNotFoundWhenVehicleDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/vehicles/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Resource not found"));
    }

    @Test
    void createVehiclePersistsAndReturnsCreatedVehicle() throws Exception {
        String validVehicleRequestJson = """
            {
              "modelYear": 2024,
              "make": "Subaru",
              "model": "Outback",
              "color": "Green",
              "licensePlate": "ABC124",
              "vin": "JTDB4MEE9L1234568",
              "fuelType": "GASOLINE",
              "doors": 4,
              "mileage": 12000
            }
            """;

        String responseBody = mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validVehicleRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.modelYear").value(2024))
                .andExpect(jsonPath("$.make").value("Subaru"))
                .andExpect(jsonPath("$.model").value("Outback"))
                .andExpect(jsonPath("$.vin").value("JTDB4MEE9L1234568"))
                .andExpect(jsonPath("$.fuelType").value("GASOLINE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdId = JsonPath.parse(responseBody).read("$.id", Long.class);
        Vehicle createdVehicle = vehicleRepository.findById(createdId).orElseThrow();

        assertThat(createdVehicle.getMake()).isEqualTo("Subaru");
        assertThat(createdVehicle.getModel()).isEqualTo("Outback");
        assertThat(createdVehicle.getModelYear()).isEqualTo(2024);
        assertThat(createdVehicle.getFuelType()).isEqualTo(FuelType.GASOLINE);
    }

    @Test
    void createVehicleReturnsValidationErrorsForInvalidRequest() throws Exception {
        String invalidVehicleRequestJson = """
            {
              "modelYear": 2024,
              "make": " ",
              "model": "Outback",
              "color": "Green",
              "licensePlate": "ABC124",
              "vin": "BADVIN",
              "doors": 4,
              "mileage": -1
            }
            """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidVehicleRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors[*].field", hasItems("make", "vin", "fuelType", "mileage")));
    }

    @Test
    void updateVehiclePersistsChanges() throws Exception {
        String updatedVehicleRequestJson = """
            {
              "modelYear": 2025,
              "make": "Tesla",
              "model": "Model Y",
              "color": "Blue",
              "licensePlate": "EV2025",
              "vin": "5YJ3E1EA0MF123457",
              "fuelType": "ELECTRIC",
              "doors": 5,
              "mileage": 2500
            }
            """;

        mockMvc.perform(put("/api/vehicles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedVehicleRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modelYear").value(2025))
                .andExpect(jsonPath("$.make").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model Y"))
                .andExpect(jsonPath("$.vin").value("5YJ3E1EA0MF123457"))
                .andExpect(jsonPath("$.fuelType").value("ELECTRIC"));

        Vehicle updatedVehicle = vehicleRepository.findById(1L).orElseThrow();
        assertThat(updatedVehicle.getModelYear()).isEqualTo(2025);
        assertThat(updatedVehicle.getMake()).isEqualTo("Tesla");
        assertThat(updatedVehicle.getModel()).isEqualTo("Model Y");
        assertThat(updatedVehicle.getColor()).isEqualTo("Blue");
        assertThat(updatedVehicle.getLicensePlate()).isEqualTo("EV2025");
        assertThat(updatedVehicle.getVin()).isEqualTo("5YJ3E1EA0MF123457");
        assertThat(updatedVehicle.getFuelType()).isEqualTo(FuelType.ELECTRIC);
        assertThat(updatedVehicle.getDoors()).isEqualTo(5);
        assertThat(updatedVehicle.getMileage()).isEqualTo(2500);
    }

    @Test
    void deleteVehicleRemovesVehicle() throws Exception {
        mockMvc.perform(delete("/api/vehicles/{id}", 2L))
                .andExpect(status().isOk());

        assertThat(vehicleRepository.findById(2L)).isEmpty();
    }
}