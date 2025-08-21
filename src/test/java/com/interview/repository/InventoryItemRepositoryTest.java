package com.interview.repository;

import com.interview.entity.InventoryItem;
import com.interview.entity.Product;
import com.interview.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Inventory Item Repository Tests")
class InventoryItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryItemRepository inventoryRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Warehouse warehouse1;
    private Warehouse warehouse2;
    private InventoryItem lowStockItem;
    private InventoryItem normalStockItem;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product("TEST-001", "Test Product 1", "Electronics", "each", new BigDecimal("99.99"));
        testProduct2 = new Product("TEST-002", "Test Product 2", "Electronics", "each", new BigDecimal("199.99"));
        
        warehouse1 = new Warehouse("Main Warehouse", "Location 1");
        warehouse2 = new Warehouse("Secondary Warehouse", "Location 2");

        entityManager.persistAndFlush(testProduct1);
        entityManager.persistAndFlush(testProduct2);
        entityManager.persistAndFlush(warehouse1);
        entityManager.persistAndFlush(warehouse2);

        // Low stock item (available: 15, reorder point: 20)
        lowStockItem = new InventoryItem(testProduct1, warehouse1);
        lowStockItem.setQuantityAvailable(15);
        lowStockItem.setQuantityReserved(5);
        lowStockItem.setReorderPoint(20);
        lowStockItem.setLastMovementAt(LocalDateTime.now().minusDays(1));

        // Normal stock item (available: 100, reorder point: 20)
        normalStockItem = new InventoryItem(testProduct2, warehouse1);
        normalStockItem.setQuantityAvailable(100);
        normalStockItem.setQuantityReserved(10);
        normalStockItem.setReorderPoint(20);
        normalStockItem.setLastMovementAt(LocalDateTime.now());

        entityManager.persistAndFlush(lowStockItem);
        entityManager.persistAndFlush(normalStockItem);
    }

    @Test
    @DisplayName("Should find inventory item by product and warehouse")
    void shouldFindInventoryItemByProductAndWarehouse() {
        // When
        Optional<InventoryItem> result = inventoryRepository.findByProductAndWarehouse(testProduct1, warehouse1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getProduct()).isEqualTo(testProduct1);
        assertThat(result.get().getWarehouse()).isEqualTo(warehouse1);
        assertThat(result.get().getQuantityAvailable()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should return empty when inventory item not found")
    void shouldReturnEmptyWhenInventoryItemNotFound() {
        // When
        Optional<InventoryItem> result = inventoryRepository.findByProductAndWarehouse(testProduct2, warehouse2);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find low stock items")
    void shouldFindLowStockItems() {
        // When
        List<InventoryItem> result = inventoryRepository.findLowStockItems(25);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(lowStockItem);
        assertThat(result.get(0).getQuantityAvailable()).isLessThan(25);
    }

    @Test
    @DisplayName("Should not return items above threshold for low stock")
    void shouldNotReturnItemsAboveThresholdForLowStock() {
        // When
        List<InventoryItem> result = inventoryRepository.findLowStockItems(10);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find items at reorder point")
    void shouldFindItemsAtReorderPoint() {
        // When
        List<InventoryItem> result = inventoryRepository.findItemsAtReorderPoint();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(lowStockItem);
        assertThat(result.get(0).getQuantityAvailable()).isLessThanOrEqualTo(result.get(0).getReorderPoint());
    }

    @Test
    @DisplayName("Should find all inventory items by product")
    void shouldFindAllInventoryItemsByProduct() {
        // Create another inventory item for the same product in different warehouse
        InventoryItem anotherItem = new InventoryItem(testProduct1, warehouse2);
        anotherItem.setQuantityAvailable(50);
        anotherItem.setReorderPoint(10);
        entityManager.persistAndFlush(anotherItem);

        // When
        List<InventoryItem> result = inventoryRepository.findByProduct(testProduct1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(item -> item.getWarehouse().getId())
                .containsExactlyInAnyOrder(warehouse1.getId(), warehouse2.getId());
    }

    @Test
    @DisplayName("Should find all inventory items by warehouse")
    void shouldFindAllInventoryItemsByWarehouse() {
        // When
        List<InventoryItem> result = inventoryRepository.findByWarehouse(warehouse1);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(item -> item.getProduct().getSku())
                .containsExactlyInAnyOrder("TEST-001", "TEST-002");
    }

    @Test
    @DisplayName("Should find inventory items by product category")
    void shouldFindInventoryItemsByProductCategory() {
        // When
        List<InventoryItem> result = inventoryRepository.findByProductCategory("Electronics");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(item -> item.getProduct().getCategory())
                .containsOnly("Electronics");
    }

    @Test
    @DisplayName("Should find inventory items with null category filter")
    void shouldFindInventoryItemsWithNullCategoryFilter() {
        // When
        List<InventoryItem> result = inventoryRepository.findByProductCategory(null);

        // Then
        assertThat(result).hasSize(2); // All items since category filter is null
    }

    @Test
    @DisplayName("Should handle empty result for non-matching category")
    void shouldHandleEmptyResultForNonMatchingCategory() {
        // When
        List<InventoryItem> result = inventoryRepository.findByProductCategory("NonExistentCategory");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should verify inventory item persistence")
    void shouldVerifyInventoryItemPersistence() {
        // Create a new inventory item
        Product newProduct = new Product("NEW-001", "New Product", "Electronics", "each", new BigDecimal("50.00"));
        entityManager.persistAndFlush(newProduct);
        
        InventoryItem newItem = new InventoryItem(newProduct, warehouse2);
        newItem.setQuantityAvailable(75);
        newItem.setReorderPoint(15);
        
        // When
        InventoryItem saved = inventoryRepository.save(newItem);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getQuantityAvailable()).isEqualTo(75);
        assertThat(saved.getReorderPoint()).isEqualTo(15);
    }
}