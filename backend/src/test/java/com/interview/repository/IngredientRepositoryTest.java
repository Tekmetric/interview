package com.interview.repository;

import com.interview.model.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IngredientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredientRepository ingredientRepository;

    private Ingredient testIngredient1;
    private Ingredient testIngredient2;
    private Ingredient testIngredient3;

    @BeforeEach
    void setUp() {
        testIngredient1 = new Ingredient();
        testIngredient1.setName("Fresh Tomatoes");
        testIngredient1.setCategory("Vegetables");
        testIngredient1.setQuantity(25.0);
        testIngredient1.setUnit("kg");
        testIngredient1.setMinimumStock(10.0);
        testIngredient1.setPricePerUnit(new BigDecimal("3.50"));
        testIngredient1.setSupplier("Local Farm Direct");
        testIngredient1.setExpirationDate(LocalDate.of(2025, 12, 31));

        testIngredient2 = new Ingredient();
        testIngredient2.setName("Olive Oil");
        testIngredient2.setCategory("Oils");
        testIngredient2.setQuantity(50.0);
        testIngredient2.setUnit("liters");
        testIngredient2.setMinimumStock(10.0);
        testIngredient2.setPricePerUnit(new BigDecimal("8.50"));
        testIngredient2.setSupplier("Mediterranean Foods");
        testIngredient2.setExpirationDate(LocalDate.of(2025, 11, 30));

        testIngredient3 = new Ingredient();
        testIngredient3.setName("Cherry Tomatoes");
        testIngredient3.setCategory("Vegetables");
        testIngredient3.setQuantity(15.0);
        testIngredient3.setUnit("kg");
        testIngredient3.setMinimumStock(5.0);
        testIngredient3.setPricePerUnit(new BigDecimal("5.00"));
        testIngredient3.setSupplier("Local Farm Direct");
        testIngredient3.setExpirationDate(LocalDate.of(2025, 11, 15));

        entityManager.persist(testIngredient1);
        entityManager.persist(testIngredient2);
        entityManager.persist(testIngredient3);
        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnIngredient() {
        // Act
        Optional<Ingredient> found = ingredientRepository.findById(testIngredient1.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Fresh Tomatoes", found.get().getName());
    }

    @Test
    void findAll_ShouldReturnAllIngredients() {
        // Act
        List<Ingredient> ingredients = ingredientRepository.findAll();

        // Assert
        assertEquals(3, ingredients.size());
    }

    @Test
    void findByCategory_ShouldReturnIngredientsInCategory() {
        // Act
        List<Ingredient> vegetables = ingredientRepository.findByCategory("Vegetables");

        // Assert
        assertEquals(2, vegetables.size());
        assertTrue(vegetables.stream().allMatch(i -> i.getCategory().equals("Vegetables")));
    }

    @Test
    void findByCategory_WhenNoMatch_ShouldReturnEmptyList() {
        // Act
        List<Ingredient> result = ingredientRepository.findByCategory("NonExistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findBySupplier_ShouldReturnIngredientsFromSupplier() {
        // Act
        List<Ingredient> farmItems = ingredientRepository.findBySupplier("Local Farm Direct");

        // Assert
        assertEquals(2, farmItems.size());
        assertTrue(farmItems.stream().allMatch(i -> i.getSupplier().equals("Local Farm Direct")));
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingIngredients() {
        // Act
        List<Ingredient> tomatoItems = ingredientRepository.findByNameContainingIgnoreCase("tomato");

        // Assert
        assertEquals(2, tomatoItems.size());
        assertTrue(tomatoItems.stream()
                .allMatch(i -> i.getName().toLowerCase().contains("tomato")));
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldBeCaseInsensitive() {
        // Act
        List<Ingredient> result1 = ingredientRepository.findByNameContainingIgnoreCase("TOMATO");
        List<Ingredient> result2 = ingredientRepository.findByNameContainingIgnoreCase("tomato");
        List<Ingredient> result3 = ingredientRepository.findByNameContainingIgnoreCase("Tomato");

        // Assert
        assertEquals(result1.size(), result2.size());
        assertEquals(result2.size(), result3.size());
    }

    @Test
    void save_ShouldPersistNewIngredient() {
        // Arrange
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName("Garlic");
        newIngredient.setCategory("Vegetables");
        newIngredient.setQuantity(8.0);
        newIngredient.setUnit("kg");
        newIngredient.setMinimumStock(3.0);
        newIngredient.setPricePerUnit(new BigDecimal("4.25"));
        newIngredient.setSupplier("Vegetable Wholesale");
        newIngredient.setExpirationDate(LocalDate.of(2025, 11, 20));

        // Act
        Ingredient saved = ingredientRepository.save(newIngredient);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Garlic", saved.getName());
        
        Optional<Ingredient> found = ingredientRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void update_ShouldModifyExistingIngredient() {
        // Arrange
        Ingredient ingredient = ingredientRepository.findById(testIngredient1.getId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        ingredient.setQuantity(30.0);
        ingredient.setPricePerUnit(new BigDecimal("4.00"));

        // Act
        Ingredient updated = ingredientRepository.save(ingredient);

        // Assert
        assertEquals(30.0, updated.getQuantity());
        assertEquals(new BigDecimal("4.00"), updated.getPricePerUnit());
        
        Ingredient found = ingredientRepository.findById(testIngredient1.getId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        assertEquals(30.0, found.getQuantity());
    }

    @Test
    void delete_ShouldRemoveIngredient() {
        // Arrange
        Long id = testIngredient1.getId();

        // Act
        ingredientRepository.delete(testIngredient1);
        entityManager.flush();

        // Assert
        Optional<Ingredient> found = ingredientRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_ShouldRemoveIngredient() {
        // Arrange
        Long id = testIngredient2.getId();

        // Act
        ingredientRepository.deleteById(id);
        entityManager.flush();

        // Assert
        Optional<Ingredient> found = ingredientRepository.findById(id);
        assertFalse(found.isPresent());
    }
}
