package com.interview.controller;

import com.interview.exception.ResourceNotFoundException;
import com.interview.model.WorkOrder;
import com.interview.service.WorkOrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    @GetMapping("/api/workOrder/{workOrderId}")
    public WorkOrder getWorkOrder(@PathVariable Long workOrderId) {
        return workOrderService.get(workOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("No work order found with id: " + workOrderId));
    }

    @GetMapping("/api/workOrders")
    public Page<WorkOrder> getAllWorkOrders(Pageable pageable) {
        return workOrderService.getAll(pageable);
    }

    @PostMapping("/api/workOrder")
    public WorkOrder createWorkOrder(@RequestBody WorkOrder workOrder) {
        return workOrderService.create(workOrder);
    }

    @PutMapping("/api/workOrder/{workOrderId}")
    public WorkOrder updateWorkOrder(@PathVariable Long workOrderId, @RequestBody WorkOrder workOrder) {
        return workOrderService.update(workOrder.setId(workOrderId));
    }

    @DeleteMapping("/api/workOrder/{workOrderId}")
    public void deleteWorkOrder(@PathVariable Long workOrderId) {
        workOrderService.delete(workOrderId);
    }
}
