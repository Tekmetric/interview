package com.interview.controller;

import com.interview.api.RepairOrderApi;
import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/repair-orders")
public class RepairOrderController implements RepairOrderApi {

    @Override
    @PostMapping
    public ResponseEntity<RepairOrderDto> create(CreateRepairOrderRequest createRepairOrderRequest) {
        return null;
    }

    @Override
    @GetMapping("/{repairOrderId}")
    public ResponseEntity<RepairOrderDto> getById(@PathVariable("repairOrderId") long repairOrderId) {
        return null;
    }

    @Override
    @GetMapping
    public ResponseEntity<PagedModel<RepairOrderDto>> getAll(Pageable pageable) {
        return null;
    }

    @Override
    @PutMapping("/{repairOrderId}")
    public ResponseEntity<RepairOrderDto> update(@PathVariable("repairOrderId") long repairOrderId, UpdateRepairOrderRequest updateRepairOrderRequest) {
        return null;
    }

    @Override
    @DeleteMapping("/{repairOrderId}")
    public ResponseEntity<Void> deleteById(long repairOrderId) {
        return ResponseEntity.noContent().build();
    }
}
