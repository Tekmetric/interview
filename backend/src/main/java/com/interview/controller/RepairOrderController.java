package com.interview.controller;

import com.interview.dto.PaginationRequestDTO;
import com.interview.dto.RepairOrderRequestDTO;
import com.interview.dto.RepairOrderResponseDTO;
import com.interview.dto.RepairOrderSummaryDTO;
import com.interview.model.RepairOrderStatus;
import com.interview.service.RepairOrderService;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RepairOrderController handles all repair-order-related API requests. It provides endpoints for
 * CRUD operations, and pagination.
 */
@RestController
@RequestMapping("/api/v1/repair-orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RepairOrderController {

  private final RepairOrderService repairOrderService;

  @GetMapping
  public ResponseEntity<List<RepairOrderSummaryDTO>> getAllRepairOrders() {
    log.info("GET /api/v1/repair-orders - Fetching all repair orders");
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getAllRepairOrders();
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/paginated")
  public ResponseEntity<Page<RepairOrderSummaryDTO>> getAllRepairOrdersPaginated(
      @Valid PaginationRequestDTO pagination) {

    log.info("GET /api/v1/repair-orders/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}",
        pagination.getPage(), pagination.getSize(), pagination.getSortBy(), pagination.getSortDir());

    Sort sort = pagination.getSortDir().equalsIgnoreCase("desc") ?
        Sort.by(pagination.getSortBy()).descending() : Sort.by(pagination.getSortBy()).ascending();
    Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

    Page<RepairOrderSummaryDTO> repairOrders = repairOrderService.getAllRepairOrders(pageable);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RepairOrderResponseDTO> getRepairOrderById(@PathVariable Long id) {
    log.info("GET /api/v1/repair-orders/{} - Fetching repair order by id", id);
    RepairOrderResponseDTO repairOrder = repairOrderService.getRepairOrderById(id);
    return ResponseEntity.ok(repairOrder);
  }

  @GetMapping("/vehicle/{vehicleId}")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByVehicleId(
      @PathVariable Long vehicleId) {
    log.info("GET /api/v1/repair-orders/vehicle/{} - Fetching repair orders by vehicle id",
        vehicleId);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByVehicleId(
        vehicleId);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByCustomerId(
      @PathVariable Long customerId) {
    log.info("GET /api/v1/repair-orders/customer/{} - Fetching repair orders by customer id",
        customerId);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByCustomerId(
        customerId);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByStatus(
      @PathVariable RepairOrderStatus status) {
    log.info("GET /api/v1/repair-orders/status/{} - Fetching repair orders by status", status);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByStatus(status);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/license-plate/{licensePlate}")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByVehicleLicensePlate(
      @PathVariable String licensePlate) {
    log.info("GET /api/v1/repair-orders/license-plate/{} - Fetching repair orders by license plate",
        licensePlate);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByVehicleLicensePlate(
        licensePlate);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/date-range")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
    log.info("GET /api/v1/repair-orders/date-range - Fetching repair orders between {} and {}",
        startDate, endDate);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByDateRange(
        startDate, endDate);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/updated-date-range")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRepairOrdersByUpdatedDateRange(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
    log.info(
        "GET /api/v1/repair-orders/updated-date-range - Fetching repair orders updated between {} and {}",
        startDate, endDate);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRepairOrdersByUpdatedDateRange(
        startDate, endDate);
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/active")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getActiveRepairOrders() {
    log.info("GET /api/v1/repair-orders/active - Fetching active repair orders");
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getActiveRepairOrders();
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/completed")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getCompletedRepairOrders() {
    log.info("GET /api/v1/repair-orders/completed - Fetching completed repair orders");
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getCompletedRepairOrders();
    return ResponseEntity.ok(repairOrders);
  }

  @GetMapping("/recent")
  public ResponseEntity<List<RepairOrderSummaryDTO>> getRecentRepairOrdersByStatus(
      @RequestParam RepairOrderStatus status,
      @RequestParam(defaultValue = "30") int days) {
    log.info(
        "GET /api/v1/repair-orders/recent - Fetching recent repair orders with status {} from last {} days",
        status, days);
    List<RepairOrderSummaryDTO> repairOrders = repairOrderService.getRecentRepairOrdersByStatus(
        status, days);
    return ResponseEntity.ok(repairOrders);
  }

  @PostMapping
  public ResponseEntity<RepairOrderResponseDTO> createRepairOrder(
      @Valid @RequestBody RepairOrderRequestDTO repairOrderRequest) {
    log.info("POST /api/v1/repair-orders - Creating new repair order for vehicle id: {}",
        repairOrderRequest.vehicleId());
    RepairOrderResponseDTO createdRepairOrder = repairOrderService.createRepairOrder(
        repairOrderRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRepairOrder);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RepairOrderResponseDTO> updateRepairOrder(
      @PathVariable Long id,
      @Valid @RequestBody RepairOrderRequestDTO repairOrderRequest) {
    log.info("PUT /api/v1/repair-orders/{} - Updating repair order", id);
    RepairOrderResponseDTO updatedRepairOrder = repairOrderService.updateRepairOrder(id,
        repairOrderRequest);
    return ResponseEntity.ok(updatedRepairOrder);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<RepairOrderResponseDTO> updateRepairOrderStatus(
      @PathVariable Long id,
      @RequestParam RepairOrderStatus status) {
    log.info("PATCH /api/v1/repair-orders/{}/status - Updating repair order status to: {}", id,
        status);
    RepairOrderResponseDTO updatedRepairOrder = repairOrderService.updateRepairOrderStatus(id,
        status);
    return ResponseEntity.ok(updatedRepairOrder);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRepairOrder(@PathVariable Long id) {
    log.info("DELETE /api/v1/repair-orders/{} - Deleting repair order", id);
    repairOrderService.deleteRepairOrder(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getTotalRepairOrders() {
    log.info("GET /api/v1/repair-orders/count - Getting total repair order count");
    long count = repairOrderService.getTotalRepairOrders();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/vehicle/{vehicleId}/count")
  public ResponseEntity<Long> getRepairOrderCountByVehicleId(@PathVariable Long vehicleId) {
    log.info("GET /api/v1/repair-orders/vehicle/{}/count - Getting repair order count for vehicle",
        vehicleId);
    long count = repairOrderService.getRepairOrderCountByVehicleId(vehicleId);
    return ResponseEntity.ok(count);
  }

  @GetMapping("/customer/{customerId}/count")
  public ResponseEntity<Long> getRepairOrderCountByCustomerId(@PathVariable Long customerId) {
    log.info(
        "GET /api/v1/repair-orders/customer/{}/count - Getting repair order count for customer",
        customerId);
    long count = repairOrderService.getRepairOrderCountByCustomerId(customerId);
    return ResponseEntity.ok(count);
  }

}
