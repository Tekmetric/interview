package com.interview.api.controller;

import com.interview.api.mapper.LaborLineItemApiMapper;
import com.interview.api.mapper.PartLineItemApiMapper;
import com.interview.api.mapper.WorkOrderApiMapper;
import com.interview.api.request.LaborLineItemRequest;
import com.interview.api.request.PartLineItemRequest;
import com.interview.api.request.WorkOrderRequest;
import com.interview.api.request.WorkOrderSearchRequest;
import com.interview.api.response.LaborLineItemResponse;
import com.interview.api.response.PartLineItemResponse;
import com.interview.api.response.WorkOrderResponse;
import com.interview.api.response.WorkOrderSearchResponse;
import com.interview.domain.LaborLineItem;
import com.interview.domain.PartLineItem;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderDetail;
import com.interview.service.WorkOrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work-orders")
public class WorkOrderRestController {
    private final WorkOrderService workOrderService;
    private final WorkOrderApiMapper workOrderApiMapper;
    private final PartLineItemApiMapper partLineItemApiMapper;
    private final LaborLineItemApiMapper laborLineItemApiMapper;

    public WorkOrderRestController(
            WorkOrderService workOrderService,
            WorkOrderApiMapper workOrderApiMapper,
            PartLineItemApiMapper partLineItemApiMapper,
            LaborLineItemApiMapper laborLineItemApiMapper) {
        this.workOrderService = workOrderService;
        this.workOrderApiMapper = workOrderApiMapper;
        this.partLineItemApiMapper = partLineItemApiMapper;
        this.laborLineItemApiMapper = laborLineItemApiMapper;
    }

    @GetMapping
    public ResponseEntity<Page<WorkOrderSearchResponse>> getAllWorkOrders(
            WorkOrderSearchRequest searchRequest, @PageableDefault(size = 20) Pageable pageable) {
        final Page<WorkOrderSearchResponse> workOrders = workOrderService
                .findAll(searchRequest.customerId(), searchRequest.vehicleId(), pageable)
                .map(workOrderApiMapper::toSearchResponse);
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(@PathVariable UUID id) {
        final WorkOrderDetail detail = workOrderService.findByIdWithLineItems(id);
        final List<PartLineItemResponse> parts = detail.partLineItems().stream()
                .map(partLineItemApiMapper::toResponse)
                .toList();
        final List<LaborLineItemResponse> labors = detail.laborLineItems().stream()
                .map(laborLineItemApiMapper::toResponse)
                .toList();
        return ResponseEntity.ok(workOrderApiMapper.toResponse(detail.workOrder(), parts, labors));
    }

    @PostMapping
    public ResponseEntity<WorkOrderSearchResponse> createWorkOrder(@Valid @RequestBody WorkOrderRequest request) {
        final WorkOrder workOrder = workOrderService.create(workOrderApiMapper.toDomain(request));
        final WorkOrderSearchResponse response = workOrderApiMapper.toSearchResponse(workOrder);
        return ResponseEntity.created(URI.create("/work-orders/" + response.id()))
                .body(response);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<WorkOrderSearchResponse> updateWorkOrder(
            @PathVariable UUID id, @Valid @RequestBody WorkOrderRequest request) {
        final WorkOrder workOrder = workOrderService.update(id, workOrderApiMapper.toDomain(id, request));
        return ResponseEntity.ok(workOrderApiMapper.toSearchResponse(workOrder));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable UUID id) {
        workOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{workOrderId}/part-line-items")
    public ResponseEntity<PartLineItemResponse> createPartLineItem(
            @PathVariable UUID workOrderId, @Valid @RequestBody PartLineItemRequest request) {
        final PartLineItem item =
                workOrderService.createPartLineItem(workOrderId, partLineItemApiMapper.toDomain(request));
        final PartLineItemResponse response = partLineItemApiMapper.toResponse(item);
        return ResponseEntity.created(URI.create("/work-orders/" + workOrderId + "/part-line-items/" + response.id()))
                .body(response);
    }

    @DeleteMapping(path = "/{workOrderId}/part-line-items/{lineItemId}")
    public ResponseEntity<Void> deletePartLineItem(@PathVariable UUID workOrderId, @PathVariable UUID lineItemId) {
        workOrderService.deletePartLineItem(workOrderId, lineItemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{workOrderId}/labor-line-items")
    public ResponseEntity<LaborLineItemResponse> createLaborLineItem(
            @PathVariable UUID workOrderId, @Valid @RequestBody LaborLineItemRequest request) {
        final LaborLineItem item =
                workOrderService.createLaborLineItem(workOrderId, laborLineItemApiMapper.toDomain(request));
        final LaborLineItemResponse response = laborLineItemApiMapper.toResponse(item);
        return ResponseEntity.created(URI.create("/work-orders/" + workOrderId + "/labor-line-items/" + response.id()))
                .body(response);
    }

    @DeleteMapping(path = "/{workOrderId}/labor-line-items/{lineItemId}")
    public ResponseEntity<Void> deleteLaborLineItem(@PathVariable UUID workOrderId, @PathVariable UUID lineItemId) {
        workOrderService.deleteLaborLineItem(workOrderId, lineItemId);
        return ResponseEntity.noContent().build();
    }
}
