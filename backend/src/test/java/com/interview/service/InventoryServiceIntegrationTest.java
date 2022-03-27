package com.interview.service;

import com.interview.Application;
import com.interview.controller.exception.InvalidInventoryIdsException;
import com.interview.controller.exception.InvalidInventoryQuantityException;
import com.interview.controller.exception.InvalidInventoryUpdateRequestException;
import com.interview.controller.exception.ResourceNotFoundException;
import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.entity.Inventory;
import com.interview.enums.InventoryStatus;
import com.interview.enums.InventoryType;
import com.interview.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class InventoryServiceIntegrationTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    private int INVALID_QUANTITY_NUMBER = 1000000;

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
    void assertThatInsertInventoryShouldBeInsertedToDb() {
        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);

        assertThat(inventoryService.getInventory(response.getId()).getId()).isEqualTo(response.getId());
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
    void assertThatInvalidInventoryIdsExceptionIfInventoryIdsNotValid() {
        assertThatExceptionOfType(InvalidInventoryIdsException.class)
                .isThrownBy(() -> inventoryService.deleteInventories(new Long[]{}));
    }

    @Test
    void assertThatDeleteInventoryShouldDecreaseInventoriesCount() {
        List<InventoryResponsePayload> inventories = inventoryService.getAllInventories(Pageable.unpaged(), null).getContent();
        int initialInventoryCount = inventories.size();

        InsertInventoryRequestPayload createInventoryRequest = createInventoryRequestPayload();
        InventoryResponsePayload response = inventoryService.createInventory(createInventoryRequest);
        assertThat(inventoryRepository.findAll()).size().isEqualTo(initialInventoryCount + 1);

        inventoryService.deleteInventory(response.getId());
        assertThat(inventoryRepository.findAllByDeletedAtIsNull(Pageable.unpaged())).size().isEqualTo(initialInventoryCount);
    }

    private InsertInventoryRequestPayload createInventoryRequestPayload() {
        return new InsertInventoryRequestPayload(
                InventoryType.TYPE_A, InventoryStatus.AVAILABLE.isActive(), "brand",
                "part-name", "part-number", 100, "support@tekmetric.com"
        );
    }
}
