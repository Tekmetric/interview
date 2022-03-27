package com.interview.service;

import com.interview.controller.exception.InvalidInventoryIdsException;
import com.interview.controller.exception.InvalidInventoryQuantityException;
import com.interview.controller.exception.InvalidInventoryUpdateRequestException;
import com.interview.controller.exception.ResourceNotFoundException;
import com.interview.controller.payloads.InsertInventoryRequestPayload;
import com.interview.controller.payloads.InventoryFiltersPayload;
import com.interview.controller.payloads.InventoryResponsePayload;
import com.interview.controller.payloads.UpdateInventoryRequestPayload;
import com.interview.entity.Inventory;
import com.interview.enums.InventoryStatus;
import com.interview.repository.InventoryRepository;
import com.interview.repository.specs.InventorySpecification;
import com.interview.repository.specs.SearchCriteria;
import com.interview.repository.specs.SearchOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponsePayload createInventory(InsertInventoryRequestPayload request) {
        checkQuantityIsValid(request.getQuantity());
        Inventory created = inventoryRepository.save(
                new Inventory(
                        request.getType(), getInventoryStatus(request.isStatus()), request.getBrand(),
                        request.getPartName(), request.getPartNumber(), request.getQuantity(), request.getSupportEmail()
                )
        );

        return new InventoryResponsePayload(created);
    }

    @Transactional
    public InventoryResponsePayload updateInventory(Long id, UpdateInventoryRequestPayload updateRequest) {
        checkUpdateInventoryRequestPayloadIsValid(updateRequest);
        checkQuantityIsValid(updateRequest.getQuantity());
        Inventory toBeUpdated = checkAndGetInventoryIfExist(id);
        Inventory updatedInventory = getUpdatedInventory(toBeUpdated, updateRequest);
        Inventory updated = inventoryRepository.save(updatedInventory);
        return new InventoryResponsePayload(updated);
    }

    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = checkAndGetInventoryIfExist(id);
        inventory.setDeletedAt(Instant.now());
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void deleteInventories(Long[] ids) {
        checkInventoryIds(ids);
        List<Inventory> inventories = inventoryRepository.findAllByIdInAndDeletedAtIsNull(Arrays.asList(ids));
        inventories.forEach(
                inventory -> inventory.setDeletedAt(Instant.now())
        );
        inventoryRepository.saveAll(inventories);
    }

    private void checkInventoryIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new InvalidInventoryIdsException("invalid.inventory.ids", "inventory id list should not be empty");
        }
    }

    @Transactional(readOnly = true)
    public InventoryResponsePayload getInventory(Long id) {
        Inventory inventory = checkAndGetInventoryIfExist(id);
        return new InventoryResponsePayload(inventory);
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponsePayload> getAllInventories(Pageable pageable, InventoryFiltersPayload filters) {
        Page<InventoryResponsePayload> inventories;
        if (filters == null || filters.filtersIsEmpty()) {
            inventories = inventoryRepository.findAllByDeletedAtIsNull(pageable).map(
                    InventoryResponsePayload::new
            );
        } else {
            InventorySpecification specification = getSpecification(filters);
            inventories = inventoryRepository.findAll(specification, pageable).map(
                    InventoryResponsePayload::new
            );
        }
        return inventories;
    }

    private InventorySpecification getSpecification(InventoryFiltersPayload filters) {
        InventorySpecification specification = new InventorySpecification();
        if (filters.getPartName() != null && !filters.getPartName().trim().isEmpty()) {
            specification.add(new SearchCriteria("partName", filters.getPartName().trim(), SearchOperation.MATCH));
        }
        if (filters.getPartNumber() != null && !filters.getPartNumber().trim().isEmpty()) {
            specification.add(new SearchCriteria("partNumber", filters.getPartNumber().trim(), SearchOperation.MATCH));
        }
        if (filters.getBrand() != null && !filters.getBrand().trim().isEmpty()) {
            specification.add(new SearchCriteria("brand", filters.getBrand().trim(), SearchOperation.MATCH));
        }
        if (filters.getSupportEmail() != null && !filters.getSupportEmail().trim().isEmpty()) {
            specification.add(new SearchCriteria("supportEmail", filters.getSupportEmail().trim(), SearchOperation.MATCH));
        }
        if (filters.getStatus() != null) {
            InventoryStatus status = getInventoryStatus(filters.getStatus());
            specification.add(new SearchCriteria("status", status, SearchOperation.EQUAL));
        }

        specification.add(new SearchCriteria("deletedAt", null, SearchOperation.IS_NULL));
        return specification;
    }

    private Inventory checkAndGetInventoryIfExist(Long id) {
        return inventoryRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new ResourceNotFoundException("Inventory could not be found")
        );
    }

    private Inventory getUpdatedInventory(Inventory toBeUpdated, UpdateInventoryRequestPayload request) {
        toBeUpdated.setType(request.getType() == null ? toBeUpdated.getType() : request.getType());
        toBeUpdated.setStatus(
                request.getStatus() == null ? toBeUpdated.getStatus() : getInventoryStatus(request.getStatus())
        );
        toBeUpdated.setBrand(request.getBrand() == null ? toBeUpdated.getBrand() : request.getBrand());
        toBeUpdated.setPartName(request.getPartName() == null ? toBeUpdated.getPartName() : request.getPartName());
        toBeUpdated.setPartNumber(request.getPartNumber() == null ? toBeUpdated.getPartNumber() : request.getPartNumber());
        toBeUpdated.setQuantity(request.getQuantity() == null ? toBeUpdated.getQuantity() : request.getQuantity());
        toBeUpdated.setSupportEmail(request.getSupportEmail() == null ? toBeUpdated.getSupportEmail() : request.getSupportEmail());
        return toBeUpdated;
    }

    private InventoryStatus getInventoryStatus(Boolean status) {
        return status ? InventoryStatus.AVAILABLE : InventoryStatus.NOT_AVAILABLE;
    }

    private void checkQuantityIsValid(Integer quantity) {
        // This is a sample business logic
        if (quantity != null && (quantity < 1 || quantity > 10000)) {
            throw new InvalidInventoryQuantityException(
                    "inventory.invalid.quantity", "Inventory quantity should be between 1 and 10000"
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
