package com.interview.service;

import com.interview.dto.workitem.CreateWorkItemRequest;
import com.interview.dto.workitem.UpdateWorkItemRequest;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.mapper.WorkItemMapper;
import com.interview.model.exception.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.WorkItemRepository;
import com.interview.repository.model.RepairOrderEntity;
import com.interview.repository.model.WorkItemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final RepairOrderRepository repairOrderRepository;

    @Transactional
    public WorkItemDto create(long repairOrderId, CreateWorkItemRequest createRequest) {
        log.info("Creating a work item for repairOrderId: {}", repairOrderId);
        RepairOrderEntity repairOrder = findRepairOrderByIdOrThrowNotFound(repairOrderId);

        WorkItemEntity workItem = WorkItemMapper.toEntity(createRequest);
        repairOrder.addWorkItem(workItem);

        var savedItem = workItemRepository.save(workItem);
        log.info("Successfully created a work item for repair order with id: {}. Work item id: {}", repairOrderId, savedItem.getId());
        return WorkItemMapper.toDto(savedItem);
    }

    @Transactional
    public void deleteByRepairOrderIdAndItemId(long repairOrderId, long workItemId) {
        log.info("Soft deleting workItemId: {} for the repairOrderId: {}", workItemId, repairOrderId);
        RepairOrderEntity repairOrderRef = repairOrderRepository.getReferenceById(repairOrderId);
        int softDeletedCount = workItemRepository.softDeleteByRepairOrderIdAndWorkItemId(repairOrderRef, workItemId, Instant.now());
        if (softDeletedCount == 0) {
            throw new ResourceNotFoundException("Work item not found for id: " + workItemId + "within the repair order with id: " + repairOrderId);
        }
        log.info("Successfully soft deleted workItemId: {} for the repairOrderId: {}", workItemId, repairOrderId);
    }

    @Transactional
    public WorkItemDto update(long repairOrderId, long workItemId, UpdateWorkItemRequest updateRequest) {
        log.info("Updating workItemId: {} for the repairOrderId: {}", workItemId, repairOrderId);
        findRepairOrderByIdOrThrowNotFound(repairOrderId);
        WorkItemEntity workItem = findWorkItemByIdOrThrowNotFound(workItemId);
        workItem.setDescription(updateRequest.description());
        workItem.setPrice(updateRequest.price());
        var updatedWorkItem = workItemRepository.save(workItem);

        log.info("Successfully updated workItemId: {} for the repairOrderId: {}", workItemId, repairOrderId);
        return WorkItemMapper.toDto(updatedWorkItem);
    }

    public Page<WorkItemDto> getAll(long repairOrderId, Pageable pageable) {
        return workItemRepository.findByRepairOrderEntityIdAndDeletedFalse(repairOrderId, pageable).map(WorkItemMapper::toDto);
    }

    private RepairOrderEntity findRepairOrderByIdOrThrowNotFound(long repairOrderId) {
        return repairOrderRepository.findById(repairOrderId).orElseThrow(
                () -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
    }

    private WorkItemEntity findWorkItemByIdOrThrowNotFound(long workItemId) {
        return workItemRepository.findById(workItemId).orElseThrow(
                () -> new ResourceNotFoundException("Work item with id: " + workItemId + " not found"));
    }
}
