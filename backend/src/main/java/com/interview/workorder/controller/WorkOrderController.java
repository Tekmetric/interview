package com.interview.workorder.controller;

import com.interview.workorder.dto.WorkOrderRequest;
import com.interview.workorder.dto.WorkOrderResponse;
import com.interview.workorder.model.WorkOrderStatus;
import com.interview.workorder.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerId}/work-orders")
// TODO(prod): Introduce explicit API versioning strategy before public release.
@Tag(name = "Work Orders", description = "Manage work orders under a customer")
@Validated
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    @Operation(
            summary = "Create work order",
            description = "Creates a new work order for the given customer.",
            tags = {"Work Orders"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public ResponseEntity<WorkOrderResponse> create(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @Valid @RequestBody WorkOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.create(customerId, request));
    }

    @GetMapping
    @Operation(
            summary = "List work orders",
            description = "Returns paged work orders for a customer with optional status filter.",
            tags = {"Work Orders"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public Page<WorkOrderResponse> list(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @RequestParam(required = false) WorkOrderStatus status,
            @PageableDefault(page = 0, size = 20, sort = "id") Pageable pageable
    ) {
        return workOrderService.list(customerId, status, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get work order",
            description = "Returns a single work order by id for the given customer.",
            tags = {"Work Orders"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public WorkOrderResponse getById(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id
    ) {
        return workOrderService.getById(customerId, id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update work order",
            description = "Updates an existing work order under the given customer.",
            tags = {"Work Orders"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public WorkOrderResponse update(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderRequest request
    ) {
        return workOrderService.update(customerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete work order",
            description = "Deletes a work order by id for the given customer.",
            tags = {"Work Orders"},
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    public void delete(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id
    ) {
        workOrderService.delete(customerId, id);
    }
}
