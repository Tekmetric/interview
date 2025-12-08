package com.interview.service;

import com.interview.dto.estimation.EstimationInfo;
import com.interview.dto.estimation.EstimationPdfInfo;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.model.EstimationStatus;
import com.interview.model.RepairOrderStatus;
import com.interview.model.exception.ResourceNotFoundException;
import com.interview.model.exception.EstimationStatusTransitionNotAllowedException;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.model.RepairOrderEntity;
import com.interview.repository.model.WorkItemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimationPersistenceService {

    private final RepairOrderRepository repairOrderRepository;

    @Transactional
    public void updateEstimationStatus(long repairOrderId, EstimationStatus estimationStatus) {
        RepairOrderEntity repairOrderEntity = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        var status = repairOrderEntity.getEstimationStatus();
        if (status == EstimationStatus.IN_PROGRESS || status == EstimationStatus.COMPLETED) {
            throw new EstimationStatusTransitionNotAllowedException("Cannot transition from IN_PROGRESS or COMPLETED");
        }

        repairOrderEntity.setEstimationStatus(estimationStatus);
        repairOrderEntity.setStatus(RepairOrderStatus.AWAITING_CUSTOMER);

        repairOrderRepository.save(repairOrderEntity);
    }

    @Transactional
    public void markEstimationAsCompleted(long repairOrderId, String pdfName) {
        RepairOrderEntity repairOrderEntity = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        repairOrderEntity.setEstimationPdfObjectKey(pdfName);
        repairOrderEntity.setEstimationStatus(EstimationStatus.COMPLETED);

        repairOrderRepository.save(repairOrderEntity);
    }

    @Transactional
    public void markEstimationAsFailed(long repairOrderId) {
        RepairOrderEntity repairOrderEntity = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        repairOrderEntity.setEstimationPdfObjectKey(null);
        repairOrderEntity.setEstimationStatus(EstimationStatus.FAILED);

        repairOrderRepository.save(repairOrderEntity);
    }

    @Transactional(readOnly = true)
    public EstimationInfo getEstimationInfo(long repairOrderId) {
        RepairOrderEntity repairOrderRef = repairOrderRepository.getReferenceById(repairOrderId);
        List<WorkItemDto> workItems = repairOrderRef.getWorkItems().stream().map(this::toDto).toList();

        return new EstimationInfo(repairOrderRef.getVin(), workItems);
    }

    @Transactional(readOnly = true)
    public EstimationPdfInfo getEstimationPdfStatus(long repairOrderId) {
        RepairOrderEntity repairOrderEntity = repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));

        return new EstimationPdfInfo(repairOrderEntity.getEstimationStatus(), repairOrderEntity.getEstimationPdfObjectKey());
    }

    private WorkItemDto toDto(WorkItemEntity entity) {
        return new WorkItemDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice()
        );
    }

}
