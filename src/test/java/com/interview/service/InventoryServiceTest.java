package com.interview.service;

import com.interview.entity.*;
import com.interview.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Service Tests")
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product testProduct;
    private Warehouse testWarehouse;
    private InventoryItem testInventoryItem;

    @BeforeEach
    void setUp() {
        testProduct = new Product("TEST-001", "Test Product", "Electronics", "each", new BigDecimal("99.99"));
        testProduct.setId(1L);

        testWarehouse = new Warehouse("Test Warehouse", "Test Location");
        testWarehouse.setId(1L);

        testInventoryItem = new InventoryItem(testProduct, testWarehouse);
        testInventoryItem.setId(1L);
        testInventoryItem.setQuantityAvailable(50);
        testInventoryItem.setQuantityReserved(5);
        testInventoryItem.setReorderPoint(20);
        testInventoryItem.setLastMovementAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get all inventory items")
    void shouldGetAllInventoryItems() {
        // Given
        List<InventoryItem> inventoryItems = List.of(testInventoryItem);
        when(inventoryRepository.findAll()).thenReturn(inventoryItems);

        // When
        List<InventoryItem> result = inventoryService.getAllInventoryItems();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testInventoryItem);
        verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("Should get inventory item by product and warehouse")
    void shouldGetInventoryItemByProductAndWarehouse() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(inventoryRepository.findByProductAndWarehouse(testProduct, testWarehouse))
                .thenReturn(Optional.of(testInventoryItem));

        // When
        Optional<InventoryItem> result = inventoryService.getInventoryItem(1L, 1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testInventoryItem);
        verify(productRepository).findById(1L);
        verify(warehouseRepository).findById(1L);
        verify(inventoryRepository).findByProductAndWarehouse(testProduct, testWarehouse);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> inventoryService.getInventoryItem(999L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");

        verify(productRepository).findById(999L);
        verify(warehouseRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should get low stock items")
    void shouldGetLowStockItems() {
        // Given
        List<InventoryItem> lowStockItems = List.of(testInventoryItem);
        when(inventoryRepository.findLowStockItems(10)).thenReturn(lowStockItems);

        // When
        List<InventoryItem> result = inventoryService.getLowStockItems(10);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testInventoryItem);
        verify(inventoryRepository).findLowStockItems(10);
    }

    @Test
    @DisplayName("Should record stock movement successfully")
    void shouldRecordStockMovementSuccessfully() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(inventoryRepository.findByProductAndWarehouse(testProduct, testWarehouse))
                .thenReturn(Optional.of(testInventoryItem));
        
        StockMovement expectedMovement = new StockMovement(testProduct, testWarehouse, 
                MovementType.IN, MovementReason.PURCHASE, 10);
        when(movementRepository.save(any(StockMovement.class))).thenReturn(expectedMovement);
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);

        // When
        StockMovement result = inventoryService.recordStockMovement(
                1L, 1L, MovementType.IN, MovementReason.PURCHASE, 10,
                BigDecimal.valueOf(99.99), "PO-001", "Test purchase"
        );

        // Then
        assertThat(result).isEqualTo(expectedMovement);
        assertThat(testInventoryItem.getQuantityAvailable()).isEqualTo(60); // 50 + 10
        verify(productRepository).findById(1L);
        verify(warehouseRepository).findById(1L);
        verify(inventoryRepository).findByProductAndWarehouse(testProduct, testWarehouse);
        verify(inventoryRepository).save(testInventoryItem);
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock for OUT movement")
    void shouldThrowExceptionWhenInsufficientStockForOutMovement() {
        // Given
        testInventoryItem.setQuantityAvailable(5); // Low stock
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(inventoryRepository.findByProductAndWarehouse(testProduct, testWarehouse))
                .thenReturn(Optional.of(testInventoryItem));

        // When & Then
        assertThatThrownBy(() -> inventoryService.recordStockMovement(
                1L, 1L, MovementType.OUT, MovementReason.SALE, 10,
                BigDecimal.valueOf(99.99), "SO-001", "Test sale"
        )).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Insufficient stock");

        verify(movementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new inventory item when not exists")
    void shouldCreateNewInventoryItemWhenNotExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(inventoryRepository.findByProductAndWarehouse(testProduct, testWarehouse))
                .thenReturn(Optional.empty());
        
        InventoryItem newItem = new InventoryItem(testProduct, testWarehouse);
        newItem.setQuantityAvailable(10);
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(newItem);
        when(movementRepository.save(any(StockMovement.class))).thenReturn(new StockMovement());

        // When
        inventoryService.recordStockMovement(
                1L, 1L, MovementType.IN, MovementReason.PURCHASE, 10,
                BigDecimal.valueOf(99.99), "PO-001", "Initial stock"
        );

        // Then
        verify(inventoryRepository).save(any(InventoryItem.class));
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    @DisplayName("Should create or update inventory item")
    void shouldCreateOrUpdateInventoryItem() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(inventoryRepository.findByProductAndWarehouse(testProduct, testWarehouse))
                .thenReturn(Optional.of(testInventoryItem));
        when(inventoryRepository.save(any(InventoryItem.class))).thenReturn(testInventoryItem);

        // When
        InventoryItem result = inventoryService.createOrUpdateInventoryItem(1L, 1L, 100, 25);

        // Then
        assertThat(result).isEqualTo(testInventoryItem);
        assertThat(testInventoryItem.getQuantityAvailable()).isEqualTo(100);
        assertThat(testInventoryItem.getReorderPoint()).isEqualTo(25);
        verify(inventoryRepository).save(testInventoryItem);
    }

    @Test
    @DisplayName("Should get movement history for product")
    void shouldGetMovementHistoryForProduct() {
        // Given
        StockMovement movement = new StockMovement(testProduct, testWarehouse, 
                MovementType.IN, MovementReason.PURCHASE, 10);
        List<StockMovement> movements = List.of(movement);
        when(movementRepository.findRecentMovementsByProduct(1L)).thenReturn(movements);

        // When
        List<StockMovement> result = inventoryService.getMovementHistory(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(movement);
        verify(movementRepository).findRecentMovementsByProduct(1L);
    }

    @Test
    @DisplayName("Should get warehouse movements")
    void shouldGetWarehouseMovements() {
        // Given
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        StockMovement movement = new StockMovement(testProduct, testWarehouse, 
                MovementType.OUT, MovementReason.SALE, 5);
        List<StockMovement> movements = List.of(movement);
        when(movementRepository.findByWarehouse(testWarehouse)).thenReturn(movements);

        // When
        List<StockMovement> result = inventoryService.getWarehouseMovements(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(movement);
        verify(warehouseRepository).findById(1L);
        verify(movementRepository).findByWarehouse(testWarehouse);
    }
}