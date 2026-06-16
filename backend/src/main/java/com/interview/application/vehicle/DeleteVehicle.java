package com.interview.application.vehicle;

import com.interview.application.ReferencedEntityException;
import com.interview.application.VehicleRepository;
import com.interview.application.WorkOrderRepository;

import java.util.UUID;

public class DeleteVehicle {

    private final VehicleRepository repository;
    private final WorkOrderRepository workOrderRepository;

    public DeleteVehicle(VehicleRepository repository, WorkOrderRepository workOrderRepository) {
        this.repository = repository;
        this.workOrderRepository = workOrderRepository;
    }

    public void execute(UUID id) {
        if (workOrderRepository.existsByVehicleId(id)) {
            throw new ReferencedEntityException("Cannot delete vehicle: it is referenced by one or more work orders.");
        }
        repository.findById(id).ifPresent(v -> repository.deleteById(id));
    }
}
