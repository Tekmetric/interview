package com.interview.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.instancio.Select.field;

import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.model.Customer;
import com.interview.model.LineItem;
import com.interview.model.RepairOrder;
import com.interview.model.RepairOrderStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.BDDSoftAssertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

@DisplayName("RepairOrderMapper")
class RepairOrderMapperTest {

  private final LineItemMapper lineItemMapper = Mappers.getMapper(LineItemMapper.class);
  private final RepairOrderMapper mapper = new RepairOrderMapperImpl(lineItemMapper);

  @Nested
  @DisplayName("toSummaryDto")
  class ToSummaryDto {

    @Test
    @DisplayName("given order with customer, when mapping to summary, "
        + "then maps all fields including customer id")
    void givenOrderWithCustomer_whenMappingToSummary_thenMapsAllFields() {
      // Given
      var customer = Instancio.create(Customer.class);
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getCustomer), customer)
          .create();

      // When
      var result = mapper.toSummaryDto(order);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.id()).isEqualTo(order.getId());
        softly.then(result.description()).isEqualTo(order.getDescription());
        softly.then(result.status()).isEqualTo(order.getStatus());
        softly.then(result.vehicleMake()).isEqualTo(order.getVehicleMake());
        softly.then(result.vehicleModel()).isEqualTo(order.getVehicleModel());
        softly.then(result.vehicleYear()).isEqualTo(order.getVehicleYear());
        softly.then(result.licensePlate()).isEqualTo(order.getLicensePlate());
        softly.then(result.customerId()).isEqualTo(customer.getId());
        softly.then(result.version()).isEqualTo(order.getVersion());
        softly.then(result.createdAt()).isEqualTo(order.getCreatedAt());
        softly.then(result.updatedAt()).isEqualTo(order.getUpdatedAt());
      });
    }

    @Test
    @DisplayName("given order with null customer, when mapping to summary, "
        + "then maps customer id as null")
    void givenOrderWithNullCustomer_whenMappingToSummary_thenMapsCustomerIdAsNull() {
      // Given
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getCustomer), null)
          .create();

      // When
      var result = mapper.toSummaryDto(order);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.customerId()).isNull();
        softly.then(result.id()).isEqualTo(order.getId());
      });
    }

    @Test
    @DisplayName("given null order, when mapping to summary, then returns null")
    void givenNullOrder_whenMappingToSummary_thenReturnsNull() {
      // When
      var result = mapper.toSummaryDto(null);

      // Then
      then(result).isNull();
    }
  }

  @Nested
  @DisplayName("toDetailDto")
  class ToDetailDto {

    @Test
    @DisplayName("given order with line items, when mapping to detail, "
        + "then maps all fields including line items")
    void givenOrderWithLineItems_whenMappingToDetail_thenMapsAllFields() {
      // Given
      var customer = Instancio.create(Customer.class);
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getCustomer), customer)
          .create();

      // When
      var result = mapper.toDetailDto(order);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.id()).isEqualTo(order.getId());
        softly.then(result.description()).isEqualTo(order.getDescription());
        softly.then(result.status()).isEqualTo(order.getStatus());
        softly.then(result.customerId()).isEqualTo(customer.getId());
        softly.then(result.lineItems()).hasSameSizeAs(order.getLineItems());
        softly.then(result.version()).isEqualTo(order.getVersion());
        softly.then(result.createdAt()).isEqualTo(order.getCreatedAt());
        softly.then(result.updatedAt()).isEqualTo(order.getUpdatedAt());
      });
    }

    @Test
    @DisplayName("given order with empty line items, when mapping to detail, "
        + "then returns empty list")
    void givenOrderWithEmptyLineItems_whenMappingToDetail_thenReturnsEmptyList() {
      // Given
      var customer = Instancio.create(Customer.class);
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getCustomer), customer)
          .set(field(RepairOrder::getLineItems), new ArrayList<LineItem>())
          .create();

      // When
      var result = mapper.toDetailDto(order);

      // Then
      then(result.lineItems()).isEmpty();
    }

    @Test
    @DisplayName("given order with null customer, when mapping to detail, "
        + "then maps customer id as null")
    void givenOrderWithNullCustomer_whenMappingToDetail_thenMapsCustomerIdAsNull() {
      // Given
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getCustomer), null)
          .create();

      // When
      var result = mapper.toDetailDto(order);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.customerId()).isNull();
        softly.then(result.id()).isEqualTo(order.getId());
      });
    }

    @Test
    @DisplayName("given null order, when mapping to detail, then returns null")
    void givenNullOrder_whenMappingToDetail_thenReturnsNull() {
      // When
      var result = mapper.toDetailDto(null);

      // Then
      then(result).isNull();
    }
  }

  @Nested
  @DisplayName("toEntity")
  class ToEntity {

    @Test
    @DisplayName("given command, when mapping to entity, then maps fields and ignores managed fields")
    void givenCommand_whenMappingToEntity_thenMapsFieldsAndIgnoresManagedFields() {
      // Given
      var command = new CreateRepairOrderCommand(
          "Oil change", "Toyota", "Camry", 2021, "ABC-1234",
          UUID.randomUUID(), List.of());

      // When
      var result = mapper.toEntity(command);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getDescription()).isEqualTo("Oil change");
        softly.then(result.getVehicleMake()).isEqualTo("Toyota");
        softly.then(result.getVehicleModel()).isEqualTo("Camry");
        softly.then(result.getVehicleYear()).isEqualTo(2021);
        softly.then(result.getLicensePlate()).isEqualTo("ABC-1234");
        softly.then(result.getId()).isNull();
        softly.then(result.getStatus()).isEqualTo(RepairOrderStatus.PENDING);
        softly.then(result.getCustomer()).isNull();
        softly.then(result.getVersion()).isNull();
        softly.then(result.getCreatedAt()).isNull();
        softly.then(result.getUpdatedAt()).isNull();
      });
    }

    @Test
    @DisplayName("given null command, when mapping to entity, then returns null")
    void givenNullCommand_whenMappingToEntity_thenReturnsNull() {
      // When
      var result = mapper.toEntity(null);

      // Then
      then(result).isNull();
    }
  }

  @Nested
  @DisplayName("updateEntity")
  class UpdateEntity {

    @Test
    @DisplayName("given command, when updating entity, then updates fields and preserves managed fields")
    void givenCommand_whenUpdatingEntity_thenUpdatesFieldsAndPreservesManagedFields() {
      // Given
      var order = Instancio.create(RepairOrder.class);
      var originalId = order.getId();
      var originalCustomer = order.getCustomer();
      var originalVersion = order.getVersion();

      var originalStatus = order.getStatus();

      var command = new UpdateRepairOrderCommand(
          "Updated description",
          "Honda", "Civic", 2022, "NEW-PLATE");

      // When
      mapper.updateEntity(command, order);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(order.getDescription()).isEqualTo("Updated description");
        softly.then(order.getStatus()).isEqualTo(originalStatus);
        softly.then(order.getVehicleMake()).isEqualTo("Honda");
        softly.then(order.getVehicleModel()).isEqualTo("Civic");
        softly.then(order.getVehicleYear()).isEqualTo(2022);
        softly.then(order.getLicensePlate()).isEqualTo("NEW-PLATE");
        softly.then(order.getId()).isEqualTo(originalId);
        softly.then(order.getCustomer()).isEqualTo(originalCustomer);
        softly.then(order.getVersion()).isEqualTo(originalVersion);
      });
    }
  }
}
