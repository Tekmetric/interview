package com.interview.controller;

import com.interview.dto.RepairOrderRequest;
import com.interview.dto.RepairOrderResponse;
import com.interview.service.RepairOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST endpoints for repair order CRUD operations.
 */
@RestController
@RequestMapping("/api/repair-orders")
public class RepairOrderController {

    private final RepairOrderService service;

    public RepairOrderController(RepairOrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RepairOrderResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RepairOrderResponse> create(
            @Valid @RequestBody RepairOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairOrderResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RepairOrderRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
