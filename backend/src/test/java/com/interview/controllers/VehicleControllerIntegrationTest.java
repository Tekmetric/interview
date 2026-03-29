package com.interview.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dtos.VehiclePatchDTO;
import com.interview.dtos.VehicleRequestDTO;
import com.interview.dtos.VehicleSearchCriteriaDTO;
import com.interview.mappers.VehicleMapper;
import com.interview.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VehicleMapper vehicleMapper;

    private VehicleRequestDTO sampleRequest;

    @BeforeEach
    void setup() {
        sampleRequest = VehicleRequestDTO.builder()
                .vin("1HGCM82633A123456")
                .make("Honda")
                .model("Civic")
                .manufactureYear(2020)
                .licensePlate("ABC123")
                .ownerName("John Doe")
                .build();
    }

    @Test
    void createVehicle_returnsCreatedVehicle() throws Exception {
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.vin").value("1HGCM82633A123456"))
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.ownerName").value("John Doe"));
    }

    @Test
    void createVehicle_validationError_returns400() throws Exception {
        VehicleRequestDTO invalidRequest = VehicleRequestDTO.builder()
                .vin("FOO")
                .make("")
                .model("Civic")
                .manufactureYear(1800)
                .ownerName("")
                .build();

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields in the request body failed validation."))
                .andExpect(jsonPath("$.errors.vin").value("VIN must be 17 characters long and contain valid characters"))
                .andExpect(jsonPath("$.errors.make").value("must not be blank"))
                .andExpect(jsonPath("$.errors.ownerName").value("must not be blank"));
    }

    @Test
    void createVehicle_conflict_returns409() throws Exception {
        repository.save(vehicleMapper.toEntity(sampleRequest));

        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Vehicle with VIN already exists: 1HGCM82633A123456"));
    }

    @Test
    void getVehicleById_returnsVehicle() throws Exception {
        var created = repository.save(vehicleMapper.toEntity(sampleRequest));

        mockMvc.perform(get("/api/v1/vehicles/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.vin").value("1HGCM82633A123456"));
    }

    @Test
    void getVehicleById_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Vehicle with id: 999 not found"));
    }

    @Test
    void updateVehicle_returnsUpdatedVehicle() throws Exception {
        var created = repository.save(vehicleMapper.toEntity(sampleRequest));

        VehicleRequestDTO updateRequest = VehicleRequestDTO.builder()
                .vin("1HGCM82633A123456")
                .make("Tesla")
                .model("Model 3")
                .manufactureYear(2023)
                .licensePlate("NEW123")
                .ownerName("Jane Doe")
                .build();

        mockMvc.perform(put("/api/v1/vehicles/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.make").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"))
                .andExpect(jsonPath("$.ownerName").value("Jane Doe"))
                .andExpect(jsonPath("$.licensePlate").value("NEW123"));
    }

    @Test
    void updateVehicle_notFound_returns404() throws Exception {
        VehicleRequestDTO updateRequest = VehicleRequestDTO.builder()
                .vin("3HGFC66F1L5000000")
                .make("Tesla")
                .model("Model S")
                .manufactureYear(2023)
                .licensePlate("NEW999")
                .ownerName("Jane Doe")
                .build();

        mockMvc.perform(put("/api/v1/vehicles/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Vehicle with id: 999 not found"));
    }

    @Test
    void patchVehicle_returnsPatchedVehicle() throws Exception {
        var created = repository.save(vehicleMapper.toEntity(sampleRequest));

        VehiclePatchDTO patch = VehiclePatchDTO.builder()
                .make("Toyota")
                .build();

        mockMvc.perform(patch("/api/v1/vehicles/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Civic")); // unchanged
    }

    @Test
    void deleteVehicle_returnsNoContent() throws Exception {
        var created = repository.save(vehicleMapper.toEntity(sampleRequest));

        mockMvc.perform(delete("/api/v1/vehicles/{id}", created.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVehicle_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Vehicle with id: 999 not found"));
    }

    @Test
    void findByVin_returnsVehicle() throws Exception {
        var created = repository.save(vehicleMapper.toEntity(sampleRequest));

        mockMvc.perform(get("/api/v1/vehicles/vin/{vin}", "1HGCM82633A123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.vin").value("1HGCM82633A123456"));
    }

    @Test
    void findByVin_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/vin/{vin}", "FOOBAR"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Vehicle with vin: FOOBAR not found"));
    }

    @Test
    void searchVehicles_noCriteria_returnsPaginatedList() throws Exception {
        repository.save(vehicleMapper.toEntity(sampleRequest));
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .vin("1GBCP44R1V5000000")
                .make(sampleRequest.make())
                .model(sampleRequest.model())
                .manufactureYear(sampleRequest.manufactureYear())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));

        VehicleSearchCriteriaDTO emptyCriteria = VehicleSearchCriteriaDTO.builder().build();

        mockMvc.perform(post("/api/v1/vehicles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].vin").value("1HGCM82633A123456"))
                .andExpect(jsonPath("$.content[1].vin").value("1GBCP44R1V5000000"));
    }

    @Test
    void searchVehicles_withCriteria_returnsFilteredList() throws Exception {
        repository.save(vehicleMapper.toEntity(sampleRequest));
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .make("Ford")
                .model("Mustang")
                .vin("1GBCP44R1V5000000")
                .manufactureYear(sampleRequest.manufactureYear())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .make("Honda")
                .model("Accord")
                .vin("5YJXCBEV1N1234567")
                .manufactureYear(sampleRequest.manufactureYear())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));

        VehicleSearchCriteriaDTO searchCriteria = VehicleSearchCriteriaDTO.builder()
                .make("Honda")
                .build();

        mockMvc.perform(post("/api/v1/vehicles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].vin").value("1HGCM82633A123456"))
                .andExpect(jsonPath("$.content[1].vin").value("5YJXCBEV1N1234567"));
    }

    @Test
    void searchVehicles_withYearRange_returnsFilteredList() throws Exception {
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .manufactureYear(2010)
                .vin("1HGCM82633A123456")
                .make(sampleRequest.make())
                .model(sampleRequest.model())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .manufactureYear(2015)
                .vin("1GBCP44R1V5000000")
                .make(sampleRequest.make())
                .model(sampleRequest.model())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));
        repository.save(vehicleMapper.toEntity(VehicleRequestDTO.builder()
                .manufactureYear(2020)
                .vin("5YJXCBEV1N1234567")
                .make(sampleRequest.make())
                .model(sampleRequest.model())
                .licensePlate(sampleRequest.licensePlate())
                .ownerName(sampleRequest.ownerName())
                .build()));

        VehicleSearchCriteriaDTO searchCriteria = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2014)
                .yearTo(2016)
                .build();

        mockMvc.perform(post("/api/v1/vehicles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].vin").value("1GBCP44R1V5000000"));
    }

    @Test
    void searchVehicles_invalidYearRange_returns400() throws Exception {
        VehicleSearchCriteriaDTO invalidCriteria = VehicleSearchCriteriaDTO.builder()
                .yearFrom(2024)
                .yearTo(2020)
                .build();

        mockMvc.perform(post("/api/v1/vehicles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCriteria)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors.validYearRange").value("Year from cannot be greater than year to"));
    }
}
