package com.interview.controller;

import com.interview.dto.PaginationRequestDTO;
import com.interview.dto.VehicleRequestDTO;
import com.interview.dto.VehicleResponseDTO;
import com.interview.dto.VehicleSummaryDTO;
import com.interview.service.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * VehicleController handles all vehicle-related API requests. It provides endpoints for CRUD
 * operations, and pagination.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VehicleController {

  private final VehicleService vehicleService;

  @GetMapping
  public ResponseEntity<List<VehicleSummaryDTO>> getAllVehicles() {
    log.info("GET /api/v1/vehicles - Fetching all vehicles");
    List<VehicleSummaryDTO> vehicles = vehicleService.getAllVehicles();
    return ResponseEntity.ok(vehicles);
  }

  @GetMapping("/paginated")
  public ResponseEntity<Page<VehicleSummaryDTO>> getAllVehiclesPaginated(
      @Valid PaginationRequestDTO pagination) {

    log.info("GET /api/v1/vehicles/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}",
        pagination.getPage(), pagination.getSize(), pagination.getSortBy(), pagination.getSortDir());

    Sort sort = pagination.getSortDir().equalsIgnoreCase("desc") ?
        Sort.by(pagination.getSortBy()).descending() : Sort.by(pagination.getSortBy()).ascending();
    Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

    Page<VehicleSummaryDTO> vehicles = vehicleService.getAllVehicles(pageable);
    return ResponseEntity.ok(vehicles);
  }

  @GetMapping("/{id}")
  public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
    log.info("GET /api/v1/vehicles/{} - Fetching vehicle by id", id);
    VehicleResponseDTO vehicle = vehicleService.getVehicleById(id);
    return ResponseEntity.ok(vehicle);
  }

  @GetMapping("/{id}/with-repair-orders")
  public ResponseEntity<VehicleResponseDTO> getVehicleByIdWithRepairOrders(@PathVariable Long id) {
    log.info("GET /api/v1/vehicles/{}/with-repair-orders - Fetching vehicle with repair orders",
        id);
    VehicleResponseDTO vehicle = vehicleService.getVehicleByIdWithRepairOrders(id);
    return ResponseEntity.ok(vehicle);
  }

  @GetMapping("/license-plate/{licensePlate}")
  public ResponseEntity<VehicleResponseDTO> getVehicleByLicensePlate(
      @PathVariable String licensePlate) {
    log.info("GET /api/v1/vehicles/license-plate/{} - Fetching vehicle by license plate",
        licensePlate);
    VehicleResponseDTO vehicle = vehicleService.getVehicleByLicensePlate(licensePlate);
    return ResponseEntity.ok(vehicle);
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<VehicleSummaryDTO>> getVehiclesByCustomerId(
      @PathVariable Long customerId) {
    log.info("GET /api/v1/vehicles/customer/{} - Fetching vehicles by customer id", customerId);
    List<VehicleSummaryDTO> vehicles = vehicleService.getVehiclesByCustomerId(customerId);
    return ResponseEntity.ok(vehicles);
  }

  @PostMapping
  public ResponseEntity<VehicleResponseDTO> createVehicle(
      @Valid @RequestBody VehicleRequestDTO vehicleRequest) {
    log.info("POST /api/v1/vehicles - Creating new vehicle with license plate: {}",
        vehicleRequest.licensePlate());
    VehicleResponseDTO createdVehicle = vehicleService.createVehicle(vehicleRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
  }

  @PutMapping("/{id}")
  public ResponseEntity<VehicleResponseDTO> updateVehicle(
      @PathVariable Long id,
      @Valid @RequestBody VehicleRequestDTO vehicleRequest) {
    log.info("PUT /api/v1/vehicles/{} - Updating vehicle", id);
    VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(id, vehicleRequest);
    return ResponseEntity.ok(updatedVehicle);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
    log.info("DELETE /api/v1/vehicles/{} - Deleting vehicle", id);
    vehicleService.deleteVehicle(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getTotalVehicles() {
    log.info("GET /api/v1/vehicles/count - Getting total vehicle count");
    long count = vehicleService.getTotalVehicles();
    return ResponseEntity.ok(count);
  }

  @GetMapping("/customer/{customerId}/count")
  public ResponseEntity<Long> getVehicleCountByCustomerId(@PathVariable Long customerId) {
    log.info("GET /api/v1/vehicles/customer/{}/count - Getting vehicle count for customer",
        customerId);
    long count = vehicleService.getVehicleCountByCustomerId(customerId);
    return ResponseEntity.ok(count);
  }

}
