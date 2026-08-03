package com.interview.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.PageDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.exception.StaleVersionException;
import com.interview.service.RepairOrderService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RepairOrderController.class)
@DisplayName("RepairOrderController")
class RepairOrderControllerComponentTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private RepairOrderService repairOrderService;

  @Nested
  @DisplayName("POST /api/v1/repair-orders")
  class Create {

    @Test
    @DisplayName("given valid command, when creating, "
        + "then returns 201 with detail and location header")
    void givenValidCommand_whenCreating_thenReturns201WithDetailAndLocationHeader()
        throws Exception {
      // Given
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderService.create(any(CreateRepairOrderCommand.class)))
          .willReturn(detailDto);

      // When / Then
      mockMvc.perform(post("/api/v1/repair-orders")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "description": "Oil change",
                    "vehicleMake": "Toyota",
                    "vehicleModel": "Camry",
                    "vehicleYear": 2021,
                    "licensePlate": "ABC-1234",
                    "customerId": "01966c3a-0000-7000-8000-000000000001",
                    "lineItems": [
                      {"description": "Oil filter", "unitPrice": 12.50}
                    ]
                  }
                  """))
          .andExpect(status().isCreated())
          .andExpect(header().exists("Location"))
          .andExpect(jsonPath("$.id").value(detailDto.id().toString()))
          .andExpect(jsonPath("$.lineItems").isArray());
    }

    @Test
    @DisplayName("given customer not found, when creating, then returns 404")
    void givenCustomerNotFound_whenCreating_thenReturns404() throws Exception {
      // Given
      var customerId = UUID.randomUUID();

      given(repairOrderService.create(any(CreateRepairOrderCommand.class)))
          .willThrow(new CustomerNotFoundException(customerId));

      // When / Then
      mockMvc.perform(post("/api/v1/repair-orders")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "description": "Oil change",
                    "vehicleMake": "Toyota",
                    "vehicleModel": "Camry",
                    "vehicleYear": 2021,
                    "customerId": "%s"
                  }
                  """.formatted(customerId)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.detail").value("Customer not found: " + customerId));
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/repair-orders/{id}")
  class Update {

    private static final String UPDATE_BODY = """
        {
          "description": "Updated description",
          "vehicleMake": "Honda",
          "vehicleModel": "Civic",
          "vehicleYear": 2022,
          "licensePlate": "XYZ-9999"
        }
        """;

    @Test
    @DisplayName("given matching version, when updating, then returns 200 with updated detail")
    void givenMatchingVersion_whenUpdating_thenReturns200WithUpdatedDetail() throws Exception {
      // Given
      var orderId = UUID.randomUUID();
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderService.update(eq(orderId), eq(0), any(UpdateRepairOrderCommand.class)))
          .willReturn(detailDto);

      // When / Then
      mockMvc.perform(put("/api/v1/repair-orders/{id}", orderId)
              .header("If-Match", "0")
              .contentType(MediaType.APPLICATION_JSON)
              .content(UPDATE_BODY))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(detailDto.id().toString()));
    }

    @Test
    @DisplayName("given version mismatch, when updating, then returns 412")
    void givenVersionMismatch_whenUpdating_thenReturns412() throws Exception {
      // Given
      var orderId = UUID.randomUUID();

      given(repairOrderService.update(eq(orderId), eq(0), any(UpdateRepairOrderCommand.class)))
          .willThrow(new StaleVersionException(orderId, 0, 1));

      // When / Then
      mockMvc.perform(put("/api/v1/repair-orders/{id}", orderId)
              .header("If-Match", "0")
              .contentType(MediaType.APPLICATION_JSON)
              .content(UPDATE_BODY))
          .andExpect(status().isPreconditionFailed())
          .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @DisplayName("given order does not exist, when updating, then returns 404")
    void givenOrderDoesNotExist_whenUpdating_thenReturns404() throws Exception {
      // Given
      var orderId = UUID.randomUUID();

      given(repairOrderService.update(eq(orderId), eq(0), any(UpdateRepairOrderCommand.class)))
          .willThrow(new RepairOrderNotFoundException(orderId));

      // When / Then
      mockMvc.perform(put("/api/v1/repair-orders/{id}", orderId)
              .header("If-Match", "0")
              .contentType(MediaType.APPLICATION_JSON)
              .content(UPDATE_BODY))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("given missing If-Match header, when updating, then returns 400")
    void givenMissingIfMatchHeader_whenUpdating_thenReturns400() throws Exception {
      // When / Then
      mockMvc.perform(put("/api/v1/repair-orders/{id}", UUID.randomUUID())
              .contentType(MediaType.APPLICATION_JSON)
              .content(UPDATE_BODY))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/repair-orders/{id}")
  class Delete {

    @Test
    @DisplayName("given order exists, when deleting, then returns 204")
    void givenOrderExists_whenDeleting_thenReturns204() throws Exception {
      // Given
      var orderId = UUID.randomUUID();

      willDoNothing().given(repairOrderService).delete(orderId);

      // When / Then
      mockMvc.perform(delete("/api/v1/repair-orders/{id}", orderId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("given invalid UUID, when deleting, then returns 400")
    void givenInvalidUuid_whenDeleting_thenReturns400() throws Exception {
      // When / Then
      mockMvc.perform(delete("/api/v1/repair-orders/{id}", "not-a-uuid"))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders")
  class GetAll {

    @Test
    @DisplayName("given default params, when getting all, then returns paged summaries")
    void givenDefaultParams_whenGettingAll_thenReturnsPagedSummaries() throws Exception {
      // Given
      var summary = Instancio.create(RepairOrderSummaryDto.class);
      var pageDto = new PageDto<>(List.of(summary), 0, 20, 1, 1, true);

      given(repairOrderService.findAll(0, 20, "createdAt", "desc"))
          .willReturn(pageDto);

      // When / Then
      mockMvc.perform(get("/api/v1/repair-orders"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isArray())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.page").value(0))
          .andExpect(jsonPath("$.size").value(20))
          .andExpect(jsonPath("$.totalElements").value(1))
          .andExpect(jsonPath("$.totalPages").value(1))
          .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("given custom params, when getting all, then passes them to service")
    void givenCustomParams_whenGettingAll_thenPassesThemToService() throws Exception {
      // Given
      var pageDto = new PageDto<RepairOrderSummaryDto>(
          List.of(), 2, 10, 0, 0, true);

      given(repairOrderService.findAll(2, 10, "status", "asc"))
          .willReturn(pageDto);

      // When / Then
      mockMvc.perform(get("/api/v1/repair-orders")
              .param("page", "2")
              .param("size", "10")
              .param("sort", "status")
              .param("direction", "asc"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.page").value(2))
          .andExpect(jsonPath("$.size").value(10));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/{id}")
  class GetById {

    @Test
    @DisplayName("given order exists, when getting by id, then returns detail with line items")
    void givenOrderExists_whenGettingById_thenReturnsDetailWithLineItems()
        throws Exception {
      // Given
      var orderId = UUID.randomUUID();
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderService.findById(orderId)).willReturn(detailDto);

      // When / Then
      mockMvc.perform(get("/api/v1/repair-orders/{id}", orderId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(detailDto.id().toString()))
          .andExpect(jsonPath("$.lineItems").isArray())
          .andExpect(jsonPath("$.customerId").exists())
          .andExpect(jsonPath("$.version").exists());
    }

    @Test
    @DisplayName("given order does not exist, when getting by id, then returns 404 with problem detail")
    void givenOrderDoesNotExist_whenGettingById_thenReturns404WithProblemDetail()
        throws Exception {
      // Given
      var orderId = UUID.randomUUID();

      given(repairOrderService.findById(orderId))
          .willThrow(new RepairOrderNotFoundException(orderId));

      // When / Then
      mockMvc.perform(get("/api/v1/repair-orders/{id}", orderId))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.detail")
              .value("Repair order not found: " + orderId));
    }

    @Test
    @DisplayName("given invalid UUID, when getting by id, then returns 400")
    void givenInvalidUuid_whenGettingById_thenReturns400() throws Exception {
      // When / Then
      mockMvc.perform(get("/api/v1/repair-orders/{id}", "not-a-uuid"))
          .andExpect(status().isBadRequest());
    }
  }
}
