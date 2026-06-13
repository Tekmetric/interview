package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarRequest;
import com.interview.dto.LoginRequest;
import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import com.interview.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarIntegrationTest {

    private static final String BASE_URL = "/carshop/v1/cars";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    private String adminToken;
    private String employeeToken;

    @BeforeEach
    void authenticate() throws Exception {
        adminToken = login("admin", "adm123");
        employeeToken = login("employee", "emp123");
    }

    private String login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    private CarRequest buildCreateRequest(String vin) {
        return CarRequest.builder()
                .vin(vin)
                .brand("Toyota")
                .model("Camry")
                .manufacturedYear(2024)
                .color("White")
                .fuelType(FuelType.HYBRID)
                .transmission("Automatic")
                .basePrice(new BigDecimal("35000.00"))
                .status(CarStatus.AVAILABLE)
                .build();
    }

    // --- Full CRUD flow ---

    @Test
    void givenValidCar_whenFullCrudFlow_thenCreateReadUpdateDelete() throws Exception {
        // Create
        CarRequest createRequest = buildCreateRequest("2T1BURHE0JC043821");
        MvcResult createResult = mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vin").value("2T1BURHE0JC043821"))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Long carId = objectMapper.readTree(responseBody).get("id").asLong();

        // Read
        mockMvc.perform(get(BASE_URL + "/" + carId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.vin").value("2T1BURHE0JC043821"));

        // Update - change to RESERVED with selling price
        CarRequest updateRequest = CarRequest.builder()
                .vin("2T1BURHE0JC043821")
                .brand("Toyota")
                .model("Camry")
                .manufacturedYear(2024)
                .color("White")
                .fuelType(FuelType.HYBRID)
                .transmission("Automatic")
                .basePrice(new BigDecimal("35000.00"))
                .sellingPrice(new BigDecimal("33000.00"))
                .status(CarStatus.RESERVED)
                .build();

        mockMvc.perform(put(BASE_URL + "/" + carId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESERVED"))
                .andExpect(jsonPath("$.sellingPrice").value(33000.00));

        // Delete
        mockMvc.perform(delete(BASE_URL + "/" + carId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get(BASE_URL + "/" + carId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    // --- Pagination ---

    @Test
    void givenPaginationParams_whenGetAll_thenReturnsCorrectPage() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.totalPages").value(greaterThanOrEqualTo(3)));
    }

    @Test
    void givenDefaultParams_whenGetAll_thenUsesPage0AndPageSize20() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void givenCustomPageSize_whenGetAll_thenReturnsCorrectNumberOfItems() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void givenSecondPage_whenGetAll_thenReturnsNextItems() throws Exception {
        MvcResult firstPageResult = mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andReturn();

        MvcResult secondPageResult = mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andReturn();

        String firstId = objectMapper.readTree(firstPageResult.getResponse().getContentAsString())
                .get("content").get(0).get("id").asText();
        String secondPageFirstId = objectMapper.readTree(secondPageResult.getResponse().getContentAsString())
                .get("content").get(0).get("id").asText();

        assertThat(firstId).isNotEqualTo(secondPageFirstId);
    }

    // --- Filtering ---

    @Test
    void givenStatusFilter_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status")
                        .value(everyItem(is("AVAILABLE"))));
    }

    @Test
    void givenBrandFilter_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("brand", "Honda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand").value("Honda"));
    }

    @Test
    void givenPriceRangeFilter_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("minPrice", "20000")
                        .param("maxPrice", "30000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)); // Honda 25000, VW 28500
    }

    @Test
    void givenMinPriceFilter_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("minPrice", "60000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)); // Tesla 89990, Corvette 65000
    }

    @Test
    void givenMaxPriceFilter_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("maxPrice", "30000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)); // Honda 25000, VW 28500
    }

    @Test
    void givenCombinedFilters_whenGetAll_thenReturnsFilteredCars() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "AVAILABLE")
                        .param("minPrice", "50000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)); // Tesla 89990, Corvette 65000
    }

    // --- Selling price / status constraint ---

    @Test
    void givenReservedStatusWithoutSellingPrice_whenCreate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("3FADP4AJ5CM123456").brand("Toyota").model("Camry")
                .manufacturedYear(2024).color("White").fuelType(FuelType.HYBRID)
                .transmission("Automatic").basePrice(new BigDecimal("35000.00"))
                .status(CarStatus.RESERVED).sellingPrice(null).build();

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price is required when status is RESERVED"));
    }

    @Test
    void givenAvailableStatusWithSellingPrice_whenCreate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("3FADP4AJ5CM123456").brand("Toyota").model("Camry")
                .manufacturedYear(2024).color("White").fuelType(FuelType.HYBRID)
                .transmission("Automatic").basePrice(new BigDecimal("35000.00"))
                .status(CarStatus.AVAILABLE).sellingPrice(new BigDecimal("30000.00")).build();

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price must be null when status is AVAILABLE"));
    }

    @Test
    void givenSoldStatusWithSellingPrice_whenCreate_thenReturns201() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("3FADP4AJ5CM123456").brand("Toyota").model("Camry")
                .manufacturedYear(2024).color("White").fuelType(FuelType.HYBRID)
                .transmission("Automatic").basePrice(new BigDecimal("35000.00"))
                .status(CarStatus.SOLD).sellingPrice(new BigDecimal("32000.00")).build();

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SOLD"))
                .andExpect(jsonPath("$.sellingPrice").value(32000.00));
    }

    // --- Duplicate VIN ---

    @Test
    void givenDuplicateVin_whenCreate_thenReturns409() throws Exception {
        // VIN from seed data
        CarRequest request = buildCreateRequest("1HGBH41JXMN109186");

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A car with VIN '1HGBH41JXMN109186' already exists"));
    }

    // --- Validation ---

    @Test
    void givenEmptyBody_whenCreate_thenReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.vin").exists())
                .andExpect(jsonPath("$.fieldErrors.brand").exists())
                .andExpect(jsonPath("$.fieldErrors.model").exists())
                .andExpect(jsonPath("$.fieldErrors.manufacturedYear").exists())
                .andExpect(jsonPath("$.fieldErrors.fuelType").exists())
                .andExpect(jsonPath("$.fieldErrors.basePrice").exists());
    }

    @Test
    void givenNonExistingId_whenGetById_thenReturns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found with id: 99999"));
    }

    @Test
    void givenNonExistingId_whenUpdate_thenReturns404() throws Exception {
        CarRequest request = buildCreateRequest("1N4AL3AP7FC201234");

        mockMvc.perform(put(BASE_URL + "/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistingId_whenDelete_thenReturns404() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    // --- Invalid enum value handling ---
    @Test
    void givenInvalidFuelTypeInBody_whenCreate_thenReturns400WithHelpfulMessage() throws Exception {
        String json = """
                {
                    "vin": "1N4AL3AP7FC201234", "brand": "Honda", "model": "Civic",
                    "manufacturedYear": 2023, "fuelType": "PETROL",
                    "basePrice": 25000.00, "status": "AVAILABLE"
                }""";

        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("PETROL")))
                .andExpect(jsonPath("$.message").value(containsString("fuelType")))
                .andExpect(jsonPath("$.message").value(containsString("GASOLINE")));
    }

    @Test
    void givenInvalidStatusQueryParam_whenGetAll_thenReturns400WithHelpfulMessage() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "DAMAGED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("DAMAGED")))
                .andExpect(jsonPath("$.message").value(containsString("status")))
                .andExpect(jsonPath("$.message").value(containsString("AVAILABLE")));
    }

    @Test
    void givenSeedData_whenCountCars_thenReturnsAtLeastFive() throws Exception {
        long count = carRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(5);
    }

    // --- Authentication and authorization ---
    @Test
    void givenNoToken_whenCreateCar_thenReturns401() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest("9NEWVIN1234567890"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenEmployeeToken_whenCreateCar_thenReturns403() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", "Bearer " + employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest("9NEWVIN1234567890"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenEmployeeToken_whenGetCars_thenReturns200() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
