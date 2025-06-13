package com.interview.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.VehicleRequestDTO;
import com.interview.model.CustomerEntity;
import com.interview.model.RepairOrderEntity;
import com.interview.model.RepairOrderStatus;
import com.interview.model.VehicleEntity;
import com.interview.repository.CustomerRepository;
import com.interview.repository.RepairOrderRepository;
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
@DisplayName("Vehicle Controller Integration Tests")
class VehicleControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private VehicleRepository vehicleRepository;

  @Autowired
  private RepairOrderRepository repairOrderRepository;

  private static final String BASE_URL = "/api/v1/vehicles";

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
    repairOrderRepository.deleteAll();
    vehicleRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /api/v1/vehicles")
  class GetAllVehiclesTests {

    @Test
    @DisplayName("Should return empty list when no vehicles exist")
    void shouldReturnEmptyListWhenNoVehicles() throws Exception {
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return all vehicles with repair order counts")
    void shouldReturnAllCustomersWithVehicleCounts() throws Exception {
      // Given
      CustomerEntity customer1 = createCustomer("John Doe", "john@example.com");
      CustomerEntity customer2 = createCustomer("Jane Smith", "jane@example.com");

      VehicleEntity vehicle1 = createVehicle(customer1, "Toyota", "Camry", 2020, "ABC123");
      createVehicle(customer2, "Honda", "Civic", 2019, "DEF456");

      createRepairOrder(vehicle1, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle1, "Tire rotation", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].make", is("Toyota")))
          .andExpect(jsonPath("$[0].model", is("Camry")))
          .andExpect(jsonPath("$[0].year", is(2020)))
          .andExpect(jsonPath("$[0].licensePlate", is("ABC123")))
          .andExpect(jsonPath("$[0].repairOrderCount", is(2)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/vehicles/paginated")
  class GetAllVehiclesPaginatedTests {

    @Test
    @DisplayName("Should return paginated vehicles with default parameters")
    void shouldReturnPaginatedVehiclesWithDefaults() throws Exception {
      // Given
      createMultipleVehicles(15);

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
    @DisplayName("Should return paginated vehicles with custom parameters")
    void shouldReturnPaginatedVehiclesWithCustomParams() throws Exception {
      // Given
      createMultipleVehicles(25);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "1")
              .param("size", "5")
              .param("sortBy", "year")
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
      createMultipleVehicles(5);

      // When & Then - negative page
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "-1"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/vehicles/{id}")
  class GetVehicleByIdTests {

    @Test
    @DisplayName("Should return vehicle by valid id")
    void shouldReturnVehicleByValidId() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(vehicle.getId().intValue())))
          .andExpect(jsonPath("$.make", is("Toyota")))
          .andExpect(jsonPath("$.model", is("Camry")))
          .andExpect(jsonPath("$.year", is(2020)))
          .andExpect(jsonPath("$.licensePlate", is("ABC123")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent vehicle id")
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
  @DisplayName("GET /api/v1/vehicles/{id}/with-repair-orders")
  class GetCustomerByIdWithVehiclesTests {

    @Test
    @DisplayName("Should return vehicle with repair orders")
    void shouldReturnVehicleWithRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle, "Brake inspection", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}/with-repair-orders", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(vehicle.getId().intValue())))
          .andExpect(jsonPath("$.make", is("Toyota")))
          .andExpect(jsonPath("$.repairOrders", hasSize(2)))
          .andExpect(jsonPath("$.repairOrders[0].description", is("Tire rotation")))
          .andExpect(jsonPath("$.repairOrders[1].description", is("Brake inspection")));
    }

    @Test
    @DisplayName("Should return vehicle with empty repair orders list")
    void shouldReturnVehicleWithEmptyRepairOrdersList() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Opel", "Mokka", 2024, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}/with-repair-orders", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.repairOrders", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/vehicles/license-plate/{licensePlate}")
  class GetVehicleByLicensePlateTests {

    @Test
    @DisplayName("Should return vehicle by valid license plate")
    void shouldReturnVehicleByValidLicensePlate() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/license-plate/{licensePlate}", "ABC123"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(vehicle.getId().intValue())))
          .andExpect(jsonPath("$.make", is("Toyota")))
          .andExpect(jsonPath("$.model", is("Camry")))
          .andExpect(jsonPath("$.year", is(2020)))
          .andExpect(jsonPath("$.licensePlate", is("ABC123")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent license plate")
    void shouldReturn404ForNonExistentLicensePlate() throws Exception {
      mockMvc.perform(get(BASE_URL + "/license-plate/{licensePlate}", "nonexistentplate"))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

  }

  @Nested
  @DisplayName("GET /api/v1/vehicles/customer/{customerId}")
  class GetVehiclesByCustomerTests {

    @Test
    @DisplayName("Should return vehicles by valid customer id")
    void shouldReturnVehicleByValidCustomer() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createVehicle(customer, "Honda", "Civic", 2019, "DEF456");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].make", is("Toyota")))
          .andExpect(jsonPath("$[0].model", is("Camry")))
          .andExpect(jsonPath("$[0].year", is(2020)))
          .andExpect(jsonPath("$[0].licensePlate", is("ABC123")))
          .andExpect(jsonPath("$[1].make", is("Honda")))
          .andExpect(jsonPath("$[1].model", is("Civic")))
          .andExpect(jsonPath("$[1].year", is(2019)))
          .andExpect(jsonPath("$[1].licensePlate", is("DEF456")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent customer")
    void shouldReturnEmptyListWhenNoVehicles() throws Exception {
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}", 99L))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }

  }

  @Nested
  @DisplayName("POST /api/v1/vehicles")
  class CreateVehicleTests {

    @Test
    @DisplayName("Should create vehicle with all fields")
    void shouldCreateVehicleWithAllFields() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleRequestDTO request = new VehicleRequestDTO(
          customer.getId(),
          "Toyota",
          "Camry",
          2020,
          "ABC123"
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.make", is("Toyota")))
          .andExpect(jsonPath("$.model", is("Camry")))
          .andExpect(jsonPath("$.year", is(2020)))
          .andExpect(jsonPath("$.licensePlate", is("ABC123")))
          .andExpect(jsonPath("$.customer.id", is(customer.getId().intValue())))
          .andExpect(jsonPath("$.customer.name", is("John Doe")))
          .andExpect(jsonPath("$.customer.email", is("john@example.com")));
    }

    @Test
    @DisplayName("Should return 400 for missing required fields")
    void shouldReturn400ForMissingRequiredFields() throws Exception {
      // Given
      VehicleRequestDTO request = new VehicleRequestDTO(null, null, null, null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid year format")
    void shouldReturn400ForInvalidYearFormat() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleRequestDTO request = new VehicleRequestDTO(
          customer.getId(),
          "Toyota",
          "Camry",
          1678,
          "ABC123"
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for duplicate license plate")
    void shouldReturn400ForDuplicateLicensePlate() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      VehicleRequestDTO request = new VehicleRequestDTO(
          customer.getId(),
          "Honda",
          "Civic",
          2019,
          "ABC123"
      );

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
      // Given - name too long (over 50 characters)
      String longName = "a".repeat(51);
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleRequestDTO request = new VehicleRequestDTO(
          customer.getId(),
          "Honda",
          longName,
          2019,
          "ABC123"
      );

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
  @DisplayName("PUT /api/v1/vehicles/{id}")
  class UpdateCustomerTests {

    @Test
    @DisplayName("Should update existing vehicle")
    void shouldUpdateExistingVehicle() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          customer.getId(),
          "Toyota",
          "Camry Updated",
          2021,
          "XYZ789"
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", vehicle.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(vehicle.getId().intValue())))
          .andExpect(jsonPath("$.make", is("Toyota")))
          .andExpect(jsonPath("$.model", is("Camry Updated")))
          .andExpect(jsonPath("$.year", is(2021)))
          .andExpect(jsonPath("$.licensePlate", is("XYZ789")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent vehicle")
    void shouldReturn404ForNonExistentVehicle() throws Exception {
      // Given
      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          1L, "Opel", "Corsa", 2005, "ABC123"
      );

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
      VehicleEntity vehicle = createVehicle(customer, "Opel", "Corsa", 2020, "ABC123");

      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          customer.getId(), "", "Corsa", null, "ABC123"
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", vehicle.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for duplicate license plate on different vehicle")
    void shouldReturn400ForDuplicateLicensePlateOnDifferentVehicle() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      VehicleEntity vehicle2 = createVehicle(customer, "Honda", "Civic", 2019, "DEF456");

      VehicleRequestDTO updateRequest = new VehicleRequestDTO(
          customer.getId(), "Honda", "Civic Updated", 2021, "ABC123"
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", vehicle2.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isConflict());
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/vehicle/{id}")
  class DeleteVehicleTests {

    @Test
    @DisplayName("Should delete existing vehicle")
    void shouldDeleteExistingVehicle() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(delete(BASE_URL + "/{id}", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isNoContent());

      // Verify deletion
      mockMvc.perform(get(BASE_URL + "/{id}", vehicle.getId()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 for non-existent vehicle")
    void shouldReturn404ForNonExistentVehicle() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/{id}", 999L))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should cascade delete vehicle with repair orders")
    void shouldCascadeDeleteCustomerWithVehicles() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);

      // When
      mockMvc.perform(delete(BASE_URL + "/{id}", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isNoContent());

      // Then - verify both vehicle and repair orders are deleted
      mockMvc.perform(get(BASE_URL + "/{id}", vehicle.getId()))
          .andExpect(status().isNotFound());

      mockMvc.perform(get("/api/v1/repair-orders/vehicle/{id}", vehicle.getId()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/vehicles/count")
  class GetTotalVehiclesTests {

    @Test
    @DisplayName("Should return zero count when no vehicles")
    void shouldReturnZeroCountWhenNoVehicles() throws Exception {
      mockMvc.perform(get(BASE_URL + "/count"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should return correct vehicle count")
    void shouldReturnCorrectCustomerCount() throws Exception {
      // Given
      createMultipleVehicles(5);

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

  private RepairOrderEntity createRepairOrder(VehicleEntity vehicle, String description, RepairOrderStatus status) {
    RepairOrderEntity repairOrder = RepairOrderEntity.builder()
        .vehicle(vehicle)
        .description(description)
        .status(status)
        .createdDate(OffsetDateTime.now())
        .updatedDate(OffsetDateTime.now())
        .build();
    return repairOrderRepository.save(repairOrder);
  }

  private void createMultipleVehicles(int count) {
    for (int i = 1; i <= count; i++) {
      CustomerEntity customer = createCustomer("Customer " + i, "customer" + i + "@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Make " + i, "Model " + i, 2000 + i, "LP" + i);
      createRepairOrder(vehicle, "Repair order for vehicle " + i, RepairOrderStatus.PENDING);
    }
  }

}
