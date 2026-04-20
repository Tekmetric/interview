package com.interview.service;

import com.interview.dto.PageDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.repository.RepairOrderRepository;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

  private final RepairOrderRepository repairOrderRepository;
  private final RepairOrderMapper repairOrderMapper;

  public PageDto<RepairOrderSummaryDto> findAll(
      @PositiveOrZero int page,
      @Range(min = 1, max = 100) int size,
      @NotBlank String sort,
      @NotBlank String direction
  ) {
    log.debug("Finding all repair orders [page={}, size={}]", page, size);
    var dir = Sort.Direction.fromString(direction);
    var result = repairOrderRepository
        .findAll(PageRequest.of(page, size, Sort.by(dir, sort)))
        .map(repairOrderMapper::toSummaryDto);
    return new PageDto<>(
        result.getContent(),
        result.getNumber(),
        result.getSize(),
        result.getTotalElements(),
        result.getTotalPages(),
        result.isLast()
    );
  }

  public RepairOrderDetailDto findById(
          @NotNull UUID id
  ) {
    log.debug("Finding repair order [id={}]", id);
    var order = repairOrderRepository.findByIdWithLineItems(id)
        .orElseThrow(() -> new RepairOrderNotFoundException(id));
    return repairOrderMapper.toDetailDto(order);
  }
}
