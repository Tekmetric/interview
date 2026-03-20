package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.model.dto.CustomerDTO;
import com.interview.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerResource.class)
class CustomerResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerDTO customerDto;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDTO("Jack", "McGinnis", "1234567890", Set.of());
    }

    @Test
    void createCustomer_ShouldReturnCreated() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customerDto);

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jack"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    void getCustomer_ShouldReturnCustomer() throws Exception {
        Long id = 1L;
        when(customerService.getCustomerById(id)).thenReturn(customerDto);

        mockMvc.perform(get("/api/customer/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jack"));
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        Long id = 1L;
        when(customerService.updateCustomer(eq(id), any(CustomerDTO.class))).thenReturn(customerDto);

        mockMvc.perform(put("/api/customer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("McGinnis"));
    }

    @Test
    void partialUpdateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        Long id = 1L;
        when(customerService.updateCustomer(eq(id), any(CustomerDTO.class))).thenReturn(customerDto);

        mockMvc.perform(patch("/api/customer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"NewName\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(customerService).deleteCustomer(id);

        mockMvc.perform(delete("/api/customer/{id}", id))
                .andExpect(status().isNoContent());
    }
}