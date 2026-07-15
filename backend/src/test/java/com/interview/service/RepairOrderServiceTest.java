package com.interview.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.interview.dto.CreateLineItemCommand;
import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.InvalidStatusTransitionException;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.exception.StaleVersionException;
import com.interview.model.RepairOrderStatus;
import com.interview.model.Customer;
import com.interview.model.LineItem;
import com.interview.model.RepairOrder;
import com.interview.repository.CustomerRepository;
import com.interview.repository.RepairOrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.BDDSoftAssertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepairOrderService")
class RepairOrderServiceTest {

  @Mock
  private RepairOrderRepository repairOrderRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private RepairOrderMapper repairOrderMapper;

  @Mock
  private LineItemMapper lineItemMapper;

  @InjectMocks
  private RepairOrderService repairOrderService;

  @Nested
  @DisplayName("findAll")
  class FindAllPaged {

    @Test
    @DisplayName("given orders exist, when finding all, then returns paged summaries")
    void givenOrdersExist_whenFindingAll_thenReturnsPagedSummaries() {
      // Given
      var order = Instancio.create(RepairOrder.class);
      var summaryDto = Instancio.create(RepairOrderSummaryDto.class);
      var page = new PageImpl<>(List.of(order), PageRequest.of(0, 20), 1);

      given(repairOrderRepository.findAll(any(Pageable.class))).willReturn(page);
      given(repairOrderMapper.toSummaryDto(order)).willReturn(summaryDto);

      // When
      var result = repairOrderService.findAll(0, 20, "createdAt", "desc");

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.content()).hasSize(1);
        softly.then(result.content().getFirst()).isEqualTo(summaryDto);
        softly.then(result.page()).isZero();
        softly.then(result.size()).isEqualTo(20);
        softly.then(result.totalElements()).isEqualTo(1);
        softly.then(result.totalPages()).isEqualTo(1);
        softly.then(result.last()).isTrue();
      });

      org.mockito.BDDMockito.then(repairOrderRepository).should().findAll(
          PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"))
      );
    }

    @Test
    @DisplayName("given no orders exist, when finding all, then returns empty content")
    void givenNoOrdersExist_whenFindingAll_thenReturnsEmptyContent() {
      // Given
      var page = new PageImpl<RepairOrder>(
          List.of(), PageRequest.of(0, 20), 0);

      given(repairOrderRepository.findAll(any(Pageable.class))).willReturn(page);

      // When
      var result = repairOrderService.findAll(0, 20, "createdAt", "desc");

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.content()).isEmpty();
        softly.then(result.totalElements()).isZero();
      });
    }
  }

  @Nested
  @DisplayName("create")
  class Create {

    @Test
    @DisplayName("given valid command with line items, when creating, "
        + "then saves order with customer and line items")
    void givenValidCommandWithLineItems_whenCreating_thenSavesOrderWithCustomerAndLineItems() {
      // Given
      var customerId = UUID.randomUUID();
      var command = new CreateRepairOrderCommand(
          "Oil change", "Toyota", "Camry", 2021, "ABC-1234", customerId,
          List.of(new CreateLineItemCommand("Oil filter", BigDecimal.valueOf(12.50))));
      var customer = Instancio.of(Customer.class)
          .set(field(Customer::getId), customerId)
          .create();
      var order = Instancio.create(RepairOrder.class);
      var lineItem = Instancio.create(LineItem.class);
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
      given(repairOrderMapper.toEntity(command)).willReturn(order);
      given(lineItemMapper.toEntity(command.lineItems().getFirst())).willReturn(lineItem);
      given(repairOrderRepository.save(order)).willReturn(order);
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.create(command);

      // Then
      then(result).isEqualTo(detailDto);
      org.mockito.BDDMockito.then(customerRepository).should().findById(customerId);
      org.mockito.BDDMockito.then(repairOrderRepository).should().save(order);
    }

    @Test
    @DisplayName("given valid command without line items, when creating, "
        + "then saves order without line items")
    void givenValidCommandWithoutLineItems_whenCreating_thenSavesOrderWithoutLineItems() {
      // Given
      var customerId = UUID.randomUUID();
      var command = new CreateRepairOrderCommand(
          "Oil change", "Toyota", "Camry", 2021, "ABC-1234", customerId, null);
      var customer = Instancio.create(Customer.class);
      var order = Instancio.create(RepairOrder.class);
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));
      given(repairOrderMapper.toEntity(command)).willReturn(order);
      given(repairOrderRepository.save(order)).willReturn(order);
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.create(command);

      // Then
      then(result).isEqualTo(detailDto);
      org.mockito.BDDMockito.then(lineItemMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("given non-existing customer, when creating, "
        + "then throws customer not found exception")
    void givenNonExistingCustomer_whenCreating_thenThrowsCustomerNotFoundException() {
      // Given
      var customerId = UUID.randomUUID();
      var command = new CreateRepairOrderCommand(
          "Oil change", "Toyota", "Camry", 2021, "ABC-1234", customerId, null);

      given(customerRepository.findById(customerId)).willReturn(Optional.empty());

      // When / Then
      thenThrownBy(() -> repairOrderService.create(command))
          .isInstanceOf(CustomerNotFoundException.class)
          .hasMessageContaining(customerId.toString());
    }
  }

  @Nested
  @DisplayName("update")
  class Update {

    @Test
    @DisplayName("given existing order, when updating, "
        + "then sets version and returns updated detail")
    void givenExistingOrder_whenUpdating_thenSetsVersionAndReturnsUpdatedDetail() {
      // Given
      var orderId = UUID.randomUUID();
      var expectedVersion = 5;
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), expectedVersion)
          .create();
      var command = new UpdateRepairOrderCommand(
          "Updated description",
          "Honda", "Civic", 2022, "XYZ-9999");
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));
      given(repairOrderRepository.save(order)).willReturn(order);
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.update(orderId, expectedVersion, command);

      // Then
      then(result).isEqualTo(detailDto);
      org.mockito.BDDMockito.then(repairOrderMapper).should()
          .updateEntity(command, order);
    }

    @Test
    @DisplayName("given version mismatch, when updating, "
        + "then throws stale version exception")
    void givenVersionMismatch_whenUpdating_thenThrowsStaleVersionException() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), 3)
          .create();
      var command = new UpdateRepairOrderCommand(
          "Updated",
          "Honda", "Civic", 2022, null);

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));

      // When / Then
      thenThrownBy(() -> repairOrderService.update(orderId, 1, command))
          .isInstanceOf(StaleVersionException.class)
          .hasMessageContaining("expected version 1")
          .hasMessageContaining("found 3");
    }

    @Test
    @DisplayName("given order does not exist, when updating, "
        + "then throws not found exception")
    void givenOrderDoesNotExist_whenUpdating_thenThrowsNotFoundException() {
      // Given
      var orderId = UUID.randomUUID();
      var command = new UpdateRepairOrderCommand(
          "Updated",
          "Honda", "Civic", 2022, null);

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.empty());

      // When / Then
      thenThrownBy(() -> repairOrderService.update(orderId, 0, command))
          .isInstanceOf(RepairOrderNotFoundException.class)
          .hasMessageContaining(orderId.toString());
    }
  }

  @Nested
  @DisplayName("delete")
  class Delete {

    @Test
    @DisplayName("given order exists, when deleting, then deletes the order")
    void givenOrderExists_whenDeleting_thenDeletesTheOrder() {
      // Given
      var orderId = UUID.randomUUID();

      // When
      repairOrderService.delete(orderId);

      // Then
      org.mockito.BDDMockito.then(repairOrderRepository).should().deleteById(orderId);
    }
  }

  @Nested
  @DisplayName("findById")
  class FindById {

    @Test
    @DisplayName("given order exists, when finding by id, "
        + "then returns detail with line items")
    void givenOrderExists_whenFindingById_thenReturnsDetailWithLineItems() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .create();
      var detailDto = Instancio.of(RepairOrderDetailDto.class)
          .set(field(RepairOrderDetailDto::id), orderId)
          .create();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.findById(orderId);

      // Then
      BDDSoftAssertions.thenSoftly(softly -> {
        softly.then(result.id()).isEqualTo(orderId);
        softly.then(result.lineItems()).isNotNull();
      });
      org.mockito.BDDMockito.then(repairOrderRepository).should()
          .findByIdWithLineItems(orderId);
    }

    @Test
    @DisplayName("given order does not exist, when finding by id, "
        + "then throws not found exception")
    void givenOrderDoesNotExist_whenFindingById_thenThrowsNotFoundException() {
      // Given
      var orderId = UUID.randomUUID();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.empty());

      // When / Then
      thenThrownBy(() -> repairOrderService.findById(orderId))
          .isInstanceOf(RepairOrderNotFoundException.class)
          .hasMessageContaining(orderId.toString());
    }
  }

  @Nested
  @DisplayName("start")
  class Start {

    @Test
    @DisplayName("given pending order, when starting, then transitions to in progress")
    void givenPendingOrder_whenStarting_thenTransitionsToInProgress() {
      // Given
      var orderId = UUID.randomUUID();
      var expectedVersion = 0;
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), expectedVersion)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.PENDING)
          .create();
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));
      given(repairOrderRepository.save(order)).willReturn(order);
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.start(orderId, expectedVersion);

      // Then
      then(result).isEqualTo(detailDto);
      then(order.getStatus()).isEqualTo(RepairOrderStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("given non-pending order, when starting, then throws invalid transition")
    void givenNonPendingOrder_whenStarting_thenThrowsInvalidTransition() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), 0)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.COMPLETED)
          .create();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));

      // When / Then
      thenThrownBy(() -> repairOrderService.start(orderId, 0))
          .isInstanceOf(InvalidStatusTransitionException.class)
          .hasMessageContaining("COMPLETED")
          .hasMessageContaining("IN_PROGRESS");
    }

    @Test
    @DisplayName("given version mismatch, when starting, then throws stale version")
    void givenVersionMismatch_whenStarting_thenThrowsStaleVersion() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), 5)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.PENDING)
          .create();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));

      // When / Then
      thenThrownBy(() -> repairOrderService.start(orderId, 0))
          .isInstanceOf(StaleVersionException.class);
    }
  }

  @Nested
  @DisplayName("close")
  class Close {

    @Test
    @DisplayName("given in-progress order, when closing, then transitions to completed")
    void givenInProgressOrder_whenClosing_thenTransitionsToCompleted() {
      // Given
      var orderId = UUID.randomUUID();
      var expectedVersion = 1;
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), expectedVersion)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.IN_PROGRESS)
          .create();
      var detailDto = Instancio.create(RepairOrderDetailDto.class);

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));
      given(repairOrderRepository.save(order)).willReturn(order);
      given(repairOrderMapper.toDetailDto(order)).willReturn(detailDto);

      // When
      var result = repairOrderService.close(orderId, expectedVersion);

      // Then
      then(result).isEqualTo(detailDto);
      then(order.getStatus()).isEqualTo(RepairOrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("given non-in-progress order, when closing, then throws invalid transition")
    void givenNonInProgressOrder_whenClosing_thenThrowsInvalidTransition() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), 0)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.PENDING)
          .create();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));

      // When / Then
      thenThrownBy(() -> repairOrderService.close(orderId, 0))
          .isInstanceOf(InvalidStatusTransitionException.class)
          .hasMessageContaining("PENDING")
          .hasMessageContaining("COMPLETED");
    }

    @Test
    @DisplayName("given version mismatch, when closing, then throws stale version")
    void givenVersionMismatch_whenClosing_thenThrowsStaleVersion() {
      // Given
      var orderId = UUID.randomUUID();
      var order = Instancio.of(RepairOrder.class)
          .set(field(RepairOrder::getId), orderId)
          .set(field(RepairOrder::getVersion), 3)
          .set(field(RepairOrder::getStatus), RepairOrderStatus.IN_PROGRESS)
          .create();

      given(repairOrderRepository.findByIdWithLineItems(orderId))
          .willReturn(Optional.of(order));

      // When / Then
      thenThrownBy(() -> repairOrderService.close(orderId, 0))
          .isInstanceOf(StaleVersionException.class);
    }
  }
}
