package com.interview.workorder.controller;

import com.interview.workorder.dto.WorkOrderRequest;
import com.interview.workorder.dto.WorkOrderResponse;
import com.interview.workorder.model.WorkOrderStatus;
import com.interview.workorder.service.WorkOrderService;
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
@Validated
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    public ResponseEntity<WorkOrderResponse> create(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @Valid @RequestBody WorkOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.create(customerId, request));
    }

    @GetMapping
    public Page<WorkOrderResponse> list(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @RequestParam(required = false) WorkOrderStatus status,
            @PageableDefault(page = 0, size = 20, sort = "id") Pageable pageable
    ) {
        return workOrderService.list(customerId, status, pageable);
    }

    @GetMapping("/{id}")
    public WorkOrderResponse getById(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id
    ) {
        return workOrderService.getById(customerId, id);
    }

    @PutMapping("/{id}")
    public WorkOrderResponse update(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderRequest request
    ) {
        return workOrderService.update(customerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId,
            @PathVariable Long id
    ) {
        workOrderService.delete(customerId, id);
    }
}
