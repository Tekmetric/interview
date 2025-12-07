package com.interview.service;

import com.interview.dto.repairorder.CreateRepairOrderRequest;
import com.interview.dto.repairorder.RepairOrderDto;
import com.interview.dto.repairorder.UpdateRepairOrderRequest;
import com.interview.model.RepairOrderStatus;
import com.interview.model.exception.EntityNotFoundException;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.model.RepairOrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;

    public RepairOrderDto create(CreateRepairOrderRequest createRepairOrderRequest) {
        RepairOrderEntity entity = toEntity(createRepairOrderRequest);

        var saved = repairOrderRepository.save(entity);
        return toDto(saved);
    }

    public RepairOrderDto findById(long repairOrderId) {
        return repairOrderRepository.findById(repairOrderId)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Repair order with id: " + repairOrderId + " not found"));
    }

    @Transactional
    public void deleteById(long repairOrderId) {
        int deletedCount = repairOrderRepository.deleteRepairOrderById(repairOrderId);
        if (deletedCount == 0) {
            throw new EntityNotFoundException("Repair order with id: " + repairOrderId + " not found");
        }
    }

    @Transactional
    public RepairOrderDto update(long repairOrderId, UpdateRepairOrderRequest updateRepairOrderRequest) {
        RepairOrderEntity entity = repairOrderRepository.findById(repairOrderId).orElseThrow(() -> new EntityNotFoundException("Repair order with id: " + repairOrderId + " not found"));
        entity.setIssueDescription(updateRepairOrderRequest.issueDescription());
        entity.setStatus(updateRepairOrderRequest.status());

        return toDto(repairOrderRepository.save(entity));
    }

    private RepairOrderDto toDto(RepairOrderEntity saved) {
        return RepairOrderDto.builder()
                .id(saved.getId())
                .vin(saved.getVin())
                .carModel(saved.getCarModel())
                .status(saved.getStatus())
                .issueDescription(saved.getIssueDescription())
                .build();
    }

    private RepairOrderEntity toEntity(CreateRepairOrderRequest createRepairOrderRequest) {
        return RepairOrderEntity.builder()
                .vin(createRepairOrderRequest.vin())
                .carModel(createRepairOrderRequest.carModel())
                .status(RepairOrderStatus.DRAFT)
                .deleted(false)
                .issueDescription(createRepairOrderRequest.issueDescription())
                .build();
    }

    public Page<RepairOrderDto> getAll(Pageable pageable) {
        return repairOrderRepository.findAll(pageable).map(this::toDto);
    }
}
