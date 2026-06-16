package com.interview.application.workorder;

import com.interview.application.EntityNotFoundException;
import com.interview.application.WorkOrderRepository;
import com.interview.domain.WorkOrder;

import java.util.UUID;

public class GetWorkOrder {

    private final WorkOrderRepository repository;

    public GetWorkOrder(WorkOrderRepository repository) {
        this.repository = repository;
    }

    public WorkOrder execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found: " + id));
    }
}
