package com.interview.repository;

import com.interview.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
    // Custom query methods using Spring Data JPA naming conventions
    List<Ingredient> findByCategory(String category);
    
    List<Ingredient> findBySupplier(String supplier);
    
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}
