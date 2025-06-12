package com.interview.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.CustomerRequestDTO;
import com.interview.model.CustomerEntity;
import com.interview.model.VehicleEntity;
import com.interview.repository.CustomerRepository;
import com.interview.repository.VehicleRepository;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private VehicleRepository vehicleRepository;

  private static final String BASE_URL = "/api/v1/customers";

  @BeforeEach
  void setUp() {
    // Clean up data between tests instead of recreating context
    cleanDatabase();
  }

  @AfterEach
  void tearDown() {
    // Additional cleanup if needed
    cleanDatabase();
  }

  private void cleanDatabase() {
    // Clean in correct order to respect foreign key constraints
    vehicleRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /api/v1/customers")
  class GetAllCustomersTests {

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomers() throws Exception {
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return all customers with vehicle counts")
    void shouldReturnAllCustomersWithVehicleCounts() throws Exception {
      // Given
      CustomerEntity customer1 = createCustomer("John Doe", "john@example.com");
      CustomerEntity customer2 = createCustomer("Jane Smith", "jane@example.com");

      createVehicle(customer1, "Toyota", "Camry", 2020, "ABC123");
      createVehicle(customer1, "Honda", "Civic", 2019, "DEF456");
      createVehicle(customer2, "Ford", "F-150", 2021, "GHI789");

      // When & Then
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].name", is("John Doe")))
          .andExpect(jsonPath("$[0].email", is("john@example.com")))
          .andExpect(jsonPath("$[0].vehicleCount", is(2)))
          .andExpect(jsonPath("$[1].name", is("Jane Smith")))
          .andExpect(jsonPath("$[1].email", is("jane@example.com")))
          .andExpect(jsonPath("$[1].vehicleCount", is(1)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/customers/paginated")
  class GetAllCustomersPaginatedTests {

    @Test
    @DisplayName("Should return paginated customers with default parameters")
    void shouldReturnPaginatedCustomersWithDefaults() throws Exception {
      // Given
      createMultipleCustomers(15);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/paginated"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content", hasSize(10)))
          .andExpect(jsonPath("$.totalElements", is(15)))
          .andExpect(jsonPath("$.totalPages", is(2)))
          .andExpect(jsonPath("$.number", is(0)))
          .andExpect(jsonPath("$.size", is(10)));
    }

    @Test
    @DisplayName("Should return paginated customers with custom parameters")
    void shouldReturnPaginatedCustomersWithCustomParams() throws Exception {
      // Given
      createMultipleCustomers(25);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "1")
              .param("size", "5")
              .param("sortBy", "name")
              .param("sortDir", "desc"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content", hasSize(5)))
          .andExpect(jsonPath("$.totalElements", is(25)))
          .andExpect(jsonPath("$.totalPages", is(5)))
          .andExpect(jsonPath("$.number", is(1)))
          .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    @DisplayName("Should handle invalid page parameters gracefully")
    void shouldHandleInvalidPageParameters() throws Exception {
      // Given
      createMultipleCustomers(5);

      // When & Then - negative page
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "-1"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/customers/{id}")
  class GetCustomerByIdTests {

    @Test
    @DisplayName("Should return customer by valid id")
    void shouldReturnCustomerByValidId() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(customer.getId().intValue())))
          .andExpect(jsonPath("$.name", is("John Doe")))
          .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent customer id")
    void shouldReturn404ForNonExistentId() throws Exception {
      mockMvc.perform(get(BASE_URL + "/{id}", 999L))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid id format")
    void shouldReturn400ForInvalidIdFormat() throws Exception {
      mockMvc.perform(get(BASE_URL + "/{id}", "invalid"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/customers/{id}/with-vehicles")
  class GetCustomerByIdWithVehiclesTests {

    @Test
    @DisplayName("Should return customer with vehicles")
    void shouldReturnCustomerWithVehicles() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createVehicle(customer, "Honda", "Civic", 2019, "DEF456");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}/with-vehicles", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(customer.getId().intValue())))
          .andExpect(jsonPath("$.name", is("John Doe")))
          .andExpect(jsonPath("$.vehicles", hasSize(2)))
          .andExpect(jsonPath("$.vehicles[0].make", is("Toyota")))
          .andExpect(jsonPath("$.vehicles[1].make", is("Honda")));
    }

    @Test
    @DisplayName("Should return customer with empty vehicles list")
    void shouldReturnCustomerWithEmptyVehiclesList() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}/with-vehicles", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.vehicles", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/customers/email/{email}")
  class GetCustomerByEmailTests {

    @Test
    @DisplayName("Should return customer by valid email")
    void shouldReturnCustomerByValidEmail() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/email/{email}", "john@example.com"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(customer.getId().intValue())))
          .andExpect(jsonPath("$.name", is("John Doe")))
          .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent email")
    void shouldReturn404ForNonExistentEmail() throws Exception {
      mockMvc.perform(get(BASE_URL + "/email/{email}", "nonexistent@example.com"))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle email with special characters")
    void shouldHandleEmailWithSpecialCharacters() throws Exception {
      // Given
      createCustomer("John Doe", "john+test@example.com");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/email/{email}", "john+test@example.com"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.email", is("john+test@example.com")));
    }
  }

  @Nested
  @DisplayName("POST /api/v1/customers")
  class CreateCustomerTests {

    @Test
    @DisplayName("Should create customer with all fields")
    void shouldCreateCustomerWithAllFields() throws Exception {
      // Given
      CustomerRequestDTO request = new CustomerRequestDTO(
          "John Doe",
          "john@example.com",
          "1234567890",
          "123 Main St"
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.name", is("John Doe")))
          .andExpect(jsonPath("$.email", is("john@example.com")))
          .andExpect(jsonPath("$.phoneNumber", is("1234567890")))
          .andExpect(jsonPath("$.address", is("123 Main St")));
    }

    @Test
    @DisplayName("Should create customer with required fields only")
    void shouldCreateCustomerWithRequiredFieldsOnly() throws Exception {
      // Given
      CustomerRequestDTO request = CustomerRequestDTO.of("John Doe", "john@example.com");

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name", is("John Doe")))
          .andExpect(jsonPath("$.email", is("john@example.com")))
          .andExpect(jsonPath("$.phoneNumber", nullValue()))
          .andExpect(jsonPath("$.address", nullValue()));
    }

    @Test
    @DisplayName("Should return 400 for missing required fields")
    void shouldReturn400ForMissingRequiredFields() throws Exception {
      // Given
      CustomerRequestDTO request = new CustomerRequestDTO(null, null, null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid email format")
    void shouldReturn400ForInvalidEmailFormat() throws Exception {
      // Given
      CustomerRequestDTO request = new CustomerRequestDTO("John Doe", "invalid-email", null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for duplicate email")
    void shouldReturn400ForDuplicateEmail() throws Exception {
      // Given
      createCustomer("Existing User", "john@example.com");
      CustomerRequestDTO request = new CustomerRequestDTO("John Doe", "john@example.com", null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 400 for field length violations")
    void shouldReturn400ForFieldLengthViolations() throws Exception {
      // Given - name too long (over 100 characters)
      String longName = "a".repeat(101);
      CustomerRequestDTO request = new CustomerRequestDTO(longName, "john@example.com", null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid content type")
    void shouldReturn400ForInvalidContentType() throws Exception {
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.TEXT_PLAIN)
              .content("invalid content"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for malformed JSON")
    void shouldReturn400ForMalformedJson() throws Exception {
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content("{invalid json")) // NOSONAR
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/customers/{id}")
  class UpdateCustomerTests {

    @Test
    @DisplayName("Should update existing customer")
    void shouldUpdateExistingCustomer() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      CustomerRequestDTO updateRequest = new CustomerRequestDTO(
          "John Updated",
          "john.updated@example.com",
          "9876543210",
          "456 Updated St"
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", customer.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(customer.getId().intValue())))
          .andExpect(jsonPath("$.name", is("John Updated")))
          .andExpect(jsonPath("$.email", is("john.updated@example.com")))
          .andExpect(jsonPath("$.phoneNumber", is("9876543210")))
          .andExpect(jsonPath("$.address", is("456 Updated St")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent customer")
    void shouldReturn404ForNonExistentCustomer() throws Exception {
      // Given
      CustomerRequestDTO updateRequest = new CustomerRequestDTO("John", "john@example.com", null, null);

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", 999L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for validation errors")
    void shouldReturn400ForValidationErrors() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      CustomerRequestDTO updateRequest = new CustomerRequestDTO("", "invalid-email", null, null);

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", customer.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for duplicate email on different customer")
    void shouldReturn400ForDuplicateEmailOnDifferentCustomer() throws Exception {
      // Given
      createCustomer("John Doe", "john@example.com");
      CustomerEntity customer2 = createCustomer("Jane Smith", "jane@example.com");

      CustomerRequestDTO updateRequest = new CustomerRequestDTO("Jane Updated", "john@example.com", null, null);

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", customer2.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isConflict());
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/customers/{id}")
  class DeleteCustomerTests {

    @Test
    @DisplayName("Should delete existing customer")
    void shouldDeleteExistingCustomer() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");

      // When & Then
      mockMvc.perform(delete(BASE_URL + "/{id}", customer.getId()))
          .andDo(print())
          .andExpect(status().isNoContent());

      // Verify deletion
      mockMvc.perform(get(BASE_URL + "/{id}", customer.getId()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 for non-existent customer")
    void shouldReturn404ForNonExistentCustomer() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/{id}", 999L))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should cascade delete customer with vehicles")
    void shouldCascadeDeleteCustomerWithVehicles() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When
      mockMvc.perform(delete(BASE_URL + "/{id}", customer.getId()))
          .andDo(print())
          .andExpect(status().isNoContent());

      // Then - verify both customer and vehicle are deleted
      mockMvc.perform(get(BASE_URL + "/{id}", customer.getId()))
          .andExpect(status().isNotFound());

      mockMvc.perform(get("/api/v1/vehicles/{id}", vehicle.getId()))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/customers/count")
  class GetTotalCustomersTests {

    @Test
    @DisplayName("Should return zero count when no customers")
    void shouldReturnZeroCountWhenNoCustomers() throws Exception {
      mockMvc.perform(get(BASE_URL + "/count"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should return correct customer count")
    void shouldReturnCorrectCustomerCount() throws Exception {
      // Given
      createMultipleCustomers(5);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/count"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("5"));
    }
  }

  // Helper methods
  private CustomerEntity createCustomer(String name, String email) {
    CustomerEntity customer = CustomerEntity.builder()
        .name(name)
        .email(email)
        .createdDate(OffsetDateTime.now())
        .updatedDate(OffsetDateTime.now())
        .build();
    return customerRepository.save(customer);
  }

  private VehicleEntity createVehicle(CustomerEntity customer, String make, String model, Integer year, String licensePlate) {
    VehicleEntity vehicle = VehicleEntity.builder()
        .customer(customer)
        .make(make)
        .model(model)
        .year(year)
        .licensePlate(licensePlate)
        .createdDate(OffsetDateTime.now())
        .updatedDate(OffsetDateTime.now())
        .build();
    return vehicleRepository.save(vehicle);
  }

  private void createMultipleCustomers(int count) {
    for (int i = 1; i <= count; i++) {
      createCustomer("Customer " + i, "customer" + i + "@example.com");
    }
  }

}
