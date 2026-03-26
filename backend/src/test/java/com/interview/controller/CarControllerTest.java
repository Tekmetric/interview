package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarRequest;
import com.interview.dto.CarResponse;
import com.interview.exception.CarNotFoundException;
import com.interview.exception.DuplicateVinException;
import com.interview.exception.InvalidCarDataException;
import com.interview.model.CarStatus;
import com.interview.model.FuelType;
import com.interview.service.CarService;
import com.interview.validator.CarRequestValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    @MockBean
    private CarRequestValidator validator;

    private CarResponse buildResponse() {
        return CarResponse.builder()
                .id(1L)
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(2023)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE)
                .build();
    }

    private CarRequest buildRequest() {
        return CarRequest.builder()
                .vin("1HGBH41JXMN109186")
                .brand("Honda")
                .model("Civic")
                .manufacturedYear(2023)
                .color("Blue")
                .fuelType(FuelType.GASOLINE)
                .transmission("Automatic")
                .basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE)
                .build();
    }

    // --- GET /carshop/v1/cars/{id} ---
    @Test
    void givenExistingCar_whenGetById_thenReturns200() throws Exception {
        when(carService.getById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/carshop/v1/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("1HGBH41JXMN109186"))
                .andExpect(jsonPath("$.brand").value("Honda"));
    }

    @Test
    void givenNonExistingCar_whenGetById_thenReturns404() throws Exception {
        when(carService.getById(999L)).thenThrow(new CarNotFoundException(999L));

        mockMvc.perform(get("/carshop/v1/cars/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Car not found with id: 999"));
    }

    // --- GET /carshop/v1/cars ---
    @Test
    void givenNoCriteria_whenGetAll_thenReturnsPageOfCars() throws Exception {
        Page<CarResponse> page = new PageImpl<>(List.of(buildResponse()));
        when(carService.getAll(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/carshop/v1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void givenFilterParams_whenGetAll_thenReturns200() throws Exception {
        Page<CarResponse> page = new PageImpl<>(List.of(buildResponse()));
        when(carService.getAll(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/carshop/v1/cars")
                        .param("status", "AVAILABLE")
                        .param("brand", "Honda")
                        .param("minPrice", "10000")
                        .param("maxPrice", "50000")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // --- POST /carshop/v1/cars ---
    @Test
    void givenValidRequest_whenCreate_thenReturns201WithLocation() throws Exception {
        CarResponse response = buildResponse();
        when(carService.create(any(CarRequest.class))).thenReturn(response);

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.vin").value("1HGBH41JXMN109186"));
    }

    @Test
    void givenMissingRequiredFields_whenCreate_thenReturns400() throws Exception {
        String invalidJson = """
                {
                    "color": "Blue"
                }
                """;

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors.vin").value("VIN is required"))
                .andExpect(jsonPath("$.fieldErrors.brand").value("Brand is required"));
    }

    @Test
    void givenReservedStatusWithoutSellingPrice_whenCreate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.RESERVED).sellingPrice(null).build();

        doThrow(new InvalidCarDataException("Selling price is required when status is RESERVED"))
                .when(validator).validate(any());

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price is required when status is RESERVED"));
    }

    @Test
    void givenSoldStatusWithoutSellingPrice_whenCreate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.SOLD).sellingPrice(null).build();

        doThrow(new InvalidCarDataException("Selling price is required when status is SOLD"))
                .when(validator).validate(any());

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price is required when status is SOLD"));
    }

    @Test
    void givenAvailableStatusWithSellingPrice_whenCreate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE).sellingPrice(new BigDecimal("24000.00")).build();

        doThrow(new InvalidCarDataException("Selling price must be null when status is AVAILABLE"))
                .when(validator).validate(any());

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price must be null when status is AVAILABLE"));
    }

    @Test
    void givenReservedStatusWithoutSellingPrice_whenUpdate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.RESERVED).sellingPrice(null).build();

        doThrow(new InvalidCarDataException("Selling price is required when status is RESERVED"))
                .when(validator).validate(any());

        mockMvc.perform(put("/carshop/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price is required when status is RESERVED"));
    }

    @Test
    void givenSoldStatusWithoutSellingPrice_whenUpdate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.SOLD).sellingPrice(null).build();

        doThrow(new InvalidCarDataException("Selling price is required when status is SOLD"))
                .when(validator).validate(any());

        mockMvc.perform(put("/carshop/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price is required when status is SOLD"));
    }

    @Test
    void givenAvailableStatusWithSellingPrice_whenUpdate_thenReturns400() throws Exception {
        CarRequest request = CarRequest.builder()
                .vin("1HGBH41JXMN109186").brand("Honda").model("Civic")
                .manufacturedYear(2023).color("Blue").fuelType(FuelType.GASOLINE)
                .transmission("Automatic").basePrice(new BigDecimal("25000.00"))
                .status(CarStatus.AVAILABLE).sellingPrice(new BigDecimal("24000.00")).build();

        doThrow(new InvalidCarDataException("Selling price must be null when status is AVAILABLE"))
                .when(validator).validate(any());

        mockMvc.perform(put("/carshop/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Selling price must be null when status is AVAILABLE"));
    }

    @Test
    void givenDuplicateVin_whenCreate_thenReturns409() throws Exception {
        when(carService.create(any(CarRequest.class)))
                .thenThrow(new DuplicateVinException("1HGBH41JXMN109186"));

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A car with VIN '1HGBH41JXMN109186' already exists"));
    }

    // --- PUT /carshop/v1/cars/{id} ---
    @Test
    void givenValidRequest_whenUpdate_thenReturns200() throws Exception {
        CarResponse response = buildResponse();
        when(carService.update(eq(1L), any(CarRequest.class))).thenReturn(response);

        mockMvc.perform(put("/carshop/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void givenNonExistingCar_whenUpdate_thenReturns404() throws Exception {
        when(carService.update(eq(999L), any(CarRequest.class)))
                .thenThrow(new CarNotFoundException(999L));

        mockMvc.perform(put("/carshop/v1/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidRequest_whenUpdate_thenReturns400() throws Exception {
        String invalidJson = """
                {
                    "color": "Blue"
                }
                """;

        mockMvc.perform(put("/carshop/v1/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isNotEmpty());
    }

    // --- Invalid enum value handling ---
    @Test
    void givenInvalidFuelTypeInBody_whenCreate_thenReturns400WithHelpfulMessage() throws Exception {
        String json = """
                {
                    "vin": "1HGBH41JXMN109186", "brand": "Honda", "model": "Civic",
                    "manufacturedYear": 2023, "fuelType": "PETROL",
                    "basePrice": 25000.00, "status": "AVAILABLE"
                }""";

        mockMvc.perform(post("/carshop/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("PETROL")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("fuelType")));
    }

    @Test
    void givenInvalidStatusQueryParam_whenGetAll_thenReturns400WithHelpfulMessage() throws Exception {
        mockMvc.perform(get("/carshop/v1/cars")
                        .param("status", "DAMAGED"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("DAMAGED")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("status")));
    }

    // --- DELETE /carshop/v1/cars/{id} ---
    @Test
    void givenExistingCar_whenDelete_thenReturns204() throws Exception {
        doNothing().when(carService).delete(1L);

        mockMvc.perform(delete("/carshop/v1/cars/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNonExistingCar_whenDelete_thenReturns404() throws Exception {
        doThrow(new CarNotFoundException(999L)).when(carService).delete(999L);

        mockMvc.perform(delete("/carshop/v1/cars/999"))
                .andExpect(status().isNotFound());
    }
}
