package com.interview.service;

import com.interview.dto.RepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.entity.RepairOrder;
import com.interview.error.DuplicateResourceException;
import com.interview.error.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates application/business logic for repair orders.
 * <p>
 * The controller is intentionally kept thin so that validation flow,
 * duplicate checks, and entity-to-DTO mapping stay centralized here.
 */
@Service
public class RepairOrderService {

    private final RepairOrderRepository repository;

    public RepairOrderService(RepairOrderRepository repository) {
        this.repository = repository;
    }

    public List<RepairOrderResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RepairOrderResponse getById(Long id) {
        return toResponse(findById(id));
    }

    /**
     * Creates a new repair order after validating that the VIN does not
     * already exist in another row.
     */
    @Transactional
    public RepairOrderResponse create(RepairOrderRequest request) {
        if (repository.existsByVehicleVin(request.getVehicleVin())) {
            throw new DuplicateResourceException(
                    "Repair order already exists for VIN: " + request.getVehicleVin()
            );
        }

        RepairOrder repairOrder = new RepairOrder();
        applyRequest(repairOrder, request);

        return toResponse(repository.save(repairOrder));
    }

    /**
     * Updates an existing repair order and prevents VIN collisions with
     * other rows in the table.
     */
    @Transactional
    public RepairOrderResponse update(Long id, RepairOrderRequest request) {
        RepairOrder existing = findById(id);

        if (repository.existsByVehicleVinAndIdNot(request.getVehicleVin(), id)) {
            throw new DuplicateResourceException(
                    "Repair order already exists for VIN: " + request.getVehicleVin()
            );
        }

        applyRequest(existing, request);

        return toResponse(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    /**
     * Shared lookup helper so all "not found" behavior stays consistent.
     */
    private RepairOrder findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + id));
    }

    /**
     * Applies request fields to the entity for both create and update flows.
     */
    private void applyRequest(RepairOrder repairOrder, RepairOrderRequest request) {
        repairOrder.setCustomerName(request.getCustomerName());
        repairOrder.setVehicleVin(request.getVehicleVin());
        repairOrder.setDescription(request.getDescription());
        repairOrder.setStatus(request.getStatus());
        repairOrder.setTotalCost(request.getTotalCost());
    }

    /**
     * Converts the persistence model into the API response model.
     */
    private RepairOrderResponse toResponse(RepairOrder repairOrder) {
        return new RepairOrderResponse(
                repairOrder.getId(),
                repairOrder.getCustomerName(),
                repairOrder.getVehicleVin(),
                repairOrder.getDescription(),
                repairOrder.getStatus(),
                repairOrder.getTotalCost(),
                repairOrder.getCreatedAt(),
                repairOrder.getUpdatedAt()
        );
    }
}
