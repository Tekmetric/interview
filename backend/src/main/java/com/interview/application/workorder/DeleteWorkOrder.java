package com.interview.application.workorder;

import com.interview.application.WorkOrderRepository;

import java.util.UUID;

public class DeleteWorkOrder {

    private final WorkOrderRepository repository;

    public DeleteWorkOrder(WorkOrderRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        repository.findById(id).ifPresent(wo -> repository.deleteById(id));
    }
}
