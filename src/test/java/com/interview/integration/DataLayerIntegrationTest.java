package com.interview.integration;

import com.interview.entity.*;
import com.interview.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
@Transactional
@DisplayName("Data Layer Integration Tests with TestContainers")
class DataLayerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    private Product testProduct1;
    private Product testProduct2;
    private Warehouse warehouse1;
    private Warehouse warehouse2;

    @BeforeEach
    void setUp() {
        // Clean up in proper order due to foreign key constraints
        stockMovementRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        productRepository.deleteAll();
        warehouseRepository.deleteAll();

        // Create test products
        testProduct1 = new Product("INT-001", "Integration Test Product 1", "Electronics", "each", new BigDecimal("99.99"));
        testProduct2 = new Product("INT-002", "Integration Test Product 2", "Software", "license", new BigDecimal("199.99"));
        testProduct1 = productRepository.save(testProduct1);
        testProduct2 = productRepository.save(testProduct2);

        // Create test warehouses
        warehouse1 = new Warehouse("Main Warehouse", "123 Main St");
        warehouse2 = new Warehouse("Secondary Warehouse", "456 Oak Ave");
        warehouse1 = warehouseRepository.save(warehouse1);
        warehouse2 = warehouseRepository.save(warehouse2);
    }

    @Test
    @DisplayName("Should persist and retrieve products with all relationships")
    void shouldPersistAndRetrieveProductsWithRelationships() {
        // Create inventory items
        InventoryItem item1 = new InventoryItem(testProduct1, warehouse1);
        item1.setQuantityAvailable(100);
        item1.setQuantityReserved(10);
        item1.setReorderPoint(20);
        inventoryItemRepository.save(item1);

        InventoryItem item2 = new InventoryItem(testProduct2, warehouse2);
        item2.setQuantityAvailable(50);
        item2.setQuantityReserved(5);
        item2.setReorderPoint(15);
        inventoryItemRepository.save(item2);

        // Create stock movements
        StockMovement movement1 = new StockMovement(testProduct1, warehouse1, MovementType.IN, MovementReason.PURCHASE, 100);
        movement1.setUnitCost(BigDecimal.valueOf(99.99));
        movement1.setReferenceNumber("PO-001");
        movement1.setCreatedAt(LocalDateTime.now().minusDays(1));
        stockMovementRepository.save(movement1);

        StockMovement movement2 = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 10);
        movement2.setUnitCost(BigDecimal.valueOf(99.99));
        movement2.setReferenceNumber("SO-001");
        movement2.setCreatedAt(LocalDateTime.now().minusHours(1));
        stockMovementRepository.save(movement2);

        // Verify products are persisted correctly
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getSku).containsExactlyInAnyOrder("INT-001", "INT-002");

        // Verify inventory items are linked correctly
        List<InventoryItem> inventoryItems = inventoryItemRepository.findAll();
        assertThat(inventoryItems).hasSize(2);
        
        InventoryItem retrievedItem1 = inventoryItemRepository.findByProductAndWarehouse(testProduct1, warehouse1).orElse(null);
        assertThat(retrievedItem1).isNotNull();
        assertThat(retrievedItem1.getQuantityAvailable()).isEqualTo(100);
        assertThat(retrievedItem1.getQuantityReserved()).isEqualTo(10);

        // Verify stock movements are linked correctly
        List<StockMovement> movements = stockMovementRepository.findRecentMovementsByProduct(testProduct1.getId());
        assertThat(movements).hasSize(2);
        assertThat(movements).extracting(StockMovement::getReferenceNumber)
                .containsExactlyInAnyOrder("PO-001", "SO-001");
    }

    @Test
    @DisplayName("Should handle complex queries across multiple tables")
    void shouldHandleComplexQueriesAcrossMultipleTables() {
        // Setup complex test data
        InventoryItem lowStockItem = new InventoryItem(testProduct1, warehouse1);
        lowStockItem.setQuantityAvailable(5);
        lowStockItem.setReorderPoint(10);
        inventoryItemRepository.save(lowStockItem);

        InventoryItem normalStockItem = new InventoryItem(testProduct2, warehouse2);
        normalStockItem.setQuantityAvailable(50);
        normalStockItem.setReorderPoint(20);
        inventoryItemRepository.save(normalStockItem);

        // Test low stock detection using reorder point
        List<InventoryItem> lowStockItems = inventoryItemRepository.findItemsAtReorderPoint();
        assertThat(lowStockItems).hasSize(1);
        assertThat(lowStockItems.get(0).getProduct().getSku()).isEqualTo("INT-001");

        // Test product search functionality
        List<Product> searchResults = productRepository.searchProducts("Integration", null, null);
        assertThat(searchResults).hasSize(2);

        // Test category filtering
        List<Product> electronicsProducts = productRepository.searchProducts(null, "Electronics", null);
        assertThat(electronicsProducts).hasSize(1);
        assertThat(electronicsProducts.get(0).getSku()).isEqualTo("INT-001");

        // Test active products only
        testProduct2.setActive(false);
        productRepository.save(testProduct2);

        List<Product> activeProducts = productRepository.findByActiveTrue();
        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.get(0).getSku()).isEqualTo("INT-001");
    }

    @Test
    @DisplayName("Should maintain data consistency with transactions")
    void shouldMaintainDataConsistencyWithTransactions() {
        // Create initial inventory
        InventoryItem item = new InventoryItem(testProduct1, warehouse1);
        item.setQuantityAvailable(100);
        item = inventoryItemRepository.save(item);

        // Create multiple stock movements in sequence
        StockMovement outMovement1 = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 20);
        outMovement1.setReferenceNumber("SO-001");
        stockMovementRepository.save(outMovement1);

        StockMovement outMovement2 = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 30);
        outMovement2.setReferenceNumber("SO-002");
        stockMovementRepository.save(outMovement2);

        StockMovement inMovement = new StockMovement(testProduct1, warehouse1, MovementType.IN, MovementReason.PURCHASE, 50);
        inMovement.setReferenceNumber("PO-002");
        stockMovementRepository.save(inMovement);

        // Verify all movements are persisted
        List<StockMovement> allMovements = stockMovementRepository.findRecentMovementsByProduct(testProduct1.getId());
        assertThat(allMovements).hasSize(3);

        // Verify movement types and quantities
        int totalIn = allMovements.stream()
                .filter(m -> m.getMovementType() == MovementType.IN)
                .mapToInt(StockMovement::getQuantity)
                .sum();
        
        int totalOut = allMovements.stream()
                .filter(m -> m.getMovementType() == MovementType.OUT)
                .mapToInt(StockMovement::getQuantity)
                .sum();

        assertThat(totalIn).isEqualTo(50);
        assertThat(totalOut).isEqualTo(50);
    }

    @Test
    @DisplayName("Should handle date range queries correctly")
    void shouldHandleDateRangeQueriesCorrectly() {
        LocalDateTime baseTime = LocalDateTime.now();
        
        // Create movements at different times
        StockMovement oldMovement = new StockMovement(testProduct1, warehouse1, MovementType.IN, MovementReason.PURCHASE, 100);
        oldMovement.setReferenceNumber("OLD-001");
        oldMovement.setCreatedAt(baseTime.minusDays(30));
        stockMovementRepository.save(oldMovement);

        StockMovement recentMovement1 = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 10);
        recentMovement1.setReferenceNumber("RECENT-001");
        recentMovement1.setCreatedAt(baseTime.minusHours(5));
        stockMovementRepository.save(recentMovement1);

        StockMovement recentMovement2 = new StockMovement(testProduct1, warehouse1, MovementType.OUT, MovementReason.SALE, 5);
        recentMovement2.setReferenceNumber("RECENT-002");
        recentMovement2.setCreatedAt(baseTime.minusHours(2));
        stockMovementRepository.save(recentMovement2);

        // Test date range query
        LocalDateTime startDate = baseTime.minusHours(12);
        LocalDateTime endDate = baseTime.plusHours(1);
        
        List<StockMovement> recentMovements = stockMovementRepository.findByCreatedAtBetween(startDate, endDate);
        
        assertThat(recentMovements).hasSize(2);
        assertThat(recentMovements).extracting(StockMovement::getReferenceNumber)
                .containsExactlyInAnyOrder("RECENT-001", "RECENT-002");
        assertThat(recentMovements).allMatch(movement -> 
                movement.getCreatedAt().isAfter(startDate) && movement.getCreatedAt().isBefore(endDate));
    }

}