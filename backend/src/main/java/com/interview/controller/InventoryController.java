package com.interview.controller;


import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryFiltersPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "*", allowedHeaders = "*")
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

    @DeleteMapping(value = "/inventories")
    public ResponseEntity<Void> deleteInventories(
            @RequestParam Long[] ids
    ) {
        inventoryService.deleteInventories(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/inventories/{id}")
    public ResponseEntity<InventoryResponsePayload> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventory(id));
    }


    @GetMapping(value = "/inventories")
    public ResponseEntity<Page<InventoryResponsePayload>> getAllInventories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String field,
            @RequestParam(defaultValue = "DESC") String order,
           // @RequestParam(required = false) String filter
            InventoryFiltersPayload filters
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), field));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Count", "100");
        responseHeaders.set("Access-Control-Expose-Headers", "X-Total-Count");

        return ResponseEntity.ok().headers(responseHeaders).body(inventoryService.getAllInventories(pageable, filters));
    }
}