package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarDto carDto;

    @BeforeEach
    void setUp() {
        carDto = new CarDto(1L, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void createCar_shouldReturnCreated() throws Exception {
        CarDto inputDto = new CarDto(null, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());
        when(carService.createCar(any(CarDto.class))).thenReturn(carDto);

        mockMvc.perform(post("/api/cars")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void getAllCars_shouldReturnListOfCars() throws Exception {
        when(carService.getAllCars()).thenReturn(List.of(carDto));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void getCarById_shouldReturnCar() throws Exception {
        when(carService.getCarById(1L)).thenReturn(carDto);

        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void updateCar_shouldReturnUpdatedCar() throws Exception {
        when(carService.updateCar(eq(1L), any(CarDto.class))).thenReturn(carDto);

        mockMvc.perform(put("/api/cars/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void deleteCar_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/cars/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void getCarCustomers_shouldReturnCustomers() throws Exception {
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe", "john.doe@example.com", Collections.emptySet());
        when(carService.getCarOwners(1L)).thenReturn(Set.of(customerDto));

        mockMvc.perform(get("/api/cars/1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(username="user", roles="USER")
    void addCarCustomer_shouldReturnOk() throws Exception {
        doNothing().when(carService).addOwnerToCar(1L, 1L);
        mockMvc.perform(post("/api/cars/1/customers/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
