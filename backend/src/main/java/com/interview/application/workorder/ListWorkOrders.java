package com.interview.application.workorder;

import com.interview.application.WorkOrderRepository;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderStatus;

import java.util.List;
import java.util.Optional;

public class ListWorkOrders {

    private final WorkOrderRepository repository;

    public ListWorkOrders(WorkOrderRepository repository) {
        this.repository = repository;
    }

    public List<WorkOrder> execute(Optional<WorkOrderStatus> status) {
        return repository.findAll(status);
    }
}
