package com.interview.service;

import com.interview.dto.CreateRepairOrderCommand;
import com.interview.dto.PageDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.dto.UpdateRepairOrderCommand;
import com.interview.exception.CustomerNotFoundException;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.exception.StaleVersionException;
import com.interview.repository.CustomerRepository;
import com.interview.repository.RepairOrderRepository;
import com.interview.model.RepairOrder_;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RepairOrderService {

  private static final Set<String> SORTABLE_FIELDS = Set.of(
      RepairOrder_.DESCRIPTION,
      RepairOrder_.STATUS,
      RepairOrder_.VEHICLE_MAKE,
      RepairOrder_.VEHICLE_MODEL,
      RepairOrder_.VEHICLE_YEAR,
      RepairOrder_.LICENSE_PLATE,
      RepairOrder_.CREATED_AT,
      RepairOrder_.UPDATED_AT
  );

  private final RepairOrderRepository repairOrderRepository;
  private final CustomerRepository customerRepository;
  private final RepairOrderMapper repairOrderMapper;
  private final LineItemMapper lineItemMapper;

  public PageDto<RepairOrderSummaryDto> findAll(
      @PositiveOrZero int page,
      @Range(min = 1, max = 100) int size,
      @NotBlank String sort,
      @NotBlank String direction
  ) {
    log.debug("Finding all repair orders [page={}, size={}]", page, size);

    if (!SORTABLE_FIELDS.contains(sort)) {
      throw new IllegalArgumentException(
          "Invalid sort field '%s'. Allowed values: %s".formatted(sort, SORTABLE_FIELDS));
    }

    var mappedDirection = Sort.Direction.fromString(direction);
    var result = repairOrderRepository
        .findAll(PageRequest.of(page, size, Sort.by(mappedDirection, sort)))
        .map(repairOrderMapper::toSummaryDto);
    var pageDto = new PageDto<>(
        result.getContent(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages(),
        result.isLast()
    );

    log.trace("Found repair orders [totalElements={}]", pageDto.totalElements());
    return pageDto;
  }

  @Transactional
  public RepairOrderDetailDto create(
      @NotNull @Valid CreateRepairOrderCommand command
  ) {
    log.debug("Creating repair order [customerId={}]", command.customerId());

    var customer = customerRepository.findById(command.customerId())
        .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));

    var order = repairOrderMapper.toEntity(command);
    order.setCustomer(customer);

    if (command.lineItems() != null) {
      var lineItems = command.lineItems().stream()
          .map(lineItemMapper::toEntity)
          .peek(lineItem -> lineItem.setRepairOrder(order))
          .toList();
      order.getLineItems().addAll(lineItems);
    }

    var saved = repairOrderRepository.save(order);
    var detail = repairOrderMapper.toDetailDto(saved);

    log.trace("Created repair order [id={}, lineItems={}]", detail.id(), detail.lineItems().size());
    return detail;
  }

  @Transactional
  public RepairOrderDetailDto update(
      @NotNull UUID id,
      int expectedVersion,
      @NotNull @Valid UpdateRepairOrderCommand command
  ) {
    log.debug("Updating repair order [id={}, expectedVersion={}]", id, expectedVersion);

    var order = repairOrderRepository.findByIdWithLineItems(id)
        .orElseThrow(() -> new RepairOrderNotFoundException(id));

    if (!order.getVersion().equals(expectedVersion)) {
      throw new StaleVersionException(id, expectedVersion, order.getVersion());
    }

    repairOrderMapper.updateEntity(command, order);
    var saved = repairOrderRepository.save(order);
    var detail = repairOrderMapper.toDetailDto(saved);

    log.trace("Updated repair order [id={}, version={}]", detail.id(), detail.version());
    return detail;
  }

  @Transactional
  public void delete(@NotNull UUID id) {
    log.debug("Deleting repair order [id={}]", id);

    repairOrderRepository.deleteById(id);

    log.trace("Deleted repair order [id={}]", id);
  }

  public RepairOrderDetailDto findById(
          @NotNull UUID id
  ) {
    log.debug("Finding repair order [id={}]", id);

    var order = repairOrderRepository.findByIdWithLineItems(id)
        .orElseThrow(() -> new RepairOrderNotFoundException(id));
    var detail = repairOrderMapper.toDetailDto(order);

    log.trace("Found repair order [id={}, lineItems={}]", id, detail.lineItems().size());

    return detail;
  }
}
