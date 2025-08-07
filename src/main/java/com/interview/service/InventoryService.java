package com.interview.service;

import com.interview.entity.*;
import com.interview.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {
    
    private final InventoryItemRepository inventoryRepository;
    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    
    @Autowired
    public InventoryService(InventoryItemRepository inventoryRepository,
                          StockMovementRepository movementRepository,
                          ProductRepository productRepository,
                          WarehouseRepository warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<InventoryItem> getInventoryItem(Long productId, Long warehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        
        return inventoryRepository.findByProductAndWarehouse(product, warehouse);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getInventoryByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return inventoryRepository.findByProduct(product);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getInventoryByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        return inventoryRepository.findByWarehouse(warehouse);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getLowStockItems(Integer threshold) {
        return inventoryRepository.findLowStockItems(threshold);
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getItemsAtReorderPoint() {
        return inventoryRepository.findItemsAtReorderPoint();
    }
    
    @Transactional(readOnly = true)
    public List<InventoryItem> getInventoryByCategory(String category) {
        return inventoryRepository.findByProductCategory(category);
    }
    
    public InventoryItem createOrUpdateInventoryItem(Long productId, Long warehouseId, 
                                                   Integer quantity, Integer reorderPoint) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        
        InventoryItem item = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(new InventoryItem(product, warehouse));
        
        if (quantity != null) {
            item.setQuantityAvailable(quantity);
        }
        if (reorderPoint != null) {
            item.setReorderPoint(reorderPoint);
        }
        
        return inventoryRepository.save(item);
    }
    
    public StockMovement recordStockMovement(Long productId, Long warehouseId,
                                           MovementType movementType, MovementReason reason,
                                           Integer quantity, BigDecimal unitCost,
                                           String referenceNumber, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        
        // Create stock movement record
        StockMovement movement = new StockMovement(product, warehouse, movementType, reason, quantity);
        movement.setUnitCost(unitCost);
        movement.setReferenceNumber(referenceNumber);
        movement.setNotes(notes);
        
        // Update inventory levels
        InventoryItem inventoryItem = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(new InventoryItem(product, warehouse));
        
        int signedQuantity = movement.getSignedQuantity();
        inventoryItem.adjustQuantity(signedQuantity);
        
        // Validate that we don't go negative
        if (inventoryItem.getQuantityAvailable() < 0) {
            throw new IllegalStateException("Insufficient stock. Available: " + 
                    (inventoryItem.getQuantityAvailable() - signedQuantity) + 
                    ", Requested: " + quantity);
        }
        
        // Save both records
        inventoryRepository.save(inventoryItem);
        return movementRepository.save(movement);
    }
    
    @Transactional(readOnly = true)
    public List<StockMovement> getMovementHistory(Long productId) {
        return movementRepository.findRecentMovementsByProduct(productId);
    }
    
    @Transactional(readOnly = true)
    public List<StockMovement> getWarehouseMovements(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
        return movementRepository.findByWarehouse(warehouse);
    }
}