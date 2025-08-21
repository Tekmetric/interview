package com.interview.mapper;

import com.interview.dto.StockMovementResponse;
import com.interview.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Stock Movement Mapper Tests")
class StockMovementMapperTest {

    private StockMovementMapper stockMovementMapper;
    private Product testProduct;
    private Warehouse testWarehouse;
    private StockMovement testStockMovement;

    @BeforeEach
    void setUp() {
        stockMovementMapper = Mappers.getMapper(StockMovementMapper.class);

        testProduct = new Product("TEST-001", "Test Product", "Electronics", "each", new BigDecimal("99.99"));
        testProduct.setId(1L);

        testWarehouse = new Warehouse("Test Warehouse", "Test Location");
        testWarehouse.setId(10L);

        testStockMovement = new StockMovement(testProduct, testWarehouse, MovementType.IN, MovementReason.PURCHASE, 100);
        testStockMovement.setId(1000L);
        testStockMovement.setUnitCost(BigDecimal.valueOf(99.99));
        testStockMovement.setReferenceNumber("PO-2024-001");
        testStockMovement.setNotes("Initial stock purchase for new product");
        testStockMovement.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    @Test
    @DisplayName("Should map StockMovement to StockMovementResponse")
    void shouldMapStockMovementToResponse() {
        // When
        StockMovementResponse result = stockMovementMapper.toResponse(testStockMovement);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1000L);
        
        // Product mapping
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productSku()).isEqualTo("TEST-001");
        assertThat(result.productName()).isEqualTo("Test Product");
        
        // Warehouse mapping
        assertThat(result.warehouseId()).isEqualTo(10L);
        assertThat(result.warehouseName()).isEqualTo("Test Warehouse");
        
