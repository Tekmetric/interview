package com.interview.mapper;

import com.interview.dto.InventoryItemResponse;
import com.interview.entity.InventoryItem;
import com.interview.entity.Product;
import com.interview.entity.Warehouse;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Inventory Item Mapper Tests")
class InventoryItemMapperTest {

    private InventoryItemMapper inventoryItemMapper;
    private Product testProduct;
    private Warehouse testWarehouse;
    private InventoryItem testInventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItemMapper = Mappers.getMapper(InventoryItemMapper.class);

        testProduct = new Product("TEST-001", "Test Product", "Electronics", "each", new BigDecimal("99.99"));
        testProduct.setId(1L);

        testWarehouse = new Warehouse("Test Warehouse", "Test Location");
        testWarehouse.setId(10L);

        testInventoryItem = new InventoryItem(testProduct, testWarehouse);
        testInventoryItem.setId(100L);
        testInventoryItem.setQuantityAvailable(50);
        testInventoryItem.setQuantityReserved(10);
        testInventoryItem.setReorderPoint(20);
        testInventoryItem.setLastMovementAt(LocalDateTime.of(2024, 1, 15, 14, 30));
        testInventoryItem.setUpdatedAt(LocalDateTime.of(2024, 1, 16, 9, 0));
    }

    @Test
    @DisplayName("Should map InventoryItem to InventoryItemResponse")
    void shouldMapInventoryItemToResponse() {
        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        
        // Product mapping
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.productSku()).isEqualTo("TEST-001");
        assertThat(result.productName()).isEqualTo("Test Product");
        assertThat(result.productCategory()).isEqualTo("Electronics");
        assertThat(result.productUnit()).isEqualTo("each");
        
        // Warehouse mapping
        assertThat(result.warehouseId()).isEqualTo(10L);
        assertThat(result.warehouseName()).isEqualTo("Test Warehouse");
        assertThat(result.warehouseLocation()).isEqualTo("Test Location");
        
        // Inventory fields
        assertThat(result.quantityAvailable()).isEqualTo(50);
        assertThat(result.quantityReserved()).isEqualTo(10);
        assertThat(result.reorderPoint()).isEqualTo(20);
        assertThat(result.lastMovementAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 14, 30));
        assertThat(result.updatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 16, 9, 0));
        
        // Calculated fields (computed in DTO constructor)
        assertThat(result.totalQuantity()).isEqualTo(60); // 50 + 10
        assertThat(result.belowReorderPoint()).isFalse(); // 50 > 20
    }

    @Test
    @DisplayName("Should handle inventory item below reorder point")
    void shouldHandleInventoryItemBelowReorderPoint() {
        // Given
        testInventoryItem.setQuantityAvailable(15); // Below reorder point of 20
        testInventoryItem.setQuantityReserved(5);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result.quantityAvailable()).isEqualTo(15);
        assertThat(result.quantityReserved()).isEqualTo(5);
        assertThat(result.totalQuantity()).isEqualTo(20); // 15 + 5
        assertThat(result.belowReorderPoint()).isTrue(); // 15 <= 20
    }

    @Test
    @DisplayName("Should handle inventory item at reorder point")
    void shouldHandleInventoryItemAtReorderPoint() {
        // Given
        testInventoryItem.setQuantityAvailable(20); // Equal to reorder point
        testInventoryItem.setQuantityReserved(0);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result.quantityAvailable()).isEqualTo(20);
        assertThat(result.quantityReserved()).isEqualTo(0);
        assertThat(result.totalQuantity()).isEqualTo(20); // 20 + 0
        assertThat(result.belowReorderPoint()).isTrue(); // 20 <= 20
    }

    @Test
    @DisplayName("Should handle inventory item with null reorder point")
    void shouldHandleInventoryItemWithNullReorderPoint() {
        // Given
        testInventoryItem.setQuantityAvailable(10);
        testInventoryItem.setQuantityReserved(5);
        testInventoryItem.setReorderPoint(null);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result.reorderPoint()).isNull();
        assertThat(result.belowReorderPoint()).isFalse(); // Should be false when reorderPoint is null
        assertThat(result.totalQuantity()).isEqualTo(15); // 10 + 5
    }

    @Test
    @DisplayName("Should handle zero quantities")
    void shouldHandleZeroQuantities() {
        // Given
        testInventoryItem.setQuantityAvailable(0);
        testInventoryItem.setQuantityReserved(0);
        testInventoryItem.setReorderPoint(5);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result.quantityAvailable()).isEqualTo(0);
        assertThat(result.quantityReserved()).isEqualTo(0);
        assertThat(result.totalQuantity()).isEqualTo(0); // 0 + 0
        assertThat(result.belowReorderPoint()).isTrue(); // 0 <= 5
    }

    @Test
    @DisplayName("Should map list of InventoryItems to list of responses")
    void shouldMapInventoryItemListToResponseList() {
        // Given
        Product product2 = new Product("TEST-002", "Test Product 2", "Office", "piece", new BigDecimal("49.99"));
        product2.setId(2L);
        
        Warehouse warehouse2 = new Warehouse("Warehouse 2", "Location 2");
        warehouse2.setId(20L);
        
        InventoryItem item2 = new InventoryItem(product2, warehouse2);
        item2.setId(200L);
        item2.setQuantityAvailable(25);
        item2.setQuantityReserved(3);
        item2.setReorderPoint(10);
        
        List<InventoryItem> inventoryItems = List.of(testInventoryItem, item2);

        // When
        List<InventoryItemResponse> result = inventoryItemMapper.toResponseList(inventoryItems);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        InventoryItemResponse response1 = result.get(0);
        assertThat(response1.id()).isEqualTo(100L);
        assertThat(response1.productSku()).isEqualTo("TEST-001");
        assertThat(response1.warehouseName()).isEqualTo("Test Warehouse");
        
        InventoryItemResponse response2 = result.get(1);
        assertThat(response2.id()).isEqualTo(200L);
        assertThat(response2.productSku()).isEqualTo("TEST-002");
        assertThat(response2.warehouseName()).isEqualTo("Warehouse 2");
    }

    @Test
    @DisplayName("Should handle null inventory item in mapping")
    void shouldHandleNullInventoryItemInMapping() {
        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle empty list mapping")
    void shouldHandleEmptyListMapping() {
        // Given
        List<InventoryItem> emptyList = List.of();

        // When
        List<InventoryItemResponse> result = inventoryItemMapper.toResponseList(emptyList);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle inventory item with null datetime fields")
    void shouldHandleInventoryItemWithNullDatetimeFields() {
        // Given
        testInventoryItem.setLastMovementAt(null);
        testInventoryItem.setUpdatedAt(null);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(testInventoryItem);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.lastMovementAt()).isNull();
        assertThat(result.updatedAt()).isNull();
        // Other fields should still be mapped correctly
        assertThat(result.quantityAvailable()).isEqualTo(50);
        assertThat(result.productSku()).isEqualTo("TEST-001");
    }

    @Test
    @DisplayName("Should map complex nested product and warehouse data")
    void shouldMapComplexNestedProductAndWarehouseData() {
        // Given
        Product complexProduct = new Product("COMPLEX-SKU-123", "Complex Product Name with Special Chars àáâ", "Electronics/Mobile/Smartphones", "unit", new BigDecimal("999.99"));
        complexProduct.setId(999L);
        
        Warehouse complexWarehouse = new Warehouse("Main Distribution Center", "1234 Industrial Blvd, Suite 100, City, State 12345");
        complexWarehouse.setId(777L);
        
        InventoryItem complexItem = new InventoryItem(complexProduct, complexWarehouse);
        complexItem.setId(555L);
        complexItem.setQuantityAvailable(100);
        complexItem.setQuantityReserved(25);

        // When
        InventoryItemResponse result = inventoryItemMapper.toResponse(complexItem);

        // Then
        assertThat(result.productSku()).isEqualTo("COMPLEX-SKU-123");
        assertThat(result.productName()).isEqualTo("Complex Product Name with Special Chars àáâ");
        assertThat(result.productCategory()).isEqualTo("Electronics/Mobile/Smartphones");
        assertThat(result.warehouseName()).isEqualTo("Main Distribution Center");
        assertThat(result.warehouseLocation()).isEqualTo("1234 Industrial Blvd, Suite 100, City, State 12345");
        assertThat(result.totalQuantity()).isEqualTo(125); // 100 + 25
    }
}