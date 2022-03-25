package com.interview.controller;

import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.service.InventoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping(value = "/inventories")
    public ResponseEntity<InventoryResponsePayload> createInventory(
            @Valid @RequestBody InsertInventoryRequestPayload createInventoryRequestPayload
    ) {
        return ResponseEntity.ok(inventoryService.createInventory(createInventoryRequestPayload));
    }

    @PutMapping(value = "/inventories/{id}")
    public ResponseEntity<InventoryResponsePayload> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInventoryRequestPayload updateInventoryRequestPayload
    ) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, updateInventoryRequestPayload));
    }

    @DeleteMapping(value = "/inventories/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/inventories/{id}")
    public ResponseEntity<InventoryResponsePayload> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }

    @GetMapping(value = "/inventories")
    public ResponseEntity<List<InventoryResponsePayload>> getAllInventories(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventories(pageable));
    }
}