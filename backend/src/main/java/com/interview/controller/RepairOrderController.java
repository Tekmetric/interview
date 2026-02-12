package com.interview.controller;

import com.interview.dto.CreateOrderDto;
import com.interview.dto.DetailOrderDto;
import com.interview.dto.ListOrderDto;
import com.interview.service.RepairOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Repair Orders", description = "CRUD operations for repair orders")
public class RepairOrderController {

  private final RepairOrderService repairOrderService;

  public RepairOrderController(RepairOrderService repairOrderService) {
    this.repairOrderService = repairOrderService;
  }

  @GetMapping
  @Operation(
      summary = "Get all repair orders",
      description = "Retrieve all repair orders with pagination")
  public ResponseEntity<Page<ListOrderDto>> getAllOrders(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(repairOrderService.getAllOrders(page, size));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get repair order by ID",
      description = "Retrieve a specific repair order by its ID")
  public ResponseEntity<DetailOrderDto> getOrderById(@PathVariable Long id) {
    return repairOrderService
        .getOrderById(id)
        .map(order -> ResponseEntity.ok(order))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(
      summary = "Create repair order",
      description = "Create a new repair order with car and invoice")
  public ResponseEntity<DetailOrderDto> createOrder(@RequestBody CreateOrderDto createDto) {
    DetailOrderDto created = repairOrderService.createOrder(createDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update repair order", description = "Update an existing repair order")
  public ResponseEntity<DetailOrderDto> updateOrder(
      @PathVariable Long id, @RequestBody CreateOrderDto createDto) {
    try {
      DetailOrderDto updated = repairOrderService.updateOrder(id, createDto);
      return ResponseEntity.ok(updated);
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete repair order", description = "Delete a repair order by ID")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    repairOrderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}
