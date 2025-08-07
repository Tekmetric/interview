package com.interview.repository;

import com.interview.entity.StockMovement;
import com.interview.entity.Product;
import com.interview.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByProduct(Product product);
    
    List<StockMovement> findByWarehouse(Warehouse warehouse);
    
    List<StockMovement> findByProductAndWarehouse(Product product, Warehouse warehouse);
    
    List<StockMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId " +
           "ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentMovementsByProduct(@Param("productId") Long productId);
}