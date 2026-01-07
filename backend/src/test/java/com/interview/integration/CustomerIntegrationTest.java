package com.interview.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.interview.dto.CustomerDTO;
import com.interview.entity.CustomerEntity;
import com.interview.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@DisplayName("Customer Integration Tests")
class CustomerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = JsonMapper.builder().build();
        customerRepository.deleteAll();
    }

    private CustomerDTO createCustomer(String firstName, String lastName, String email, String phone) {
        CustomerDTO customer = new CustomerDTO();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        return customer;
    }

    @Test
    @DisplayName("Full CRUD Lifecycle")
    void testFullCrudLifecycle() throws Exception {
        assertThat(customerRepository.count()).isEqualTo(0);

        CustomerDTO newCustomer = createCustomer("Integration", "Test", "integration@test.com", "555-1234");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.firstName").value("Integration"));

        assertThat(customerRepository.count()).isEqualTo(1);
        assertThat(customerRepository.existsByEmail("integration@test.com")).isTrue();

        CustomerEntity savedEntity = customerRepository.findByEmail("integration@test.com").orElseThrow();
        Long customerId = savedEntity.getId();
        assertThat(customerId).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo("Integration");

        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.firstName").value("Integration"));

        CustomerDTO updateDTO = createCustomer("Updated", "Name", "updated@test.com", "555-9999");

        mockMvc.perform(put("/api/v1/customers/" + customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));

        CustomerEntity updatedEntity = customerRepository.findById(customerId).orElseThrow();
        assertThat(updatedEntity.getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedEntity.getFirstName()).isEqualTo("Updated");

        CustomerDTO duplicateCustomer = createCustomer("Duplicate", "Test", "updated@test.com", "555-5555");

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCustomer)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Customer already exists with email: updated@test.com"));

        assertThat(customerRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/customers/" + customerId))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.count()).isEqualTo(0);
        assertThat(customerRepository.findById(customerId)).isEmpty();

        mockMvc.perform(get("/api/v1/customers/" + customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Advanced Features - Pagination and Search")
    void advancedFeaturesTest() throws Exception {
        List<CustomerDTO> customers = List.of(
                createCustomer("Alice", "Smith", "alice@test.com", "555-0001"),
                createCustomer("Bob", "Smith", "bob@test.com", "555-0002"),
                createCustomer("Charlie", "Brown", "charlie@test.com", "555-0003")
        );

        for (CustomerDTO customer : customers) {
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer)))
                    .andExpect(status().isCreated());
        }

        assertThat(customerRepository.count()).isEqualTo(3);

        mockMvc.perform(get("/api/v1/customers")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));

        mockMvc.perform(get("/api/v1/customers")
                        .param("lastName", "Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content[1].lastName").value("Smith"));

        mockMvc.perform(get("/api/v1/customers")
                        .param("firstName", "Charlie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("charlie@test.com"));
    }
}
