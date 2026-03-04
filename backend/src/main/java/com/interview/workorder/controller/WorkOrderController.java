package com.interview.workorder.controller;

import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;
import com.interview.workorder.service.WorkOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/{customerId}/work-orders")
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
    public List<WorkOrderResponse> list(
            @PathVariable @Positive(message = "customerId must be positive") Long customerId
    ) {
        return workOrderService.list(customerId);
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
