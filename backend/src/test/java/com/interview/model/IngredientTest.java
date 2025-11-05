package com.interview.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        ingredient = new Ingredient();
    }

    @Test
    void constructor_WithNoArgs_ShouldInitializeWithDefaults() {
        // Assert
        assertNotNull(ingredient);
        assertNotNull(ingredient.getLastUpdated());
    }

    @Test
    void constructor_WithAllArgs_ShouldSetAllFields() {
        // Arrange & Act
        LocalDate expirationDate = LocalDate.of(2025, 12, 31);
        Ingredient fullIngredient = new Ingredient(
                "Test Ingredient",
                "Test Category",
                50.0,
                "kg",
                10.0,
                new BigDecimal("5.00"),
                "Test Supplier",
                expirationDate
        );

        // Assert
        assertEquals("Test Ingredient", fullIngredient.getName());
        assertEquals("Test Category", fullIngredient.getCategory());
        assertEquals(50.0, fullIngredient.getQuantity());
        assertEquals("kg", fullIngredient.getUnit());
        assertEquals(10.0, fullIngredient.getMinimumStock());
        assertEquals(new BigDecimal("5.00"), fullIngredient.getPricePerUnit());
        assertEquals("Test Supplier", fullIngredient.getSupplier());
        assertEquals(expirationDate, fullIngredient.getExpirationDate());
        assertNotNull(fullIngredient.getLastUpdated());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        String name = "Tomatoes";
        String category = "Vegetables";
        Double quantity = 25.0;
        String unit = "kg";
        Double minimumStock = 10.0;
        BigDecimal price = new BigDecimal("3.50");
        String supplier = "Local Farm";
        LocalDate expirationDate = LocalDate.of(2025, 11, 15);
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setCategory(category);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setMinimumStock(minimumStock);
        ingredient.setPricePerUnit(price);
        ingredient.setSupplier(supplier);
        ingredient.setExpirationDate(expirationDate);
        ingredient.setLastUpdated(lastUpdated);

        // Assert
        assertEquals(id, ingredient.getId());
        assertEquals(name, ingredient.getName());
        assertEquals(category, ingredient.getCategory());
        assertEquals(quantity, ingredient.getQuantity());
        assertEquals(unit, ingredient.getUnit());
        assertEquals(minimumStock, ingredient.getMinimumStock());
        assertEquals(price, ingredient.getPricePerUnit());
        assertEquals(supplier, ingredient.getSupplier());
        assertEquals(expirationDate, ingredient.getExpirationDate());
        assertEquals(lastUpdated, ingredient.getLastUpdated());
    }

    @Test
    void isLowStock_WhenQuantityBelowMinimum_ShouldReturnTrue() {
        // Arrange
        ingredient.setQuantity(5.0);
        ingredient.setMinimumStock(10.0);

        // Act & Assert
        assertTrue(ingredient.isLowStock());
    }

    @Test
    void isLowStock_WhenQuantityEqualsMinimum_ShouldReturnTrue() {
        // Arrange
        ingredient.setQuantity(10.0);
        ingredient.setMinimumStock(10.0);

        // Act & Assert
        assertTrue(ingredient.isLowStock());
    }

    @Test
    void isLowStock_WhenQuantityAboveMinimum_ShouldReturnFalse() {
        // Arrange
        ingredient.setQuantity(15.0);
        ingredient.setMinimumStock(10.0);

        // Act & Assert
        assertFalse(ingredient.isLowStock());
    }

    @Test
    void preUpdate_ShouldUpdateLastUpdatedTimestamp() throws InterruptedException {
        // Arrange
        LocalDateTime originalTimestamp = LocalDateTime.now().minusDays(1);
        ingredient.setLastUpdated(originalTimestamp);
        
        // Small delay to ensure timestamp difference
        Thread.sleep(10);

        // Act
        ingredient.preUpdate();

        // Assert
        assertNotNull(ingredient.getLastUpdated());
        assertTrue(ingredient.getLastUpdated().isAfter(originalTimestamp));
    }

    @Test
    void pricePerUnit_ShouldHandleDecimalPrecision() {
        // Arrange
        BigDecimal price = new BigDecimal("12.50");

        // Act
        ingredient.setPricePerUnit(price);

        // Assert
        assertEquals(price, ingredient.getPricePerUnit());
        assertEquals(2, ingredient.getPricePerUnit().scale());
    }

    @Test
    void quantity_ShouldHandleDoubleValues() {
        // Arrange
        Double quantity1 = 10.5;
        Double quantity2 = 0.25;
        Double quantity3 = 100.0;

        // Act & Assert
        ingredient.setQuantity(quantity1);
        assertEquals(quantity1, ingredient.getQuantity());

        ingredient.setQuantity(quantity2);
        assertEquals(quantity2, ingredient.getQuantity());

        ingredient.setQuantity(quantity3);
        assertEquals(quantity3, ingredient.getQuantity());
    }

    @Test
    void expirationDate_ShouldHandleFutureDates() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusMonths(6);

        // Act
        ingredient.setExpirationDate(futureDate);

        // Assert
        assertEquals(futureDate, ingredient.getExpirationDate());
        assertTrue(ingredient.getExpirationDate().isAfter(LocalDate.now()));
    }

    @Test
    void expirationDate_ShouldHandlePastDates() {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(5);

        // Act
        ingredient.setExpirationDate(pastDate);

        // Assert
        assertEquals(pastDate, ingredient.getExpirationDate());
        assertTrue(ingredient.getExpirationDate().isBefore(LocalDate.now()));
    }

    @Test
    void supplier_ShouldHandleNullValue() {
        // Act
        ingredient.setSupplier(null);

        // Assert
        assertNull(ingredient.getSupplier());
    }

    @Test
    void expirationDate_ShouldHandleNullValue() {
        // Act
        ingredient.setExpirationDate(null);

        // Assert
        assertNull(ingredient.getExpirationDate());
    }
}
