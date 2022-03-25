package com.interview.service;

import com.interview.controller.exception.InvalidInventoryQuantityException;
import com.interview.controller.exception.InvalidInventoryUpdateRequestException;
import com.interview.controller.exception.ResourceNotFoundException;
import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.entity.Inventory;
import com.interview.repository.InventoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponsePayload createInventory(InsertInventoryRequestPayload createRequest) {
        checkCreateInventoryRequestPayloadIsValid(createRequest);
        Inventory created = inventoryRepository.save(
                new Inventory(
                        createRequest.getType(),
                        createRequest.getStatus(),
                        createRequest.getBrand(),
                        createRequest.getPartName(),
                        createRequest.getPartNumber(),
                        createRequest.getQuantity()
                )
        );

        return new InventoryResponsePayload(created);
    }

    @Transactional
    public InventoryResponsePayload updateInventory(Long id, UpdateInventoryRequestPayload updateRequest) {
        checkUpdateInventoryRequestPayloadIsValid(updateRequest);
        Inventory toBeUpdated = checkAndGetInventoryIfExist(id);
        Inventory updatedInventory = getUpdatedInventory(toBeUpdated, updateRequest);
        Inventory created = inventoryRepository.save(updatedInventory);
        return new InventoryResponsePayload(created);
    }

    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = checkAndGetInventoryIfExist(id);
        inventoryRepository.deleteById(inventory.getId());
    }

    @Transactional(readOnly = true)
    public InventoryResponsePayload getInventory(Long id) {
        Inventory inventory = checkAndGetInventoryIfExist(id);
        return new InventoryResponsePayload(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponsePayload> getAllInventories(Pageable pageable) {
        return inventoryRepository.findAllByDeletedAtIsNull(pageable).stream().map(
                InventoryResponsePayload::new
        ).collect(Collectors.toList());
    }

    private Inventory checkAndGetInventoryIfExist(Long id) {
        return inventoryRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new ResourceNotFoundException("Inventory could not be found")
        );
    }

    private Inventory getUpdatedInventory(Inventory toBeUpdated, UpdateInventoryRequestPayload updateRequest) {
        toBeUpdated.setType(updateRequest.getType() == null ? toBeUpdated.getType() : updateRequest.getType());
        toBeUpdated.setStatus(updateRequest.getStatus() == null ? toBeUpdated.getStatus() : updateRequest.getStatus());
        toBeUpdated.setBrand(updateRequest.getBrand() == null ? toBeUpdated.getBrand() : updateRequest.getBrand());
        toBeUpdated.setPartName(updateRequest.getPartName() == null ? toBeUpdated.getPartName() : updateRequest.getPartName());
        toBeUpdated.setPartNumber(updateRequest.getPartNumber() == null ? toBeUpdated.getPartNumber() : updateRequest.getPartNumber());
        toBeUpdated.setQuantity(updateRequest.getQuantity() == null ? toBeUpdated.getQuantity() : updateRequest.getQuantity());
        return toBeUpdated;
    }

    private void checkCreateInventoryRequestPayloadIsValid(InsertInventoryRequestPayload createRequest) {
        if (createRequest.getQuantity() < 1 || createRequest.getQuantity() > 1000) {
            throw new InvalidInventoryQuantityException(
                    "inventory.invalid.quantity", "Inventory quantity should be between 1 and 1000"
            );
        }
    }

    private void checkUpdateInventoryRequestPayloadIsValid(UpdateInventoryRequestPayload updateRequest) {
        if (updateRequest.isEmptyRequest()) {
            throw new InvalidInventoryUpdateRequestException(
                    "inventory.invalid.update.request", "Inventory update requests can not be empty"
            );
        }
    }

}
