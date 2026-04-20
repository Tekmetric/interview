package com.interview.integration;

import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureRestTestClient
@Transactional
@DisplayName("RepairOrder IT")
class RepairOrderIT {

  @Autowired
  private RestTestClient restTestClient;

  private static final String CUSTOMER_ID =
      "01966c3a-0000-7000-8000-000000000001";

  @Nested
  @DisplayName("POST /api/v1/repair-orders")
  class Create {

    @Test
    @DisplayName("given valid command with line items, when creating, "
        + "then returns 201 with detail and location header")
    void givenValidCommand_whenCreating_thenReturns201WithDetailAndLocationHeader() {
      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Windshield replacement",
                "vehicleMake": "Honda",
                "vehicleModel": "Accord",
                "vehicleYear": 2022,
                "licensePlate": "NEW-0001",
                "customerId": "%s",
                "lineItems": [
                  {"description": "Windshield glass", "unitPrice": 350.00},
                  {"description": "Installation labor", "unitPrice": 150.00}
                ]
              }
              """.formatted(CUSTOMER_ID))
          .exchange()
          .expectStatus().isCreated()
          .expectHeader().exists("Location")
          .expectBody(RepairOrderDetailDto.class)
          .value(body -> {
            org.assertj.core.api.BDDSoftAssertions.thenSoftly(softly -> {
              softly.then(body.id()).isNotNull();
              softly.then(body.description()).isEqualTo("Windshield replacement");
              softly.then(body.status().name()).isEqualTo("PENDING");
              softly.then(body.vehicleMake()).isEqualTo("Honda");
              softly.then(body.vehicleModel()).isEqualTo("Accord");
              softly.then(body.vehicleYear()).isEqualTo(2022);
              softly.then(body.customerId())
                  .isEqualTo(UUID.fromString(CUSTOMER_ID));
              softly.then(body.lineItems()).hasSize(2);
            });
          });
    }

    @Test
    @DisplayName("given valid command without line items, when creating, "
        + "then returns 201 with empty line items")
    void givenValidCommandWithoutLineItems_whenCreating_thenReturns201WithEmptyLineItems() {
      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Quick inspection",
                "vehicleMake": "Ford",
                "vehicleModel": "Focus",
                "vehicleYear": 2020,
                "customerId": "%s"
              }
              """.formatted(CUSTOMER_ID))
          .exchange()
          .expectStatus().isCreated()
          .expectBody(RepairOrderDetailDto.class)
          .value(body -> {
            org.assertj.core.api.BDDSoftAssertions.thenSoftly(softly -> {
              softly.then(body.id()).isNotNull();
              softly.then(body.description()).isEqualTo("Quick inspection");
              softly.then(body.lineItems()).isEmpty();
            });
          });
    }

    @Test
    @DisplayName("given non-existing customer, when creating, then returns 404")
    void givenNonExistingCustomer_whenCreating_thenReturns404() {
      // Given
      var nonExistingCustomerId = UUID.randomUUID();

      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Oil change",
                "vehicleMake": "Toyota",
                "vehicleModel": "Camry",
                "vehicleYear": 2021,
                "customerId": "%s"
              }
              """.formatted(nonExistingCustomerId))
          .exchange()
          .expectStatus().isNotFound()
          .expectBody()
          .jsonPath("$.detail").value(detail ->
              org.assertj.core.api.BDDAssertions.then((String) detail)
                  .contains(nonExistingCustomerId.toString()));
    }

    @Test
    @DisplayName("given missing required fields, when creating, then returns 400")
    void givenMissingRequiredFields_whenCreating_thenReturns400() {
      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("{}")
          .exchange()
          .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("given invalid vehicle year, when creating, then returns 400")
    void givenInvalidVehicleYear_whenCreating_thenReturns400() {
      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Oil change",
                "vehicleMake": "Toyota",
                "vehicleModel": "Camry",
                "vehicleYear": -1,
                "customerId": "%s"
              }
              """.formatted(CUSTOMER_ID))
          .exchange()
          .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("given invalid line item price, when creating, then returns 400")
    void givenInvalidLineItemPrice_whenCreating_thenReturns400() {
      // When / Then
      restTestClient.post().uri("/api/v1/repair-orders")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Oil change",
                "vehicleMake": "Toyota",
                "vehicleModel": "Camry",
                "vehicleYear": 2021,
                "customerId": "%s",
                "lineItems": [
                  {"description": "Oil filter", "unitPrice": -5.00}
                ]
              }
              """.formatted(CUSTOMER_ID))
          .exchange()
          .expectStatus().isBadRequest();
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/repair-orders/{id}")
  class Update {

    private static final String ORDER_TO_UPDATE =
        "01966c3a-1000-7000-8000-000000000003";

    @Test
    @DisplayName("given matching version, when updating, then returns 200 with updated fields")
    void givenMatchingVersion_whenUpdating_thenReturns200WithUpdatedFields() {
      // When / Then
      restTestClient.put().uri("/api/v1/repair-orders/{id}", ORDER_TO_UPDATE)
          .header("If-Match", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Full engine overhaul",
                "status": "IN_PROGRESS",
                "vehicleMake": "Ford",
                "vehicleModel": "F-150",
                "vehicleYear": 2023,
                "licensePlate": "UPD-0001"
              }
              """)
          .exchange()
          .expectStatus().isOk()
          .expectBody(RepairOrderDetailDto.class)
          .value(body -> {
            org.assertj.core.api.BDDSoftAssertions.thenSoftly(softly -> {
              softly.then(body.id())
                  .isEqualTo(UUID.fromString(ORDER_TO_UPDATE));
              softly.then(body.description()).isEqualTo("Full engine overhaul");
              softly.then(body.status().name()).isEqualTo("IN_PROGRESS");
              softly.then(body.licensePlate()).isEqualTo("UPD-0001");
              softly.then(body.version()).isNotNull();
            });
          });
    }

    @Test
    @DisplayName("given non-existing order, when updating, then returns 404")
    void givenNonExistingOrder_whenUpdating_thenReturns404() {
      // Given
      var nonExistingId = UUID.randomUUID();

      // When / Then
      restTestClient.put().uri("/api/v1/repair-orders/{id}", nonExistingId)
          .header("If-Match", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .body("""
              {
                "description": "Should not exist",
                "status": "PENDING",
                "vehicleMake": "Toyota",
                "vehicleModel": "Camry",
                "vehicleYear": 2021
              }
              """)
          .exchange()
          .expectStatus().isNotFound();
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/repair-orders/{id}")
  class Delete {

    private static final String ORDER_TO_DELETE =
        "01966c3a-1000-7000-8000-000000000004";

    @Test
    @DisplayName("given existing order, when deleting, then returns 204 and order is gone")
    void givenExistingOrder_whenDeleting_thenReturns204AndOrderIsGone() {
      // When
      restTestClient.delete().uri("/api/v1/repair-orders/{id}", ORDER_TO_DELETE)
          .exchange()
          .expectStatus().isNoContent();

      // Then — verify it's gone
      restTestClient.get().uri("/api/v1/repair-orders/{id}", ORDER_TO_DELETE)
          .exchange()
          .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("given non-existing order, when deleting, then returns 204")
    void givenNonExistingOrder_whenDeleting_thenReturns204() {
      // Given
      var nonExistingId = UUID.randomUUID();

      // When / Then
      restTestClient.delete().uri("/api/v1/repair-orders/{id}", nonExistingId)
          .exchange()
          .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("given invalid UUID, when deleting, then returns 400")
    void givenInvalidUuid_whenDeleting_thenReturns400() {
      // When / Then
      restTestClient.delete().uri("/api/v1/repair-orders/{id}", "not-a-uuid")
          .exchange()
          .expectStatus().isBadRequest();
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders")
  class GetAll {

    @Test
    @DisplayName("given seed data, when getting all with defaults, "
        + "then returns paged repair orders")
    void givenSeedData_whenGettingAllWithDefaults_thenReturnsPagedOrders() {
      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders")
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.content").isArray()
          .jsonPath("$.content.length()").isEqualTo(4)
          .jsonPath("$.page").isEqualTo(0)
          .jsonPath("$.size").isEqualTo(20)
          .jsonPath("$.totalElements").isEqualTo(4)
          .jsonPath("$.totalPages").isEqualTo(1)
          .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    @DisplayName("given seed data, when getting all with custom pagination, "
        + "then returns requested page")
    void givenSeedData_whenGettingAllWithCustomPagination_thenReturnsRequestedPage() {
      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders?page=0&size=2"
              + "&sort=createdAt&direction=asc")
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.content.length()").isEqualTo(2)
          .jsonPath("$.page").isEqualTo(0)
          .jsonPath("$.size").isEqualTo(2)
          .jsonPath("$.totalElements").isEqualTo(4)
          .jsonPath("$.totalPages").isEqualTo(2)
          .jsonPath("$.last").isEqualTo(false);
    }

    @Test
    @DisplayName("given seed data, when getting all summaries, "
        + "then each summary has required fields")
    void givenSeedData_whenGettingAllSummaries_thenEachHasRequiredFields() {
      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders")
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.content[0].id").isNotEmpty()
          .jsonPath("$.content[0].description").isNotEmpty()
          .jsonPath("$.content[0].status").isNotEmpty()
          .jsonPath("$.content[0].customerId").isNotEmpty()
          .jsonPath("$.content[0].vehicleMake").isNotEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/v1/repair-orders/{id}")
  class GetById {

    private static final String ORDER_WITH_LINE_ITEMS =
        "01966c3a-1000-7000-8000-000000000001";

    @Test
    @DisplayName("given existing order, when getting by id, "
        + "then returns detail with line items")
    void givenExistingOrder_whenGettingById_thenReturnsDetailWithLineItems() {
      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders/{id}", ORDER_WITH_LINE_ITEMS)
          .exchange()
          .expectStatus().isOk()
          .expectBody(RepairOrderDetailDto.class)
          .value(body -> {
            org.assertj.core.api.BDDSoftAssertions.thenSoftly(softly -> {
              softly.then(body.id())
                  .isEqualTo(UUID.fromString(ORDER_WITH_LINE_ITEMS));
              softly.then(body.description())
                  .isEqualTo("Oil change and tire rotation");
              softly.then(body.status().name()).isEqualTo("COMPLETED");
              softly.then(body.vehicleMake()).isEqualTo("Toyota");
              softly.then(body.lineItems()).hasSize(3);
              softly.then(body.customerId())
                  .isEqualTo(UUID.fromString(CUSTOMER_ID));
              softly.then(body.version()).isNotNull();
              softly.then(body.createdAt()).isNotNull();
            });
          });
    }

    @Test
    @DisplayName("given non-existing order, when getting by id, "
        + "then returns 404 with problem detail")
    void givenNonExistingOrder_whenGettingById_thenReturns404() {
      // Given
      var nonExistingId = UUID.randomUUID();

      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders/{id}", nonExistingId)
          .exchange()
          .expectStatus().isNotFound()
          .expectBody()
          .jsonPath("$.detail").value(detail ->
              org.assertj.core.api.BDDAssertions.then((String) detail)
                  .contains(nonExistingId.toString()));
    }

    @Test
    @DisplayName("given invalid UUID, when getting by id, then returns 400")
    void givenInvalidUuid_whenGettingById_thenReturns400() {
      // When / Then
      restTestClient.get().uri("/api/v1/repair-orders/{id}", "not-a-uuid")
          .exchange()
          .expectStatus().isBadRequest();
    }
  }
}
