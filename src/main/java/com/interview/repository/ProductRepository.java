package com.interview.repository;

import com.interview.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByActiveTrue();
    
    List<Product> findByCategoryAndActiveTrue(String category);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true ORDER BY p.category")
    List<String> findAllActiveCategories();
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:active IS NULL OR p.active = :active)")
    List<Product> searchProducts(@Param("search") String search, 
                               @Param("category") String category,
                               @Param("active") Boolean active);
}