package com.interview.service;

import jakarta.persistence.EntityNotFoundException;
import com.interview.model.Ingredient;
import com.interview.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository repository;

    @InjectMocks
    private IngredientService ingredientService;

    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testIngredient = new Ingredient();
        testIngredient.setId(1L);
        testIngredient.setName("Test Tomatoes");
        testIngredient.setCategory("Vegetables");
        testIngredient.setQuantity(25.0);
        testIngredient.setUnit("kg");
        testIngredient.setMinimumStock(10.0);
        testIngredient.setPricePerUnit(new BigDecimal("3.50"));
        testIngredient.setSupplier("Test Supplier");
        testIngredient.setExpirationDate(LocalDate.of(2025, 12, 31));
    }

    @Test
    void getAllIngredients_ShouldReturnAllIngredients() {
        // Arrange
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        ingredient2.setName("Test Onions");
        ingredient2.setCategory("Vegetables");
        ingredient2.setQuantity(15.0);
        ingredient2.setUnit("kg");
        ingredient2.setMinimumStock(5.0);
        ingredient2.setPricePerUnit(new BigDecimal("2.00"));
        
        List<Ingredient> ingredients = Arrays.asList(testIngredient, ingredient2);
        when(repository.findAll()).thenReturn(ingredients);

        // Act
        List<Ingredient> result = ingredientService.getAllIngredients();

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getIngredientById_WhenExists_ShouldReturnIngredient() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testIngredient));

        // Act
        Ingredient result = ingredientService.getIngredientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Tomatoes", result.getName());
        assertEquals("Vegetables", result.getCategory());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getIngredientById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, 
            () -> ingredientService.getIngredientById(999L));
        verify(repository, times(1)).findById(999L);
    }

    @Test
    void createIngredient_ShouldSaveAndReturnIngredient() {
        // Arrange
        when(repository.save(any(Ingredient.class))).thenReturn(testIngredient);

        // Act
        Ingredient result = ingredientService.createIngredient(testIngredient);

        // Assert
        assertNotNull(result);
        assertEquals("Test Tomatoes", result.getName());
        verify(repository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        Ingredient updatedDetails = new Ingredient();
        updatedDetails.setName("Updated Tomatoes");
        updatedDetails.setCategory("Vegetables");
        updatedDetails.setQuantity(30.0);
        updatedDetails.setUnit("kg");
        updatedDetails.setMinimumStock(10.0);
        updatedDetails.setPricePerUnit(new BigDecimal("4.00"));
        updatedDetails.setSupplier("New Supplier");
        updatedDetails.setExpirationDate(LocalDate.of(2025, 11, 30));

        when(repository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(repository.save(any(Ingredient.class))).thenReturn(testIngredient);

        // Act
        Ingredient result = ingredientService.updateIngredient(1L, updatedDetails);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void updateIngredient_WhenNotExists_ShouldThrowException() {
        // Arrange
        Ingredient updatedDetails = new Ingredient();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, 
            () -> ingredientService.updateIngredient(999L, updatedDetails));
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(Ingredient.class));
    }

    @Test
    void deleteIngredient_WhenExists_ShouldDelete() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testIngredient));
        doNothing().when(repository).delete(any(Ingredient.class));

        // Act
        ingredientService.deleteIngredient(1L);

        // Assert
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(testIngredient);
    }

    @Test
    void deleteIngredient_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, 
            () -> ingredientService.deleteIngredient(999L));
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).delete(any(Ingredient.class));
    }

    @Test
    void getIngredientsByCategory_ShouldReturnFilteredList() {
        // Arrange
        List<Ingredient> vegetables = Arrays.asList(testIngredient);
        when(repository.findByCategory("Vegetables")).thenReturn(vegetables);

        // Act
        List<Ingredient> result = ingredientService.getIngredientsByCategory("Vegetables");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Vegetables", result.get(0).getCategory());
        verify(repository, times(1)).findByCategory("Vegetables");
    }

    @Test
    void getIngredientsBySupplier_ShouldReturnFilteredList() {
        // Arrange
        List<Ingredient> supplierItems = Arrays.asList(testIngredient);
        when(repository.findBySupplier("Test Supplier")).thenReturn(supplierItems);

        // Act
        List<Ingredient> result = ingredientService.getIngredientsBySupplier("Test Supplier");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Supplier", result.get(0).getSupplier());
        verify(repository, times(1)).findBySupplier("Test Supplier");
    }

    @Test
    void searchIngredientsByName_ShouldReturnMatchingItems() {
        // Arrange
        List<Ingredient> searchResults = Arrays.asList(testIngredient);
        when(repository.findByNameContainingIgnoreCase("tomato")).thenReturn(searchResults);

        // Act
        List<Ingredient> result = ingredientService.searchIngredientsByName("tomato");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("tomato"));
        verify(repository, times(1)).findByNameContainingIgnoreCase("tomato");
    }

    @Test
    void getLowStockIngredients_ShouldReturnOnlyLowStockItems() {
        // Arrange
        Ingredient lowStockItem = new Ingredient();
        lowStockItem.setId(2L);
        lowStockItem.setName("Low Stock Item");
        lowStockItem.setCategory("Vegetables");
        lowStockItem.setQuantity(3.0);
        lowStockItem.setUnit("kg");
        lowStockItem.setMinimumStock(5.0);
        lowStockItem.setPricePerUnit(new BigDecimal("2.00"));

        List<Ingredient> allIngredients = Arrays.asList(testIngredient, lowStockItem);
        when(repository.findAll()).thenReturn(allIngredients);

        // Act
        List<Ingredient> result = ingredientService.getLowStockIngredients();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isLowStock());
        verify(repository, times(1)).findAll();
    }
}
