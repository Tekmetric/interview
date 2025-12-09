package com.interview.service;

import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import com.interview.mapper.RepairOrderMapper;
import com.interview.model.exception.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.WorkItemRepository;
import com.interview.repository.model.RepairOrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final WorkItemRepository workItemRepository;

    public RepairOrderDto create(CreateRepairOrderRequest createRepairOrderRequest) {
        RepairOrderEntity entity = RepairOrderMapper.toEntity(createRepairOrderRequest);

        var saved = repairOrderRepository.save(entity);
        return RepairOrderMapper.toDto(saved);
    }

    public RepairOrderDto findById(long repairOrderId) {
        log.info("Searching for repair order with id: {}", repairOrderId);
        return repairOrderRepository.findById(repairOrderId)
                .map(RepairOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
    }

    @Transactional
    public void deleteById(long repairOrderId) {
        log.info("Deleting repair order with id: {}", repairOrderId);
        RepairOrderEntity repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        workItemRepository.deleteAllByRepairOrderId(repairOrderId);
        repairOrderRepository.delete(repairOrder);
        log.info("Successfully deleted repair order with id: {}", repairOrderId);
    }

    @Transactional
    public RepairOrderDto update(long repairOrderId, UpdateRepairOrderRequest updateRepairOrderRequest) {
        log.info("Updating repairOrderId: {}", repairOrderId);
        RepairOrderEntity entity = repairOrderRepository.findById(repairOrderId).orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
        entity.setIssueDescription(updateRepairOrderRequest.issueDescription());

        return RepairOrderMapper.toDto(repairOrderRepository.save(entity));
    }

    public Page<RepairOrderDto> getAll(Pageable pageable) {
        return repairOrderRepository.findAll(pageable).map(RepairOrderMapper::toDto);
    }
}
