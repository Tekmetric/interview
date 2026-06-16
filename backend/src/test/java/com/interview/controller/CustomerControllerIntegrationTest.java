package com.interview.controller;

import static com.interview.util.TestFixtures.CUSTOMER_ID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.DuplicateResourceException;
import com.interview.service.CustomerService;
import com.interview.util.TestFixtureLoader;
import com.interview.util.TestFixtures;

@WebMvcTest(CustomerController.class)
class CustomerControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/api/v1/customers";

    @Override
    protected String protectedEndpoint() {
        return ENDPOINT;
    }

    @MockitoBean
    CustomerService customerService;

    @Test
    void create_validRequest_returns201WithLocation() throws Exception {
        when(customerService.create(any())).thenReturn(TestFixtures.customerResponse());

        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("customer/create_customer_request.json")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(ENDPOINT)))
                .andExpect(content().json(TestFixtureLoader.load("customer/customer_response.json")));
    }

    @Test
    void create_missingFirstName_returns400WithFieldError() throws Exception {
        mockMvc.perform(post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("customer/create_customer_request_missing_first_name.json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.firstName").exists());

        verifyNoInteractions(customerService);
    }

    @Test
    void findById_existingCustomer_returns200() throws Exception {
        when(customerService.findById(CUSTOMER_ID)).thenReturn(TestFixtures.customerResponse());

        mockMvc.perform(get(ENDPOINT + "/{id}", CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(TestFixtureLoader.load("customer/customer_response.json")));
    }

    @Test
    void findById_unknownId_returns404() throws Exception {
        var unknownId = UUID.randomUUID();
        when(customerService.findById(unknownId))
                .thenThrow(new CustomerNotFoundException(unknownId));

        mockMvc.perform(get(ENDPOINT + "/{id}", unknownId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_returns200PagedResponse() throws Exception {
        var page = new PageImpl<>(List.of(TestFixtures.customerResponse()));
        when(customerService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(CUSTOMER_ID.toString()));
    }

    @Test
    void update_validRequest_returns200() throws Exception {
        when(customerService.update(eq(CUSTOMER_ID), any())).thenReturn(TestFixtures.customerResponse());

        mockMvc.perform(put(ENDPOINT + "/{id}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("customer/update_customer_request.json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CUSTOMER_ID.toString()));
    }

    @Test
    void update_unknownId_returns404() throws Exception {
        var unknownId = UUID.randomUUID();
        when(customerService.update(eq(unknownId), any()))
                .thenThrow(new CustomerNotFoundException(unknownId));

        mockMvc.perform(put(ENDPOINT + "/{id}", unknownId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("customer/update_customer_request.json")))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_duplicateEmail_returns409() throws Exception {
        when(customerService.update(eq(CUSTOMER_ID), any()))
                .thenThrow(new DuplicateResourceException("Email already in use"));

        mockMvc.perform(put(ENDPOINT + "/{id}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestFixtureLoader.load("customer/update_customer_request.json")))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_existingCustomer_returns204() throws Exception {
        doNothing().when(customerService).delete(CUSTOMER_ID);

        mockMvc.perform(delete(ENDPOINT + "/{id}", CUSTOMER_ID))
                .andExpect(status().isNoContent());

        verify(customerService).delete(CUSTOMER_ID);
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        var unknownId = UUID.randomUUID();
        doThrow(new CustomerNotFoundException(unknownId))
                .when(customerService).delete(unknownId);

        mockMvc.perform(delete(ENDPOINT + "/{id}", unknownId))
                .andExpect(status().isNotFound());
    }
}
