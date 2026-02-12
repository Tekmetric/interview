package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CustomerRequest;
import com.interview.entity.Customer;
import com.interview.enums.ContactMethod;
import com.interview.mapper.CustomerMapper;
import com.interview.repository.CustomerRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Full‑stack integration tests for {@link CustomerController}.
 *
 * <p>The Spring context is started on a random port and the real database (H2 in‑memory for the <code>test</code>
 * profile) is used. Security filters are disabled via {@code addFilters = false} because the purpose of these
 * tests is to validate controller–service interactions and global exception handling, not JWT flow.</p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("CustomerController ‑ Integration")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @BeforeEach
    void cleanDatabase() {
        customerRepository.deleteAll();
    }

    private CustomerRequest validRequest() {
        return new CustomerRequest("John", null, "Doe", "john.doe@example.com", "+1-555-0101", "123 Main St", LocalDate.of(1990, 1, 1),
            ContactMethod.EMAIL);
    }

    @Nested
    @DisplayName("POST /api/v1/customers")
    class CreateCustomer {

        @Test
        @DisplayName("should create a customer and return 201")
        void shouldCreateCustomer() throws Exception {
            mockMvc
                .perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
        }

        @Test
        @DisplayName("should fail validation and return 400 when firstName is blank")
        void shouldFailValidation() throws Exception {
            CustomerRequest invalid = new CustomerRequest("", null, "Doe", "jane.doe@example.com", null, null, null, null);

            mockMvc
                .perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("VALIDATION_ERROR")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/customers/{id}")
    class GetCustomerById {

        @Test
        @DisplayName("should return 200 with customer payload when found")
        void shouldReturnCustomer() throws Exception {
            // Persist via repository to generate ID
            var saved = customerRepository.save(validRequestToEntity());

            mockMvc
                .perform(get("/api/v1/customers/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
        }

        @Test
        @DisplayName("should return 404 when customer is not found")
        void shouldReturn404() throws Exception {
            mockMvc
                .perform(get("/api/v1/customers/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("CUSTOMER_NOT_FOUND")));
        }

        private Customer validRequestToEntity() {
            Customer entity = new Customer();
            entity.setFirstName("John");
            entity.setLastName("Doe");
            entity.setEmail("john.doe@example.com");
            entity.setPhone("+1‑555‑0101");
            return entity;
        }
    }

    @Nested
    @DisplayName("GET /api/v1/customers")
    class GetAllCustomers {

        @Test
        @DisplayName("should return 200 and list of customers")
        void shouldReturnAllCustomers() throws Exception {
            customerRepository.saveAll(List.of(new Customer(null, null, "Alice", "Smith", "alice@example.com", null, null, List.of(), Set.of()),
                new Customer(null, null, "Bob", "Jones", "bob@example.com", null, null, List.of(), Set.of())));

            mockMvc.perform(get("/api/v1/customers")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/customers/paginated")
    class GetCustomersPaginatedTests {
        @Test
        @DisplayName("should return paginated result with metadata")
        void shouldReturnPaginated() throws Exception {
            // insert 2 customers
            customerRepository.save(customerMapper.toEntity(validRequest()));
            customerRepository.save(
                customerMapper.toEntity(new CustomerRequest("Alice", null, "Smith", "alice@example.com", "555-0102", null, null, null)));

            mockMvc
                .perform(get("/api/v1/customers/paginated").param("page", "0").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.totalElements").value(2));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/customers/{id}")
    class UpdateCustomerTests {
        private Long id;
        private Long version;

        @BeforeEach
        void initCustomer() {
            Customer saved = customerRepository.save(customerMapper.toEntity(validRequest()));
            id = saved.getId();
            version = saved.getVersion(); // should be 0 on first persist
        }

        @Test
        @DisplayName("should update customer and bump version – 200")
        void shouldUpdateCustomer() throws Exception {
            CustomerRequest updateReq = new CustomerRequest("Johnny",                      // firstName changed
                version,                        // correct version
                "Doe", "johnny.doe@example.com",      // email changed
                "555-0109",                    // phone changed
                null, null, null);

            mockMvc
                .perform(
                    put("/api/v1/customers/{id}", id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.version", greaterThan(version.intValue())));
        }

        @Test
        @DisplayName("should return 400 when version is missing")
        void shouldReturn400WhenVersionMissing() throws Exception {
            CustomerRequest bad = new CustomerRequest("Johnny", null, "Doe", "johnny.doe@example.com", "555-0109", null, null, null);

            mockMvc
                .perform(put("/api/v1/customers/{id}", id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Version is required")));
        }

        @Test
        @DisplayName("should return 409 on optimistic locking conflict")
        void shouldReturn409OnVersionConflict() throws Exception {
            CustomerRequest conflict = new CustomerRequest("Johnny", version + 5, "Doe", "johnny.doe@example.com", "555-0109", null, null, null);

            mockMvc
                .perform(put("/api/v1/customers/{id}", id).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(conflict)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("OPTIMISTIC_LOCK_ERROR"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/customers/{id}")
    class DeleteCustomerTests {
        @Test
        @DisplayName("should delete customer and return 204")
        void shouldDeleteCustomer() throws Exception {
            Long id = customerRepository.save(customerMapper.toEntity(validRequest())).getId();

            mockMvc.perform(delete("/api/v1/customers/{id}", id)).andExpect(status().isNoContent());

            assertFalse(customerRepository.findById(id).isPresent(), "Customer should be removed from DB");
        }

        @Test
        @DisplayName("should return 404 when deleting non‑existent customer")
        void shouldReturn404OnDeleteMissing() throws Exception {
            mockMvc
                .perform(delete("/api/v1/customers/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CUSTOMER_NOT_FOUND"));
        }
    }
}