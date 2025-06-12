package com.interview.service;

import com.interview.dto.RepairOrderRequestDTO;
import com.interview.dto.RepairOrderResponseDTO;
import com.interview.dto.RepairOrderSummaryDTO;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.RepairOrderMapper;
import com.interview.model.RepairOrderEntity;
import com.interview.model.RepairOrderStatus;
import com.interview.model.VehicleEntity;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.VehicleRepository;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing repair orders. Provides methods to create, read, update, and delete
 * repair orders, as well as methods to fetch repair order summaries and details.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RepairOrderService {

  private final RepairOrderRepository repairOrderRepository;
  private final VehicleRepository vehicleRepository;
  private final RepairOrderMapper repairOrderMapper;

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getAllRepairOrders() {
    log.debug("Fetching all repair orders");
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findAllByOrderById();
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public Page<RepairOrderSummaryDTO> getAllRepairOrders(Pageable pageable) {
    log.debug("Fetching repair orders with pagination: {}", pageable);
    Page<RepairOrderEntity> repairOrders = repairOrderRepository.findAll(pageable);
    return repairOrders.map(repairOrderMapper::toSummaryDTO);
  }

  @Transactional(readOnly = true)
  public RepairOrderResponseDTO getRepairOrderById(Long id) {
    log.debug("Fetching repair order with id: {}", id);
    RepairOrderEntity repairOrder = repairOrderRepository.findByIdWithVehicleAndCustomer(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Repair order not found with id: " + id));
    return repairOrderMapper.toResponseDTO(repairOrder);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByVehicleId(Long vehicleId) {
    log.debug("Fetching repair orders for vehicle id: {}", vehicleId);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByVehicleId(vehicleId);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByCustomerId(Long customerId) {
    log.debug("Fetching repair orders for customer id: {}", customerId);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByCustomerId(customerId);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByStatus(RepairOrderStatus status) {
    log.debug("Fetching repair orders by status: {}", status);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByStatus(status);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByVehicleLicensePlate(String licensePlate) {
    log.debug("Fetching repair orders for vehicle license plate: {}", licensePlate);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByVehicleLicensePlate(
        licensePlate);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> searchRepairOrdersByDescription(String description) {
    log.debug("Searching repair orders by description: {}", description);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByDescriptionContainingIgnoreCase(
        description);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByDateRange(OffsetDateTime startDate,
      OffsetDateTime endDate) {
    log.debug("Fetching repair orders created between {} and {}", startDate, endDate);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByCreatedDateBetween(startDate,
        endDate);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRepairOrdersByUpdatedDateRange(OffsetDateTime startDate,
      OffsetDateTime endDate) {
    log.debug("Fetching repair orders updated between {} and {}", startDate, endDate);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByUpdatedDateBetween(startDate,
        endDate);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getActiveRepairOrders() {
    log.debug("Fetching active repair orders");
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findActiveRepairOrders();
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getCompletedRepairOrders() {
    log.debug("Fetching completed repair orders");
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findCompletedRepairOrdersOrderByUpdatedDate();
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  @Transactional(readOnly = true)
  public List<RepairOrderSummaryDTO> getRecentRepairOrdersByStatus(RepairOrderStatus status, int days) {
    log.debug("Fetching repair orders with status {} from last {} days", status, days);
    OffsetDateTime date = OffsetDateTime.now().minusDays(days);
    List<RepairOrderEntity> repairOrders = repairOrderRepository.findByStatusAndCreatedDateAfter(
        status, date);
    return repairOrderMapper.toSummaryDTOList(repairOrders);
  }

  public RepairOrderResponseDTO createRepairOrder(RepairOrderRequestDTO repairOrderRequest) {
    log.debug("Creating new repair order for vehicle id: {}", repairOrderRequest.vehicleId());

    VehicleEntity vehicle = vehicleRepository.findByIdWithCustomer(repairOrderRequest.vehicleId())
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Vehicle not found with id: " + repairOrderRequest.vehicleId()));

    RepairOrderEntity repairOrder = repairOrderMapper.toEntity(repairOrderRequest, vehicle);
    RepairOrderEntity savedRepairOrder = repairOrderRepository.save(repairOrder);
    log.info("Repair order created successfully with id: {}", savedRepairOrder.getId());
    return repairOrderMapper.toResponseDTO(savedRepairOrder);
  }

  public RepairOrderResponseDTO updateRepairOrder(Long id,
      RepairOrderRequestDTO repairOrderRequest) {
    log.debug("Updating repair order with id: {}", id);

    RepairOrderEntity existingRepairOrder = repairOrderRepository.findByIdWithVehicleAndCustomer(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Repair order not found with id: " + id));

    // Update fields using mapper
    VehicleEntity vehicle = existingRepairOrder.getVehicle();
    if (!existingRepairOrder.getVehicle().getId().equals(repairOrderRequest.vehicleId())) {
      vehicle = vehicleRepository.findByIdWithCustomer(repairOrderRequest.vehicleId())
          .orElseThrow(
              () -> new ResourceNotFoundException(
                  "Vehicle not found with id: " + repairOrderRequest.vehicleId()));
    }
    repairOrderMapper.updateEntityFromDTO(existingRepairOrder, repairOrderRequest, vehicle);

    RepairOrderEntity updatedRepairOrder = repairOrderRepository.save(existingRepairOrder);
    log.info("Repair order updated successfully with id: {}", updatedRepairOrder.getId());
    return repairOrderMapper.toResponseDTO(updatedRepairOrder);
  }

  public RepairOrderResponseDTO updateRepairOrderStatus(Long id, RepairOrderStatus status) {
    log.debug("Updating repair order status with id: {} to status: {}", id, status);

    RepairOrderEntity existingRepairOrder = repairOrderRepository.findByIdWithVehicleAndCustomer(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Repair order not found with id: " + id));

    existingRepairOrder.setStatus(status);
    RepairOrderEntity updatedRepairOrder = repairOrderRepository.save(existingRepairOrder);
    log.info("Repair order status updated successfully with id: {}", updatedRepairOrder.getId());
    return repairOrderMapper.toResponseDTO(updatedRepairOrder);
  }

  public void deleteRepairOrder(Long id) {
    log.debug("Deleting repair order with id: {}", id);

    RepairOrderEntity repairOrder = repairOrderRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Repair order not found with id: " + id));
    repairOrderRepository.delete(repairOrder);
    log.info("Repair order deleted successfully with id: {}", id);
  }

  @Transactional(readOnly = true)
  public long getTotalRepairOrders() {
    return repairOrderRepository.count();
  }

  @Transactional(readOnly = true)
  public long getRepairOrderCountByVehicleId(Long vehicleId) {
    return repairOrderRepository.countByVehicleId(vehicleId);
  }

  @Transactional(readOnly = true)
  public long getRepairOrderCountByCustomerId(Long customerId) {
    return repairOrderRepository.countByCustomerId(customerId);
  }

}