        // Movement fields
        assertThat(result.movementType()).isEqualTo("IN");
        assertThat(result.movementReason()).isEqualTo("PURCHASE");
        assertThat(result.quantity()).isEqualTo(100);
        assertThat(result.unitCost()).isEqualTo(BigDecimal.valueOf(99.99));
        assertThat(result.referenceNumber()).isEqualTo("PO-2024-001");
        assertThat(result.notes()).isEqualTo("Initial stock purchase for new product");
        assertThat(result.createdAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    @Test
    @DisplayName("Should map OUT movement with SALE reason")
    void shouldMapOutMovementWithSaleReason() {
        // Given
        StockMovement outMovement = new StockMovement(testProduct, testWarehouse, MovementType.OUT, MovementReason.SALE, 25);
        outMovement.setId(2000L);
        outMovement.setUnitCost(BigDecimal.valueOf(149.99));
        outMovement.setReferenceNumber("SO-2024-001");
        outMovement.setNotes("Sale to customer XYZ Corp");
        outMovement.setCreatedAt(LocalDateTime.of(2024, 1, 16, 14, 45));

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(outMovement);

        // Then
        assertThat(result.id()).isEqualTo(2000L);
        assertThat(result.movementType()).isEqualTo("OUT");
        assertThat(result.movementReason()).isEqualTo("SALE");
        assertThat(result.quantity()).isEqualTo(25);
        assertThat(result.unitCost()).isEqualTo(BigDecimal.valueOf(149.99));
        assertThat(result.referenceNumber()).isEqualTo("SO-2024-001");
        assertThat(result.notes()).isEqualTo("Sale to customer XYZ Corp");
    }

    @Test
    @DisplayName("Should map movement with ADJUSTMENT reason")
    void shouldMapMovementWithAdjustmentReason() {
        // Given
        StockMovement adjustment = new StockMovement(testProduct, testWarehouse, MovementType.IN, MovementReason.ADJUSTMENT, 5);
        adjustment.setId(3000L);
        adjustment.setReferenceNumber("ADJ-2024-001");
        adjustment.setNotes("Inventory count adjustment");
        adjustment.setCreatedAt(LocalDateTime.of(2024, 1, 17, 9, 15));

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(adjustment);

        // Then
        assertThat(result.movementType()).isEqualTo("IN");
        assertThat(result.movementReason()).isEqualTo("ADJUSTMENT");
        assertThat(result.quantity()).isEqualTo(5);
        assertThat(result.unitCost()).isNull(); // Not set for adjustments
        assertThat(result.referenceNumber()).isEqualTo("ADJ-2024-001");
        assertThat(result.notes()).isEqualTo("Inventory count adjustment");
    }

    @Test
    @DisplayName("Should handle movement with null optional fields")
    void shouldHandleMovementWithNullOptionalFields() {
        // Given
        StockMovement minimalMovement = new StockMovement(testProduct, testWarehouse, MovementType.OUT, MovementReason.DAMAGE, 3);
        minimalMovement.setId(4000L);
        minimalMovement.setCreatedAt(LocalDateTime.of(2024, 1, 18, 16, 20));
        // unitCost, referenceNumber, notes left as null

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(minimalMovement);

        // Then
        assertThat(result.id()).isEqualTo(4000L);
        assertThat(result.movementType()).isEqualTo("OUT");
        assertThat(result.movementReason()).isEqualTo("DAMAGE");
        assertThat(result.quantity()).isEqualTo(3);
        assertThat(result.unitCost()).isNull();
        assertThat(result.referenceNumber()).isNull();
        assertThat(result.notes()).isNull();
        assertThat(result.productSku()).isEqualTo("TEST-001"); // Product fields should still be mapped
        assertThat(result.warehouseName()).isEqualTo("Test Warehouse"); // Warehouse fields should still be mapped
    }

    @Test
    @DisplayName("Should map list of StockMovements to list of responses")
    void shouldMapStockMovementListToResponseList() {
        // Given
        Product product2 = new Product("TEST-002", "Test Product 2", "Office", "piece", new BigDecimal("49.99"));
        product2.setId(2L);
        
        Warehouse warehouse2 = new Warehouse("Warehouse 2", "Location 2");
        warehouse2.setId(20L);
        
        StockMovement movement2 = new StockMovement(product2, warehouse2, MovementType.OUT, MovementReason.TRANSFER, 15);
        movement2.setId(5000L);
        movement2.setReferenceNumber("TR-2024-001");
        movement2.setCreatedAt(LocalDateTime.of(2024, 1, 19, 11, 0));
        
        List<StockMovement> movements = List.of(testStockMovement, movement2);

        // When
        List<StockMovementResponse> result = stockMovementMapper.toResponseList(movements);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        StockMovementResponse response1 = result.get(0);
        assertThat(response1.id()).isEqualTo(1000L);
        assertThat(response1.productSku()).isEqualTo("TEST-001");
        assertThat(response1.warehouseName()).isEqualTo("Test Warehouse");
        assertThat(response1.movementType()).isEqualTo("IN");
        
        StockMovementResponse response2 = result.get(1);
        assertThat(response2.id()).isEqualTo(5000L);
        assertThat(response2.productSku()).isEqualTo("TEST-002");
        assertThat(response2.warehouseName()).isEqualTo("Warehouse 2");
        assertThat(response2.movementType()).isEqualTo("OUT");
    }

    @Test
    @DisplayName("Should handle null stock movement in mapping")
    void shouldHandleNullStockMovementInMapping() {
        // When
        StockMovementResponse result = stockMovementMapper.toResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle empty list mapping")
    void shouldHandleEmptyListMapping() {
        // Given
        List<StockMovement> emptyList = List.of();

        // When
        List<StockMovementResponse> result = stockMovementMapper.toResponseList(emptyList);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should preserve precision for unit cost values")
    void shouldPreservePrecisionForUnitCostValues() {
        // Given
        testStockMovement.setUnitCost(BigDecimal.valueOf(123.456));

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(testStockMovement);

        // Then
        assertThat(result.unitCost()).isEqualTo(BigDecimal.valueOf(123.456));
    }

    @Test
    @DisplayName("Should handle large quantities and costs")
    void shouldHandleLargeQuantitiesAndCosts() {
        // Given
        testStockMovement.setQuantity(999999);
        testStockMovement.setUnitCost(BigDecimal.valueOf(99999.99));

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(testStockMovement);

        // Then
        assertThat(result.quantity()).isEqualTo(999999);
        assertThat(result.unitCost()).isEqualTo(BigDecimal.valueOf(99999.99));
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        // Given
        Product specialProduct = new Product("SPECIAL-ÄÖÜ-001", "Product with Special Chars: àáâãäåæç & <script>", "Category/Subcategory", "unit", new BigDecimal("100.0"));
        specialProduct.setId(999L);
        
        Warehouse specialWarehouse = new Warehouse("Warehouse with Ümlaut & Special Chars", "123 Main St. Suite #100");
        specialWarehouse.setId(888L);
        
        StockMovement specialMovement = new StockMovement(specialProduct, specialWarehouse, MovementType.IN, MovementReason.PURCHASE, 50);
        specialMovement.setId(6000L);
        specialMovement.setReferenceNumber("PO-SPECIAL-äöü-2024");
        specialMovement.setNotes("Special order with ümlaut & characters: <test>");

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(specialMovement);

        // Then
        assertThat(result.productSku()).isEqualTo("SPECIAL-ÄÖÜ-001");
        assertThat(result.productName()).isEqualTo("Product with Special Chars: àáâãäåæç & <script>");
        assertThat(result.warehouseName()).isEqualTo("Warehouse with Ümlaut & Special Chars");
        assertThat(result.referenceNumber()).isEqualTo("PO-SPECIAL-äöü-2024");
        assertThat(result.notes()).isEqualTo("Special order with ümlaut & characters: <test>");
    }

    @Test
    @DisplayName("Should map all movement types and reasons correctly")
    void shouldMapAllMovementTypesAndReasonsCorrectly() {
        // Test different combinations
        StockMovement returnMovement = new StockMovement(testProduct, testWarehouse, MovementType.IN, MovementReason.RETURN, 10);
        returnMovement.setId(7000L);

        // When
        StockMovementResponse result = stockMovementMapper.toResponse(returnMovement);

        // Then
        assertThat(result.movementType()).isEqualTo("IN");
        assertThat(result.movementReason()).isEqualTo("RETURN");
        assertThat(result.quantity()).isEqualTo(10);
    }
}