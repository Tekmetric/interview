package com.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dto.InventoryItemResponse;
import com.interview.dto.StockMovementCreateRequest;
import com.interview.dto.StockMovementResponse;
import com.interview.entity.*;
import com.interview.mapper.InventoryItemMapper;
import com.interview.mapper.StockMovementMapper;
import com.interview.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(InventoryController.class)
@WithMockUser
class InventoryControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private InventoryItemMapper inventoryItemMapper;

    @MockBean
    private StockMovementMapper stockMovementMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllInventory_shouldReturnInventoryList() throws Exception {
        // Given
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.getAllInventoryItems()).thenReturn(List.of(item));
        when(inventoryItemMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quantityAvailable").value(100))
                .andExpect(jsonPath("$[0].reorderPoint").value(10));
    }

    @Test
    void getInventoryByProduct_shouldReturnProductInventory() throws Exception {
        // Given
        Long productId = 1L;
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.getInventoryByProduct(productId)).thenReturn(List.of(item));
        when(inventoryItemMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getInventoryByWarehouse_shouldReturnWarehouseInventory() throws Exception {
        // Given
        Long warehouseId = 1L;
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.getInventoryByWarehouse(warehouseId)).thenReturn(List.of(item));
        when(inventoryItemMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory/warehouse/{warehouseId}", warehouseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getLowStockItems_shouldReturnLowStockItems() throws Exception {
        // Given
        Integer threshold = 5;
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.getLowStockItems(threshold)).thenReturn(List.of(item));
        when(inventoryItemMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory/low-stock")
                        .param("threshold", threshold.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getReorderAlerts_shouldReturnReorderAlerts() throws Exception {
        // Given
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.getItemsAtReorderPoint()).thenReturn(List.of(item));
        when(inventoryItemMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory/reorder-alerts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void recordStockMovement_shouldCreateMovement() throws Exception {
        // Given
        StockMovementCreateRequest request = new StockMovementCreateRequest(
                1L, 1L, "IN", "PURCHASE", 50, 
                new BigDecimal("25.00"), "REF123", "Test movement"
        );
        
        StockMovement movement = createTestStockMovement();
        StockMovementResponse response = createTestStockMovementResponse();
        
        when(inventoryService.recordStockMovement(
                eq(1L), eq(1L), eq(MovementType.IN), eq(MovementReason.PURCHASE),
                eq(50), eq(new BigDecimal("25.00")), eq("REF123"), eq("Test movement")))
                .thenReturn(movement);
        when(stockMovementMapper.toResponse(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/inventory/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void getProductMovementHistory_shouldReturnMovements() throws Exception {
        // Given
        Long productId = 1L;
        StockMovement movement = createTestStockMovement();
        StockMovementResponse response = createTestStockMovementResponse();
        
        when(inventoryService.getMovementHistory(productId)).thenReturn(List.of(movement));
        when(stockMovementMapper.toResponseList(any())).thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/api/inventory/movements/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void adjustInventory_shouldUpdateInventory() throws Exception {
        // Given
        Long productId = 1L;
        Long warehouseId = 1L;
        Integer quantity = 150;
        Integer reorderPoint = 20;
        
        InventoryItem item = createTestInventoryItem();
        InventoryItemResponse response = createTestInventoryItemResponse();
        
        when(inventoryService.createOrUpdateInventoryItem(productId, warehouseId, quantity, reorderPoint))
                .thenReturn(item);
        when(inventoryItemMapper.toResponse(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/api/inventory/adjust")
                        .param("productId", productId.toString())
                        .param("warehouseId", warehouseId.toString())
                        .param("quantity", quantity.toString())
                        .param("reorderPoint", reorderPoint.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    private InventoryItem createTestInventoryItem() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("TEST-001");
        product.setName("Test Product");
        
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Test Warehouse");
        
        InventoryItem item = new InventoryItem();
        item.setId(1L);
        item.setProduct(product);
        item.setWarehouse(warehouse);
        item.setQuantityAvailable(100);
        item.setReorderPoint(10);
        item.setLastMovementAt(LocalDateTime.now());
        
        return item;
    }

    private InventoryItemResponse createTestInventoryItemResponse() {
        return new InventoryItemResponse(
                1L, 1L, "TEST-001", "Test Product", "Electronics", "piece",
                1L, "Test Warehouse", "Main Location", 100, 0, 100, 10,
                false, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private StockMovement createTestStockMovement() {
        Product product = new Product();
        product.setId(1L);
        
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        
        StockMovement movement = new StockMovement();
        movement.setId(1L);
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(MovementType.IN);
        movement.setMovementReason(MovementReason.PURCHASE);
        movement.setQuantity(50);
        movement.setUnitCost(new BigDecimal("25.00"));
        movement.setReferenceNumber("REF123");
        movement.setNotes("Test movement");
        movement.setCreatedAt(LocalDateTime.now());
        
        return movement;
    }

    private StockMovementResponse createTestStockMovementResponse() {
        return new StockMovementResponse(
                1L, 1L, "TEST-001", "Test Product", 1L, "Test Warehouse",
                "IN", "PURCHASE", 50, new BigDecimal("25.00"),
                "REF123", "Test movement", LocalDateTime.now()
        );
    }
}