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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repairOrderRepository.findById(repairOrderId)
                .map(RepairOrderMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
    }

    @Transactional
    public void deleteById(long repairOrderId) {
        RepairOrderEntity repairOrder = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        workItemRepository.deleteAllByRepairOrderId(repairOrderId);
        repairOrderRepository.delete(repairOrder);
    }

    @Transactional
    public RepairOrderDto update(long repairOrderId, UpdateRepairOrderRequest updateRepairOrderRequest) {
        RepairOrderEntity entity = repairOrderRepository.findById(repairOrderId).orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
        entity.setIssueDescription(updateRepairOrderRequest.issueDescription());
        entity.setStatus(updateRepairOrderRequest.status());

        return RepairOrderMapper.toDto(repairOrderRepository.save(entity));
    }

    public Page<RepairOrderDto> getAll(Pageable pageable) {
        return repairOrderRepository.findAll(pageable).map(RepairOrderMapper::toDto);
    }
}
