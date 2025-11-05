package com.interview.service;

import jakarta.persistence.EntityNotFoundException;
import com.interview.model.Ingredient;
import com.interview.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all ingredients
     */
    public List<Ingredient> getAllIngredients() {
        return repository.findAll();
    }

    /**
     * Get a single ingredient by ID
     */
    public Ingredient getIngredientById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with id: " + id));
    }

    /**
     * Create a new ingredient
     */
    public Ingredient createIngredient(Ingredient ingredient) {
        ingredient.setLastUpdated(LocalDateTime.now());
        return repository.save(ingredient);
    }

    /**
     * Update an existing ingredient
     */
    public Ingredient updateIngredient(Long id, Ingredient ingredientDetails) {
        Ingredient ingredient = getIngredientById(id);
        
        ingredient.setName(ingredientDetails.getName());
        ingredient.setCategory(ingredientDetails.getCategory());
        ingredient.setQuantity(ingredientDetails.getQuantity());
        ingredient.setUnit(ingredientDetails.getUnit());
        ingredient.setMinimumStock(ingredientDetails.getMinimumStock());
        ingredient.setPricePerUnit(ingredientDetails.getPricePerUnit());
        ingredient.setSupplier(ingredientDetails.getSupplier());
        ingredient.setExpirationDate(ingredientDetails.getExpirationDate());
        ingredient.setLastUpdated(LocalDateTime.now());
        
        return repository.save(ingredient);
    }

    /**
     * Delete an ingredient
     */
    public void deleteIngredient(Long id) {
        Ingredient ingredient = getIngredientById(id);
        repository.delete(ingredient);
    }

    /**
     * Get ingredients by category
     */
    public List<Ingredient> getIngredientsByCategory(String category) {
        return repository.findByCategory(category);
    }

    /**
     * Get ingredients by supplier
     */
    public List<Ingredient> getIngredientsBySupplier(String supplier) {
        return repository.findBySupplier(supplier);
    }

    /**
     * Search ingredients by name (partial match, case-insensitive)
     */
    public List<Ingredient> searchIngredientsByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Get ingredients that are low in stock
     */
    public List<Ingredient> getLowStockIngredients() {
        return repository.findAll().stream()
                .filter(Ingredient::isLowStock)
                .collect(Collectors.toList());
    }
}
