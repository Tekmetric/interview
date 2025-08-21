package com.interview.repository;

import com.interview.entity.InventoryItem;
import com.interview.entity.Product;
import com.interview.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByProductAndWarehouse(Product product, Warehouse warehouse);
    
    List<InventoryItem> findByProduct(Product product);
    
    List<InventoryItem> findByWarehouse(Warehouse warehouse);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantityAvailable <= :threshold")
    List<InventoryItem> findLowStockItems(@Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.reorderPoint IS NOT NULL " +
           "AND i.quantityAvailable <= i.reorderPoint")
    List<InventoryItem> findItemsAtReorderPoint();
    
    @Query("SELECT i FROM InventoryItem i JOIN i.product p WHERE " +
           "(:category IS NULL OR p.category = :category) " +
           "AND p.active = true")
    List<InventoryItem> findByProductCategory(@Param("category") String category);
}