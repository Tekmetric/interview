package com.interview.repository;

import static org.assertj.core.api.BDDAssertions.then;

import com.interview.model.Customer;
import com.interview.model.LineItem;
import com.interview.model.RepairOrder;
import com.interview.model.RepairOrderStatus;
import java.math.BigDecimal;
import org.assertj.core.api.BDDSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@DisplayName("RepairOrderRepository")
class RepairOrderRepositoryComponentTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private RepairOrderRepository repairOrderRepository;

  private Customer customer;

  @BeforeEach
  void setUp() {
    em.getEntityManager()
        .createQuery("DELETE FROM LineItem").executeUpdate();
    em.getEntityManager()
        .createQuery("DELETE FROM RepairOrder").executeUpdate();
    em.getEntityManager()
        .createQuery("DELETE FROM Customer").executeUpdate();
    em.flush();

    customer = em.persistAndFlush(Customer.builder()
        .name("John Doe")
        .email("john@example.com")
        .phone("555-1234")
        .build());
  }

  @Nested
  @DisplayName("findByIdWithLineItems")
  class FindByIdWithLineItems {

    @Test
    @DisplayName("given order with line items, when finding by id, "
        + "then returns order with line items eagerly loaded")
    void givenOrderWithLineItems_whenFindingById_thenReturnsWithLineItemsLoaded() {
      // Given
      var order = persistOrder(RepairOrderStatus.PENDING);
      persistLineItem(order, "Oil change", new BigDecimal("49.99"));
      persistLineItem(order, "Brake pads", new BigDecimal("129.99"));
      em.clear();

      // When
      var result = repairOrderRepository.findByIdWithLineItems(order.getId());

      // Then
      then(result).isPresent();
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.get().getLineItems()).hasSize(2);
        softly.then(result.get().getDescription()).isEqualTo("Order PENDING");
        softly.then(result.get().getCustomer().getId())
            .isEqualTo(customer.getId());
      });
    }

    @Test
    @DisplayName("given order without line items, when finding by id, "
        + "then returns order with empty list")
    void givenOrderWithoutLineItems_whenFindingById_thenReturnsWithEmptyList() {
      // Given
      var order = persistOrder(RepairOrderStatus.IN_PROGRESS);
      em.clear();

      // When
      var result = repairOrderRepository.findByIdWithLineItems(order.getId());

      // Then
      then(result).isPresent();
      then(result.get().getLineItems()).isEmpty();
    }

    @Test
    @DisplayName("given non-existing id, when finding by id, then returns empty")
    void givenNonExistingId_whenFindingById_thenReturnsEmpty() {
      // Given
      var nonExistingId = java.util.UUID.randomUUID();

      // When
      var result = repairOrderRepository.findByIdWithLineItems(nonExistingId);

      // Then
      then(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("findByCustomerId")
  class FindByCustomerId {

    @Test
    @DisplayName("given orders for customer, when finding by customer id, "
        + "then returns only that customer's orders")
    void givenOrdersForCustomer_whenFindingByCustomerId_thenReturnsOnlyTheirOrders() {
      // Given
      var otherCustomer = em.persistAndFlush(Customer.builder()
          .name("Jane Doe")
          .email("jane@example.com")
          .build());

      persistOrder(RepairOrderStatus.PENDING);
      persistOrder(RepairOrderStatus.COMPLETED);

      var otherOrder = RepairOrder.builder()
          .description("Other order")
          .status(RepairOrderStatus.IN_PROGRESS)
          .vehicleMake("Honda")
          .vehicleModel("Civic")
          .vehicleYear(2024)
          .customer(otherCustomer)
          .build();
      em.persistAndFlush(otherOrder);

      // When
      var result = repairOrderRepository.findByCustomerId(
          customer.getId(), PageRequest.of(0, 10));

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getContent()).hasSize(2);
        softly.then(result.getTotalElements()).isEqualTo(2);
      });
    }

    @Test
    @DisplayName("given no orders for customer, when finding by customer id, "
        + "then returns empty page")
    void givenNoOrdersForCustomer_whenFindingByCustomerId_thenReturnsEmptyPage() {
      // Given
      var otherCustomer = em.persistAndFlush(Customer.builder()
          .name("No Orders")
          .email("noorders@example.com")
          .build());

      // When
      var result = repairOrderRepository.findByCustomerId(
          otherCustomer.getId(), PageRequest.of(0, 10));

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getContent()).isEmpty();
        softly.then(result.getTotalElements()).isZero();
      });
    }
  }

  @Nested
  @DisplayName("findByStatus")
  class FindByStatus {

    @Test
    @DisplayName("given orders with mixed statuses, when finding by status, "
        + "then returns only matching orders")
    void givenMixedStatuses_whenFindingByStatus_thenReturnsOnlyMatching() {
      // Given
      persistOrder(RepairOrderStatus.PENDING);
      persistOrder(RepairOrderStatus.PENDING);
      persistOrder(RepairOrderStatus.COMPLETED);

      // When
      var result = repairOrderRepository.findByStatus(
          RepairOrderStatus.PENDING, PageRequest.of(0, 10));

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getContent()).hasSize(2);
        softly.then(result.getTotalElements()).isEqualTo(2);
      });
    }
  }

  @Nested
  @DisplayName("findAll")
  class FindAll {

    @Test
    @DisplayName("given multiple orders, when finding all sorted, "
        + "then returns paged and sorted results")
    void givenMultipleOrders_whenFindingAllSorted_thenReturnsPagedAndSorted() {
      // Given
      persistOrder(RepairOrderStatus.PENDING);
      persistOrder(RepairOrderStatus.IN_PROGRESS);
      persistOrder(RepairOrderStatus.COMPLETED);

      // When
      var result = repairOrderRepository.findAll(
          PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "status")));

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.getContent()).hasSize(2);
        softly.then(result.getTotalElements()).isEqualTo(3);
        softly.then(result.getTotalPages()).isEqualTo(2);
      });
    }
  }

  private RepairOrder persistOrder(RepairOrderStatus status) {
    var order = RepairOrder.builder()
        .description("Order " + status)
        .status(status)
        .vehicleMake("Toyota")
        .vehicleModel("Camry")
        .vehicleYear(2023)
        .licensePlate("ABC-1234")
        .customer(customer)
        .build();
    return em.persistAndFlush(order);
  }

  private LineItem persistLineItem(RepairOrder order, String description,
      BigDecimal price) {
    var item = LineItem.builder()
        .description(description)
        .unitPrice(price)
        .repairOrder(order)
        .build();
    return em.persistAndFlush(item);
  }
}
