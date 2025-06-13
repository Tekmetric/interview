package com.interview.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.RepairOrderRequestDTO;
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
@DisplayName("Repair Order Controller Integration Tests")
class RepairOrderControllerIntegrationTest {

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

  private static final String BASE_URL = "/api/v1/repair-orders";

  @BeforeEach
  void setUp() {
    cleanDatabase();
  }

  @AfterEach
  void tearDown() {
    cleanDatabase();
  }

  private void cleanDatabase() {
    // Clean in correct order to respect foreign key constraints
    repairOrderRepository.deleteAll();
    vehicleRepository.deleteAll();
    customerRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders")
  class GetAllRepairOrdersTests {

    @Test
    @DisplayName("Should return empty list when no repair orders exist")
    void shouldReturnEmptyListWhenNoRepairOrders() throws Exception {
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return all repair orders")
    void shouldReturnAllRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].description", is("Oil change")))
          .andExpect(jsonPath("$[0].status", is("COMPLETED")))
          .andExpect(jsonPath("$[1].description", is("Tire rotation")))
          .andExpect(jsonPath("$[1].status", is("PENDING")));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/paginated")
  class GetAllRepairOrdersPaginatedTests {

    @Test
    @DisplayName("Should return paginated repair orders with default parameters")
    void shouldReturnPaginatedRepairOrdersWithDefaults() throws Exception {
      // Given
      createMultipleRepairOrders(15);

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
    @DisplayName("Should return paginated repair orders with custom parameters")
    void shouldReturnPaginatedRepairOrdersWithCustomParams() throws Exception {
      // Given
      createMultipleRepairOrders(25);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "1")
              .param("size", "5")
              .param("sortBy", "createdDate")
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
      createMultipleRepairOrders(5);

      // When & Then - negative page
      mockMvc.perform(get(BASE_URL + "/paginated")
              .param("page", "-1"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/{id}")
  class GetRepairOrderByIdTests {

    @Test
    @DisplayName("Should return repair order by valid id")
    void shouldReturnRepairOrderByValidId() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/{id}", repairOrder.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(repairOrder.getId().intValue())))
          .andExpect(jsonPath("$.description", is("Oil change")))
          .andExpect(jsonPath("$.status", is("COMPLETED")))
          .andExpect(jsonPath("$.vehicle.id", is(vehicle.getId().intValue())))
          .andExpect(jsonPath("$.vehicle.make", is("Toyota")))
          .andExpect(jsonPath("$.vehicle.model", is("Camry")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent repair order id")
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
  @DisplayName("GET /api/v1/repair-orders/vehicle/{vehicleId}")
  class GetRepairOrdersByVehicleIdTests {

    @Test
    @DisplayName("Should return repair orders by valid vehicle id")
    void shouldReturnRepairOrdersByValidVehicleId() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/vehicle/{vehicleId}", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].description", is("Oil change")))
          .andExpect(jsonPath("$[1].description", is("Tire rotation")));
    }

    @Test
    @DisplayName("Should return empty list for vehicle with no repair orders")
    void shouldReturnEmptyListForVehicleWithNoRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/vehicle/{vehicleId}", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/customer/{customerId}")
  class GetRepairOrdersByCustomerIdTests {

    @Test
    @DisplayName("Should return repair orders by valid customer id")
    void shouldReturnRepairOrdersByValidCustomerId() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle1 = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      VehicleEntity vehicle2 = createVehicle(customer, "Honda", "Civic", 2019, "DEF456");
      createRepairOrder(vehicle1, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle2, "Brake check", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].description", is("Oil change")))
          .andExpect(jsonPath("$[1].description", is("Brake check")));
    }

    @Test
    @DisplayName("Should return empty list for customer with no repair orders")
    void shouldReturnEmptyListForCustomerWithNoRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/status/{status}")
  class GetRepairOrdersByStatusTests {

    @Test
    @DisplayName("Should return repair orders by status")
    void shouldReturnRepairOrdersByStatus() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle, "Brake check", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/status/{status}", RepairOrderStatus.PENDING))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].status", is("PENDING")))
          .andExpect(jsonPath("$[1].status", is("PENDING")));
    }

    @Test
    @DisplayName("Should return empty list for status with no repair orders")
    void shouldReturnEmptyListForStatusWithNoRepairOrders() throws Exception {
      mockMvc.perform(get(BASE_URL + "/status/{status}", RepairOrderStatus.IN_PROGRESS))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/license-plate/{licensePlate}")
  class GetRepairOrdersByLicensePlateTests {

    @Test
    @DisplayName("Should return repair orders by license plate")
    void shouldReturnRepairOrdersByLicensePlate() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/license-plate/{licensePlate}", "ABC123"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].description", is("Oil change")))
          .andExpect(jsonPath("$[1].description", is("Tire rotation")));
    }

    @Test
    @DisplayName("Should return empty list for non-existent license plate")
    void shouldReturnEmptyListForNonExistentLicensePlate() throws Exception {
      mockMvc.perform(get(BASE_URL + "/license-plate/{licensePlate}", "NONEXISTENT"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(0)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/date-range")
  class GetRepairOrdersByDateRangeTests {

    @Test
    @DisplayName("Should return repair orders within date range")
    void shouldReturnRepairOrdersWithinDateRange() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      OffsetDateTime startDate = OffsetDateTime.now().minusDays(10);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(10);

      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/date-range")
              .param("startDate", startDate.toString())
              .param("endDate", endDate.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should return 400 for invalid date format")
    void shouldReturn400ForInvalidDateFormat() throws Exception {
      mockMvc.perform(get(BASE_URL + "/date-range")
              .param("startDate", "invalid-date")
              .param("endDate", "invalid-date"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for missing date parameters")
    void shouldReturn400ForMissingDateParameters() throws Exception {
      mockMvc.perform(get(BASE_URL + "/date-range"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/updated-date-range")
  class GetRepairOrdersByUpdatedDateRangeTests {

    @Test
    @DisplayName("Should return repair orders within updated date range")
    void shouldReturnRepairOrdersWithinUpdatedDateRange() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      OffsetDateTime startDate = OffsetDateTime.now().minusDays(10);
      OffsetDateTime endDate = OffsetDateTime.now().plusDays(10);

      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/updated-date-range")
              .param("startDate", startDate.toString())
              .param("endDate", endDate.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(1)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/active")
  class GetActiveRepairOrdersTests {

    @Test
    @DisplayName("Should return active repair orders")
    void shouldReturnActiveRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.IN_PROGRESS);
      createRepairOrder(vehicle, "Brake check", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/active"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2))); // PENDING and IN_PROGRESS are active
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/completed")
  class GetCompletedRepairOrdersTests {

    @Test
    @DisplayName("Should return completed repair orders")
    void shouldReturnCompletedRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle, "Brake check", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/completed"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)))
          .andExpect(jsonPath("$[0].status", is("COMPLETED")))
          .andExpect(jsonPath("$[1].status", is("COMPLETED")));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/recent")
  class GetRecentRepairOrdersByStatusTests {

    @Test
    @DisplayName("Should return recent repair orders by status")
    void shouldReturnRecentRepairOrdersByStatus() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Brake check", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/recent")
              .param("status", RepairOrderStatus.COMPLETED.toString())
              .param("days", "30"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should use default days parameter when not provided")
    void shouldUseDefaultDaysParameterWhenNotProvided() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/recent")
              .param("status", RepairOrderStatus.COMPLETED.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
  }

  @Nested
  @DisplayName("POST /api/v1/repair-orders")
  class CreateRepairOrderTests {

    @Test
    @DisplayName("Should create repair order with valid data")
    void shouldCreateRepairOrderWithValidData() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      RepairOrderRequestDTO request = RepairOrderRequestDTO.of(
          vehicle.getId(),
          "Oil change and filter replacement",
          RepairOrderStatus.PENDING
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", notNullValue()))
          .andExpect(jsonPath("$.description", is("Oil change and filter replacement")))
          .andExpect(jsonPath("$.status", is("PENDING")))
          .andExpect(jsonPath("$.vehicle.id", is(vehicle.getId().intValue())));
    }

    @Test
    @DisplayName("Should return 400 for missing required fields")
    void shouldReturn400ForMissingRequiredFields() throws Exception {
      // Given
      RepairOrderRequestDTO request = new RepairOrderRequestDTO(null, null, null);

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for description too long")
    void shouldReturn400ForDescriptionTooLong() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      String longDescription = "a".repeat(1001); // Over 1000 characters
      RepairOrderRequestDTO request = RepairOrderRequestDTO.of(
          vehicle.getId(),
          longDescription,
          RepairOrderStatus.PENDING
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for non-existent vehicle")
    void shouldReturn400ForNonExistentVehicle() throws Exception {
      // Given
      RepairOrderRequestDTO request = RepairOrderRequestDTO.of(
          999L,
          "Oil change",
          RepairOrderStatus.PENDING
      );

      // When & Then
      mockMvc.perform(post(BASE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andDo(print())
          .andExpect(status().isNotFound());
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
  @DisplayName("PUT /api/v1/repair-orders/{id}")
  class UpdateRepairOrderTests {

    @Test
    @DisplayName("Should update existing repair order")
    void shouldUpdateExistingRepairOrder() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      RepairOrderRequestDTO updateRequest = RepairOrderRequestDTO.of(
          vehicle.getId(),
          "Oil change and tire rotation",
          RepairOrderStatus.IN_PROGRESS
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", repairOrder.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(repairOrder.getId().intValue())))
          .andExpect(jsonPath("$.description", is("Oil change and tire rotation")))
          .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent repair order")
    void shouldReturn404ForNonExistentRepairOrder() throws Exception {
      // Given
      RepairOrderRequestDTO updateRequest = RepairOrderRequestDTO.of(
          1L,
          "Oil change",
          RepairOrderStatus.PENDING
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
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      RepairOrderRequestDTO updateRequest = new RepairOrderRequestDTO(
          vehicle.getId(), "", null
      );

      // When & Then
      mockMvc.perform(put(BASE_URL + "/{id}", repairOrder.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateRequest)))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PATCH /api/v1/repair-orders/{id}/status")
  class UpdateRepairOrderStatusTests {

    @Test
    @DisplayName("Should update repair order status")
    void shouldUpdateRepairOrderStatus() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(patch(BASE_URL + "/{id}/status", repairOrder.getId())
              .param("status", RepairOrderStatus.IN_PROGRESS.toString()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id", is(repairOrder.getId().intValue())))
          .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
          .andExpect(jsonPath("$.description", is("Oil change")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent repair order")
    void shouldReturn404ForNonExistentRepairOrder() throws Exception {
      mockMvc.perform(patch(BASE_URL + "/{id}/status", 999L)
              .param("status", RepairOrderStatus.COMPLETED.toString()))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid status")
    void shouldReturn400ForInvalidStatus() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(patch(BASE_URL + "/{id}/status", repairOrder.getId())
              .param("status", "INVALID_STATUS"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for missing status parameter")
    void shouldReturn400ForMissingStatusParameter() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(patch(BASE_URL + "/{id}/status", repairOrder.getId()))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/repair-orders/{id}")
  class DeleteRepairOrderTests {

    @Test
    @DisplayName("Should delete existing repair order")
    void shouldDeleteExistingRepairOrder() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      RepairOrderEntity repairOrder = createRepairOrder(vehicle, "Oil change", RepairOrderStatus.PENDING);

      // When & Then
      mockMvc.perform(delete(BASE_URL + "/{id}", repairOrder.getId()))
          .andDo(print())
          .andExpect(status().isNoContent());

      // Verify deletion
      mockMvc.perform(get(BASE_URL + "/{id}", repairOrder.getId()))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 404 for non-existent repair order")
    void shouldReturn404ForNonExistentRepairOrder() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/{id}", 999L))
          .andDo(print())
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid id format")
    void shouldReturn400ForInvalidIdFormat() throws Exception {
      mockMvc.perform(delete(BASE_URL + "/{id}", "invalid"))
          .andDo(print())
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/count")
  class GetTotalRepairOrdersTests {

    @Test
    @DisplayName("Should return zero count when no repair orders")
    void shouldReturnZeroCountWhenNoRepairOrders() throws Exception {
      mockMvc.perform(get(BASE_URL + "/count"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should return correct repair order count")
    void shouldReturnCorrectRepairOrderCount() throws Exception {
      // Given
      createMultipleRepairOrders(7);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/count"))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("7"));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/vehicle/{vehicleId}/count")
  class GetRepairOrderCountByVehicleIdTests {

    @Test
    @DisplayName("Should return correct count for vehicle with repair orders")
    void shouldReturnCorrectCountForVehicleWithRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      createRepairOrder(vehicle, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle, "Tire rotation", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle, "Brake check", RepairOrderStatus.IN_PROGRESS);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/vehicle/{vehicleId}/count", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("Should return zero count for vehicle with no repair orders")
    void shouldReturnZeroCountForVehicleWithNoRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/vehicle/{vehicleId}/count", vehicle.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should return zero count for non-existent vehicle")
    void shouldReturnZeroCountForNonExistentVehicle() throws Exception {
      mockMvc.perform(get(BASE_URL + "/vehicle/{vehicleId}/count", 999L))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/customer/{customerId}/count")
  class GetRepairOrderCountByCustomerIdTests {

    @Test
    @DisplayName("Should return correct count for customer with repair orders")
    void shouldReturnCorrectCountForCustomerWithRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      VehicleEntity vehicle1 = createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");
      VehicleEntity vehicle2 = createVehicle(customer, "Honda", "Civic", 2019, "DEF456");

      createRepairOrder(vehicle1, "Oil change", RepairOrderStatus.COMPLETED);
      createRepairOrder(vehicle1, "Tire rotation", RepairOrderStatus.PENDING);
      createRepairOrder(vehicle2, "Brake check", RepairOrderStatus.IN_PROGRESS);

      // When & Then
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}/count", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("Should return zero count for customer with no repair orders")
    void shouldReturnZeroCountForCustomerWithNoRepairOrders() throws Exception {
      // Given
      CustomerEntity customer = createCustomer("John Doe", "john@example.com");
      createVehicle(customer, "Toyota", "Camry", 2020, "ABC123");

      // When & Then
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}/count", customer.getId()))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
    }

    @Test
    @DisplayName("Should return zero count for non-existent customer")
    void shouldReturnZeroCountForNonExistentCustomer() throws Exception {
      mockMvc.perform(get(BASE_URL + "/customer/{customerId}/count", 999L))
          .andDo(print())
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(content().string("0"));
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

  private void createMultipleRepairOrders(int count) {
    for (int i = 1; i <= count; i++) {
      CustomerEntity customer = createCustomer("Customer " + i, "customer" + i + "@example.com");
      VehicleEntity vehicle = createVehicle(customer, "Make " + i, "Model " + i, 2000 + i, "LP" + i);

      // Create different statuses for variety
      RepairOrderStatus status = switch (i % 3) {
        case 0 -> RepairOrderStatus.COMPLETED;
        case 1 -> RepairOrderStatus.PENDING;
        default -> RepairOrderStatus.IN_PROGRESS;
      };

      createRepairOrder(vehicle, "Repair order " + i, status);
    }
  }

}
