package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehicleRequest;
import com.interview.dto.VehicleResponse;
import com.interview.entity.Vehicle;
import com.interview.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();
    }

    private Vehicle saveVehicle(String make, String model, int year, String vin, String plate, int mileage) {
        return vehicleRepository.save(new Vehicle(make, model, year, vin, plate, mileage));
    }

    private VehicleRequest buildRequest(String make, String model, int year, String vin, String plate, Integer mileage) {
        VehicleRequest req = new VehicleRequest();
        req.setMake(make);
        req.setModel(model);
        req.setYear(year);
        req.setVin(vin);
        req.setLicensePlate(plate);
        req.setMileage(mileage);
        return req;
    }

    @Test
    void getAll_returnsEmptyList_whenNoVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAll_returnsPaginatedVehicles() throws Exception {
        saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);
        saveVehicle("Honda", "Civic", 2019, "2T1BURHE0JC043821", "XYZ-5678", 62000);
        saveVehicle("Ford", "F-150", 2021, "1FTEW1EG3JFB52345", "DEF-9012", 28000);

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getAll_respectsPageAndSizeParams() throws Exception {
        saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);
        saveVehicle("Honda", "Civic", 2019, "2T1BURHE0JC043821", "XYZ-5678", 62000);
        saveVehicle("Ford", "F-150", 2021, "1FTEW1EG3JFB52345", "DEF-9012", 28000);

        mockMvc.perform(get("/api/vehicles").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    void getAll_returns400_whenPageIsNegative() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getAll_returns400_whenSizeIsZero() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getAll_returns400_whenSizeExceedsMaximum() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getById_returns200WithVehicle_whenFound() throws Exception {
        Vehicle saved = saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);

        mockMvc.perform(get("/api/vehicles/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.vin").value("1HGBH41JXMN109186"))
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.mileage").value(45000));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/vehicles/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Vehicle not found with id: 9999"));
    }

    @Test
    void create_returns201WithCreatedVehicle_onSuccess() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);

        MvcResult result = mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.year").value(2020))
                .andExpect(jsonPath("$.vin").value("1HGBH41JXMN109186"))
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.mileage").value(45000))
                .andReturn();

        VehicleResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), VehicleResponse.class);
        assertThat(vehicleRepository.findById(response.getId())).isPresent();
    }

    @Test
    void create_returns201_withNullOptionalFields() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 2020, null, null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.year").value(2020));
    }

    @Test
    void create_returns400_whenMakeIsBlank() throws Exception {
        VehicleRequest req = buildRequest("", "Camry", 2020, null, null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void create_returns400_whenModelIsBlank() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "  ", 2020, null, null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns400_whenYearIsNull() throws Exception {
        VehicleRequest req = new VehicleRequest();
        req.setMake("Toyota");
        req.setModel("Camry");
        // year intentionally omitted

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns400_whenYearIsBefore1886() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 1885, null, null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns400_whenYearIsAfter2100() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 2101, null, null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns400_whenVinFormatIsInvalid() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 2020, "INVALID-VIN", null, null);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns400_whenMileageIsNegative() throws Exception {
        VehicleRequest req = buildRequest("Toyota", "Camry", 2020, null, null, -1);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_returns409_whenVinAlreadyExists() throws Exception {
        saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);

        VehicleRequest req = buildRequest("Honda", "Civic", 2021, "1HGBH41JXMN109186", "XYZ-9999", 10000);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void update_returns200WithUpdatedVehicle_whenFound() throws Exception {
        Vehicle saved = saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);
        VehicleRequest req = buildRequest("Honda", "Accord", 2022, "2T1BURHE0JC043821", "NEW-PLATE", 10000);

        mockMvc.perform(put("/api/vehicles/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.model").value("Accord"))
                .andExpect(jsonPath("$.year").value(2022))
                .andExpect(jsonPath("$.vin").value("2T1BURHE0JC043821"))
                .andExpect(jsonPath("$.mileage").value(10000));
    }

    @Test
    void update_returns404_whenVehicleNotFound() throws Exception {
        VehicleRequest req = buildRequest("Honda", "Accord", 2022, null, null, null);

        mockMvc.perform(put("/api/vehicles/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void update_returns400_whenRequestBodyIsInvalid() throws Exception {
        Vehicle saved = saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);
        VehicleRequest req = buildRequest("", "Accord", 2022, null, null, null);

        mockMvc.perform(put("/api/vehicles/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void update_returns409_whenVinConflictsWithAnotherVehicle() throws Exception {
        saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);
        Vehicle second = saveVehicle("Honda", "Civic", 2019, "2T1BURHE0JC043821", "XYZ-5678", 62000);

        // Attempt to update second vehicle's VIN to first vehicle's VIN
        VehicleRequest req = buildRequest("Honda", "Civic", 2019, "1HGBH41JXMN109186", "XYZ-5678", 62000);

        mockMvc.perform(put("/api/vehicles/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void delete_returns204_onSuccess() throws Exception {
        Vehicle saved = saveVehicle("Toyota", "Camry", 2020, "1HGBH41JXMN109186", "ABC-1234", 45000);

        mockMvc.perform(delete("/api/vehicles/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(vehicleRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void delete_returns404_whenVehicleNotFound() throws Exception {
        mockMvc.perform(delete("/api/vehicles/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
