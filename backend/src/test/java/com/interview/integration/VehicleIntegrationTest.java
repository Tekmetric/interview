package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.interview.dto.VehicleDTO;
import com.interview.entity.CustomerEntity;
import com.interview.entity.VehicleEntity;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("Vehicle Integration Tests")
class VehicleIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Long testCustomerId;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = JsonMapper.builder().build();
        vehicleRepository.deleteAll();
        customerRepository.deleteAll();

        CustomerEntity testCustomer = new CustomerEntity();
        testCustomer.setFirstName("Test");
        testCustomer.setLastName("Owner");
        testCustomer.setEmail("owner@test.com");
        testCustomer.setPhoneNumber("555-0000");

        CustomerEntity savedCustomer = customerRepository.save(testCustomer);
        testCustomerId = savedCustomer.getId();
    }

    private VehicleDTO createVehicle(String vin, Integer year, String make, String model) {
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setVin(vin);
        vehicle.setModelYear(year);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setCustomerId(testCustomerId);
        return vehicle;
    }

    @Test
    @DisplayName("Full CRUD Lifecycle")
    void testFullCrudLifecycle() throws Exception {
        assertThat(vehicleRepository.count()).isEqualTo(0);

        VehicleDTO newVehicle = createVehicle("1HGCV1F30LA001234", 2020, "Honda", "Accord");

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vin").value("1HGCV1F30LA001234"))
                .andExpect(jsonPath("$.make").value("Honda"));

        assertThat(vehicleRepository.count()).isEqualTo(1);
        assertThat(vehicleRepository.existsByVin("1HGCV1F30LA001234")).isTrue();

        VehicleEntity savedEntity = vehicleRepository.findByVin("1HGCV1F30LA001234").orElseThrow();
        Long vehicleId = savedEntity.getId();
        assertThat(vehicleId).isNotNull();
        assertThat(savedEntity.getMake()).isEqualTo("Honda");

        mockMvc.perform(get("/api/v1/vehicles/" + vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId))
                .andExpect(jsonPath("$.vin").value("1HGCV1F30LA001234"))
                .andExpect(jsonPath("$.make").value("Honda"));

        VehicleDTO updateDTO = createVehicle("1HGCV1F30LA001234", 2021, "Honda", "Civic");

        mockMvc.perform(put("/api/v1/vehicles/" + vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId))
                .andExpect(jsonPath("$.model").value("Civic"))
                .andExpect(jsonPath("$.modelYear").value(2021));

        VehicleEntity updatedEntity = vehicleRepository.findById(vehicleId).orElseThrow();
        assertThat(updatedEntity.getModel()).isEqualTo("Civic");
        assertThat(updatedEntity.getModelYear()).isEqualTo(2021);

        VehicleDTO duplicateVehicle = createVehicle("1HGCV1F30LA001234", 2022, "Toyota", "Camry");

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateVehicle)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Vehicle already exists with VIN: 1HGCV1F30LA001234"));

        assertThat(vehicleRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/vehicles/" + vehicleId))
                .andExpect(status().isNoContent());

        assertThat(vehicleRepository.count()).isEqualTo(0);
        assertThat(vehicleRepository.findById(vehicleId)).isEmpty();

        mockMvc.perform(get("/api/v1/vehicles/" + vehicleId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Advanced Features - Pagination and VIN Search")
    void advancedFeaturesTest() throws Exception {
        List<VehicleDTO> vehicles = List.of(
                createVehicle("1HGCV1F30LA001234", 2020, "Honda", "Accord"),
                createVehicle("2HGCV1F30LA005678", 2021, "Honda", "Civic"),
                createVehicle("3TMLU4EN0FM123456", 2022, "Toyota", "Tacoma")
        );

        for (VehicleDTO vehicle : vehicles) {
            mockMvc.perform(post("/api/v1/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(vehicle)))
                    .andExpect(status().isCreated());
        }

        assertThat(vehicleRepository.count()).isEqualTo(3);

        mockMvc.perform(get("/api/v1/vehicles")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));

        mockMvc.perform(get("/api/v1/vehicles")
                        .param("vin", "HGCV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].make").value("Honda"))
                .andExpect(jsonPath("$.content[1].make").value("Honda"));

        mockMvc.perform(get("/api/v1/vehicles")
                        .param("vin", "3TMLU"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].make").value("Toyota"));
    }

    @Test
    @DisplayName("Create Vehicle - Should Fail if Customer ID does not exist")
    void testCreateVehicleWithInvalidCustomer() throws Exception {
        VehicleDTO invalidVehicle = createVehicle("BADV1N1234567890A", 2022, "Ford", "Focus");
        invalidVehicle.setCustomerId(99999L);

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidVehicle)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 99999"));
    }
}
