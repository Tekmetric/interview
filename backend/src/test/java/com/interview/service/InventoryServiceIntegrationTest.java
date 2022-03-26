package com.interview.service;

import com.interview.controller.exception.InvalidInventoryQuantityException;
import com.interview.controller.exception.InvalidInventoryUpdateRequestException;
import com.interview.controller.exception.ResourceNotFoundException;
import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import com.interview.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InventoryServiceIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    private int INVALID_QUANTITY_NUMBER = 1000000;

    // test data is loading with load_data.xml liquibase script
    private int INITIAL_INVENTORY_COUNT = 120;

    @BeforeAll
    static void init() {
    }

    @BeforeEach
    void setup() {
    }


    @Test
    void assertThatInvalidInventoryQuantityExceptionIsThrownForInvalidQuantities() {
        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        createInventoryRequest.setQuantity(INVALID_QUANTITY_NUMBER);
        assertThatExceptionOfType(InvalidInventoryQuantityException.class)
                .isThrownBy(() -> inventoryService.createInventory(createInventoryRequest));
    }

    @Test
    void assertThatAllInventoriesShouldReturnInitialTestDataList() {
        List<InventoryResponsePayload> inventories = inventoryService.getAllInventories(Pageable.unpaged(), "").getContent();

        assertThat(inventories).isNotNull();
        assertThat(inventories).size().isEqualTo(INITIAL_INVENTORY_COUNT);
    }

    @Test
    void assertThatInsertInventoryShouldBeInsertedToDb() {
        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        assertThat(inventoryService.getAllInventories(Pageable.unpaged(), "")).size().isEqualTo(INITIAL_INVENTORY_COUNT);
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);
        List<InventoryResponsePayload> inventories = inventoryService.getAllInventories(Pageable.unpaged(), "").getContent();
        assertThat(inventories).size().isEqualTo(INITIAL_INVENTORY_COUNT + 1);

    }

    @Test
    void assertThatUpdateInventoryShouldBeUpdatedWithRequestedValue() {
        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);

        UpdateInventoryRequestPayload updateRequest = new UpdateInventoryRequestPayload();
        updateRequest.setQuantity(444);
        Long inventoryId = response.getId();
        InventoryResponsePayload updatedInventory = inventoryService.updateInventory(inventoryId, updateRequest);

        Assertions.assertEquals((int) updatedInventory.getQuantity(), 444);

    }

    @Test
    void assertThatInvalidUpdateRequestExceptionIfUpdateInventoryRequestIsInvalid() {
        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);

        UpdateInventoryRequestPayload updateRequest = new UpdateInventoryRequestPayload();
        Long inventoryId = response.getId();

        assertThatExceptionOfType(InvalidInventoryUpdateRequestException.class)
                .isThrownBy(() -> inventoryService.updateInventory(inventoryId, updateRequest));
    }

    @Test
    void assertThatResourceNotFoundExceptionIfInventoryNotExist() {
        long inventoryId = 0L;
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> inventoryService.deleteInventory(inventoryId));
    }

    @Test
    void assertThatDeleteInventoryShouldDecreaseInventoriesCount() {
        List<InventoryResponsePayload> inventories = inventoryService.getAllInventories(Pageable.unpaged(), "").getContent();
        int initialInventoryCount = inventories.size();

        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);
        assertThat(inventoryRepository.findAll()).size().isEqualTo(initialInventoryCount + 1);

        inventoryService.deleteInventory(response.getId());
        assertThat(inventoryRepository.findAll()).size().isEqualTo(initialInventoryCount);
    }

    private InsertInventoryRequestPayload createInventoryRequestPayload() {
        return new InsertInventoryRequestPayload(
                InventoryType.TYPE_A, InventoryStatus.AVAILABLE.isActive(), "brand",
                "part-name", "part-number", 100, "support@tekmetric.com"
        );
    }
}
