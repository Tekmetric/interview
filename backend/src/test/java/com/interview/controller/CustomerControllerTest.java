package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CarDto;
import com.interview.dto.CustomerDto;
import com.interview.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_shouldReturnCreated() throws Exception {
        CustomerDto customerDto = new CustomerDto(null, "John", "Doe", "john.doe@example.com", Collections.emptySet());
        CustomerDto savedCustomerDto = new CustomerDto(1L, "John", "Doe", "john.doe@example.com", Collections.emptySet());

        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(savedCustomerDto);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getCustomerCars_shouldReturnCars() throws Exception {
        CarDto carDto = new CarDto(1L, "Toyota", "Camry", 2022, "White", "12345678901234567", Collections.emptySet());
        when(customerService.getCustomerCars(1L)).thenReturn(Set.of(carDto));

        mockMvc.perform(get("/api/customers/1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }
}
