package com.interview.controller;

import com.interview.dto.InventoryItemResponse;
import com.interview.dto.StockMovementCreateRequest;
import com.interview.dto.StockMovementResponse;
import com.interview.entity.InventoryItem;
import com.interview.entity.MovementReason;
import com.interview.entity.MovementType;
import com.interview.entity.StockMovement;
import com.interview.mapper.InventoryItemMapper;
import com.interview.mapper.StockMovementMapper;
import com.interview.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    private final InventoryItemMapper inventoryItemMapper;
    private final StockMovementMapper stockMovementMapper;
    
    @Autowired
    public InventoryController(InventoryService inventoryService,
                             InventoryItemMapper inventoryItemMapper,
                             StockMovementMapper stockMovementMapper) {
        this.inventoryService = inventoryService;
        this.inventoryItemMapper = inventoryItemMapper;
        this.stockMovementMapper = stockMovementMapper;
    }
    
    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAllInventory() {
        List<InventoryItem> items = inventoryService.getAllInventoryItems();
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryItemResponse>> getInventoryByProduct(@PathVariable Long productId) {
        List<InventoryItem> items = inventoryService.getInventoryByProduct(productId);
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryItemResponse>> getInventoryByWarehouse(@PathVariable Long warehouseId) {
        List<InventoryItem> items = inventoryService.getInventoryByWarehouse(warehouseId);
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemResponse>> getLowStockItems(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<InventoryItem> items = inventoryService.getLowStockItems(threshold);
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reorder-alerts")
    public ResponseEntity<List<InventoryItemResponse>> getReorderAlerts() {
        List<InventoryItem> items = inventoryService.getItemsAtReorderPoint();
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<InventoryItemResponse>> searchInventory(
            @RequestParam(required = false) String category) {
        List<InventoryItem> items = inventoryService.getInventoryByCategory(category);
        List<InventoryItemResponse> response = inventoryItemMapper.toResponseList(items);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/movements")
    public ResponseEntity<StockMovementResponse> recordStockMovement(
            @Valid @RequestBody StockMovementCreateRequest request) {
        
        MovementType movementType = MovementType.valueOf(request.movementType());
        MovementReason movementReason = MovementReason.valueOf(request.movementReason());
        
        StockMovement movement = inventoryService.recordStockMovement(
                request.productId(),
                request.warehouseId(),
                movementType,
                movementReason,
                request.quantity(),
                request.unitCost(),
                request.referenceNumber(),
                request.notes()
        );
        
        StockMovementResponse response = stockMovementMapper.toResponse(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<StockMovementResponse>> getProductMovementHistory(@PathVariable Long productId) {
        List<StockMovement> movements = inventoryService.getMovementHistory(productId);
        List<StockMovementResponse> response = stockMovementMapper.toResponseList(movements);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/movements/warehouse/{warehouseId}")
    public ResponseEntity<List<StockMovementResponse>> getWarehouseMovements(@PathVariable Long warehouseId) {
        List<StockMovement> movements = inventoryService.getWarehouseMovements(warehouseId);
        List<StockMovementResponse> response = stockMovementMapper.toResponseList(movements);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/adjust")
    public ResponseEntity<InventoryItemResponse> adjustInventory(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Integer reorderPoint) {
        
        InventoryItem item = inventoryService.createOrUpdateInventoryItem(
                productId, warehouseId, quantity, reorderPoint);
        InventoryItemResponse response = inventoryItemMapper.toResponse(item);
        return ResponseEntity.ok(response);
    }
}