package com.interview.controller;

import com.interview.dto.PageDto;
import com.interview.dto.RepairOrderDetailDto;
import com.interview.dto.RepairOrderSummaryDto;
import com.interview.exception.RepairOrderNotFoundException;
import com.interview.service.RepairOrderService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController {

  private final RepairOrderService repairOrderService;

  @GetMapping
  public PageDto<RepairOrderSummaryDto> findAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "createdAt") String sort,
      @RequestParam(defaultValue = "desc") String direction
  ) {
    log.info("GET /api/repair-orders [page={}, size={}, sort={}, direction={}]",
        page, size, sort, direction);
    return repairOrderService.findAll(page, size, sort, direction);
  }

  @GetMapping("/{id}")
  public RepairOrderDetailDto findById(@PathVariable UUID id) {
    log.info("GET /api/repair-orders [id={}]", id);
    return repairOrderService.findById(id);
  }

  @ExceptionHandler(RepairOrderNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFound(RepairOrderNotFoundException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
  }
}
