package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CustomerDTO;
import com.interview.service.CustomerService;
import com.interview.web.rest.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerResource.class)
class CustomerResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setFirstName("John");
        customerDTO.setLastName("Doe");
    }

    @Test
    void createCustomer_shouldReturnCreated() throws Exception {
        when(customerService.save(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CustomerDTO())))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/customers/1"))
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getCustomer_whenExists_shouldReturnOk() throws Exception {
        when(customerService.findOne(1L)).thenReturn(customerDTO);

        mockMvc.perform(get("/api/customers/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getCustomer_whenNotExists_shouldReturnNotFound() throws Exception {
        when(customerService.findOne(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/customers/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllCustomers_shouldReturnOk() throws Exception {
        when(customerService.findAll()).thenReturn(Collections.singletonList(customerDTO));

        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void deleteCustomer_shouldReturnNoContent() throws Exception {
        doNothing().when(customerService).delete(1L);

        mockMvc.perform(delete("/api/customers/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void partialUpdateCustomer_shouldReturnOk() throws Exception {
        when(customerService.partialUpdate(any(CustomerDTO.class))).thenReturn(customerDTO);

        mockMvc.perform(patch("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }
}
