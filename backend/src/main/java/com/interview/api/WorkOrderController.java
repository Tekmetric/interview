package com.interview.api;

import com.interview.api.dto.WorkOrderRequest;
import com.interview.api.dto.WorkOrderResponse;
import com.interview.api.dto.WorkOrderUpdateRequest;
import com.interview.application.workorder.CreateWorkOrder;
import com.interview.application.workorder.DeleteWorkOrder;
import com.interview.application.workorder.GetWorkOrder;
import com.interview.application.workorder.ListWorkOrders;
import com.interview.application.workorder.UpdateWorkOrder;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/work-orders")
@Transactional
public class WorkOrderController {

    private final CreateWorkOrder createWorkOrder;
    private final GetWorkOrder getWorkOrder;
    private final UpdateWorkOrder updateWorkOrder;
    private final ListWorkOrders listWorkOrders;
    private final DeleteWorkOrder deleteWorkOrder;

    public WorkOrderController(CreateWorkOrder createWorkOrder, GetWorkOrder getWorkOrder,
                              UpdateWorkOrder updateWorkOrder, ListWorkOrders listWorkOrders,
                              DeleteWorkOrder deleteWorkOrder) {
        this.createWorkOrder = createWorkOrder;
        this.getWorkOrder = getWorkOrder;
        this.updateWorkOrder = updateWorkOrder;
        this.listWorkOrders = listWorkOrders;
        this.deleteWorkOrder = deleteWorkOrder;
    }

    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody WorkOrderRequest request) {
        WorkOrder workOrder = createWorkOrder.execute(
                request.getCustomerId(),
                request.getVehicleId(),
                request.getDescription()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(workOrder));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<WorkOrderResponse> getById(@PathVariable UUID id) {
        WorkOrder workOrder = getWorkOrder.execute(id);
        return ResponseEntity.ok(toResponse(workOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody WorkOrderUpdateRequest request) {
        WorkOrder workOrder = updateWorkOrder.execute(
                id,
                request.getCustomerId(),
                request.getVehicleId(),
                request.getDescription(),
                request.getStatus()
        );
        return ResponseEntity.ok(toResponse(workOrder));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<WorkOrderResponse>> list(
            @RequestParam(required = false) Optional<WorkOrderStatus> status) {
        List<WorkOrderResponse> list = listWorkOrders.execute(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteWorkOrder.execute(id);
        return ResponseEntity.noContent().build();
    }

    private WorkOrderResponse toResponse(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getCustomerId(),
                workOrder.getVehicleId(),
                workOrder.getDescription(),
                workOrder.getStatus(),
                workOrder.getCreatedAt()
        );
    }
}
