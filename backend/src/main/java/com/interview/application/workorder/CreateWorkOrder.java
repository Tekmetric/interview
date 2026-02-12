package com.interview.application.workorder;

import com.interview.application.CustomerRepository;
import com.interview.application.InvalidReferenceException;
import com.interview.application.VehicleRepository;
import com.interview.application.WorkOrderRepository;
import com.interview.domain.Vehicle;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderStatus;

import java.time.Instant;
import java.util.UUID;

public class CreateWorkOrder {

    private final WorkOrderRepository repository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    public CreateWorkOrder(WorkOrderRepository repository, CustomerRepository customerRepository,
                          VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public WorkOrder execute(UUID customerId, UUID vehicleId, String description) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidReferenceException("Customer not found: " + customerId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new InvalidReferenceException("Vehicle not found: " + vehicleId));
        if (!vehicle.getCustomerId().equals(customerId)) {
            throw new InvalidReferenceException("Vehicle does not belong to the given customer.");
        }
        WorkOrder workOrder = new WorkOrder(
                UUID.randomUUID(),
                customerId,
                vehicleId,
                description,
                WorkOrderStatus.OPEN,
                Instant.now()
        );
        return repository.save(workOrder);
    }
}
