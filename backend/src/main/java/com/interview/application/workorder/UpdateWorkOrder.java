package com.interview.application.workorder;

import com.interview.application.CustomerRepository;
import com.interview.application.EntityNotFoundException;
import com.interview.application.InvalidReferenceException;
import com.interview.application.VehicleRepository;
import com.interview.application.WorkOrderRepository;
import com.interview.domain.Vehicle;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderStatus;

import java.util.UUID;

public class UpdateWorkOrder {

    private final WorkOrderRepository repository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public UpdateWorkOrder(WorkOrderRepository repository, CustomerRepository customerRepository,
                          VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public WorkOrder execute(UUID id, UUID customerId, UUID vehicleId, String description, WorkOrderStatus status) {
        WorkOrder existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found: " + id));
        customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidReferenceException("Customer not found: " + customerId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new InvalidReferenceException("Vehicle not found: " + vehicleId));
        if (!vehicle.getCustomerId().equals(customerId)) {
            throw new InvalidReferenceException("Vehicle does not belong to the given customer.");
        }
        WorkOrder updated = new WorkOrder(
                existing.getId(),
                customerId,
                vehicleId,
                description,
                status,
                existing.getCreatedAt()
        );
        return repository.save(updated);
    }
}
