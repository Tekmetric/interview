package com.interview.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.interview.dto.CustomerDTO;

import com.interview.dto.ServiceJobDTO;

import com.interview.dto.VehicleDTO;

import com.interview.service.CustomerService;

import com.interview.service.ServiceJobService;

import com.interview.service.VehicleService;

import com.interview.web.rest.errors.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.PageRequest;

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



    @MockBean

    private VehicleService vehicleService;



    @MockBean

    private ServiceJobService serviceJobService;



    @Autowired

    private ObjectMapper objectMapper;



    private CustomerDTO customerDTO;



    @BeforeEach

    void setUp() {

        customerDTO = new CustomerDTO();

        customerDTO.setId(1L);

        customerDTO.setFirstName("John");

        customerDTO.setLastName("Doe");

        customerDTO.setEmail("john.doe@example.com");

    }



    @Test

    void createCustomer_shouldReturnCreated() throws Exception {

        CustomerDTO createDto = new CustomerDTO();

        createDto.setFirstName("John");

        createDto.setLastName("Doe");

        createDto.setEmail("john.doe@example.com");



        when(customerService.create(any(CustomerDTO.class))).thenReturn(customerDTO);



        mockMvc.perform(post("/api/customers")

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(createDto)))

            .andExpect(status().isCreated())

            .andExpect(header().string("Location", "/api/customers/1"))

            .andExpect(jsonPath("$.id").value(1L));

    }



    @Test

    void createCustomer_withInvalidData_shouldReturnBadRequest() throws Exception {

        CustomerDTO invalidCustomer = new CustomerDTO();

        invalidCustomer.setFirstName(""); // Invalid

        invalidCustomer.setLastName("Test");

        invalidCustomer.setEmail("test@test.com");



        mockMvc.perform(post("/api/customers")

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(invalidCustomer)))

            .andExpect(status().isBadRequest());

    }



    @Test

    void createCustomer_withDuplicateEmail_shouldReturnConflict() throws Exception {

        CustomerDTO duplicateDto = new CustomerDTO();

        duplicateDto.setFirstName("John");

        duplicateDto.setLastName("Doe");

        duplicateDto.setEmail("john.doe@example.com");

        

        when(customerService.create(any(CustomerDTO.class)))

            .thenThrow(new DataIntegrityViolationException("Duplicate entry"));



        mockMvc.perform(post("/api/customers")

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(duplicateDto)))

            .andExpect(status().isConflict());

    }



    @Test

    void updateCustomer_shouldReturnOk() throws Exception {

        when(customerService.update(any(CustomerDTO.class))).thenReturn(customerDTO);



        mockMvc.perform(put("/api/customers/1")

                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(customerDTO)))

            .andExpect(status().isOk())

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

        Page<CustomerDTO> page = new PageImpl<>(Collections.singletonList(customerDTO));

        when(customerService.findAll(any())).thenReturn(page);



        mockMvc.perform(get("/api/customers?page=0&size=10"))

            .andExpect(status().isOk())

            .andExpect(jsonPath("$.content[0].id").value(1L));

    }



    @Test

    void getVehiclesForCustomer_shouldReturnOk() throws Exception {

        Page<VehicleDTO> page = new PageImpl<>(Collections.singletonList(new VehicleDTO()));

        when(vehicleService.findByCustomerId(any(), any())).thenReturn(page);



        mockMvc.perform(get("/api/customers/1/vehicles?page=0&size=5"))

            .andExpect(status().isOk())

            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }



    @Test

    void getServiceJobsForCustomer_shouldReturnOk() throws Exception {

        Page<ServiceJobDTO> page = new PageImpl<>(Collections.singletonList(new ServiceJobDTO()));

        when(serviceJobService.findByCustomerId(any(), any())).thenReturn(page);



        mockMvc.perform(get("/api/customers/1/service-jobs?page=0&size=5"))

            .andExpect(status().isOk())

            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

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


