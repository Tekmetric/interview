package com.interview.controller;

import com.interview.api.RepairOrderApi;
import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import com.interview.service.RepairOrderService;
import com.interview.validator.PaginationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/repair-orders")
@RequiredArgsConstructor
public class RepairOrderController implements RepairOrderApi {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "vin");

    private final RepairOrderService repairOrderService;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RepairOrderDto> create(@Valid @RequestBody CreateRepairOrderRequest createRepairOrderRequest) {
        RepairOrderDto repairOrder = repairOrderService.create(createRepairOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(repairOrder);
    }

    @Override
    @GetMapping("/{repairOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RepairOrderDto> getById(@PathVariable("repairOrderId") long repairOrderId) {
        RepairOrderDto repairOrder = repairOrderService.findById(repairOrderId);
        return ResponseEntity.ok(repairOrder);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<PagedModel<RepairOrderDto>> getAll(@PageableDefault(size = 5, sort = "id") Pageable pageable) {

        PaginationValidator.validate(pageable, ALLOWED_SORT_FIELDS);

        Page<RepairOrderDto> page = repairOrderService.getAll(pageable);
        return ResponseEntity.ok(new PagedModel<>(page));
    }

    @Override
    @PutMapping("/{repairOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RepairOrderDto> update(@PathVariable("repairOrderId") long repairOrderId, @Valid @RequestBody UpdateRepairOrderRequest updateRepairOrderRequest) {
        RepairOrderDto updatedRepairOrder = repairOrderService.update(repairOrderId, updateRepairOrderRequest);
        return ResponseEntity.ok(updatedRepairOrder);
    }

    @Override
    @DeleteMapping("/{repairOrderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable("repairOrderId") long repairOrderId) {
        repairOrderService.deleteById(repairOrderId);
        return ResponseEntity.noContent().build();
    }
}
