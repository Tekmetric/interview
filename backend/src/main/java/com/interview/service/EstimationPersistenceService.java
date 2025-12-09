package com.interview.service;

import com.interview.dto.estimation.EstimationInfo;
import com.interview.dto.estimation.EstimationPdfInfo;
import com.interview.dto.workitem.WorkItemDto;
import com.interview.mapper.WorkItemMapper;
import com.interview.model.EstimationStatus;
import com.interview.model.RepairOrderStatus;
import com.interview.model.exception.EstimationStatusTransitionNotAllowedException;
import com.interview.model.exception.ResourceNotFoundException;
import com.interview.repository.RepairOrderRepository;
import com.interview.repository.model.RepairOrderEntity;
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
        log.info("Updating estimation status for repairOrderId: {} to status: {}", repairOrderId, estimationStatus);
        RepairOrderEntity repairOrderEntity = findRepairOrderOrThrow(repairOrderId);

        var status = repairOrderEntity.getEstimationStatus();
        if (status == EstimationStatus.IN_PROGRESS || status == EstimationStatus.COMPLETED) {
            throw new EstimationStatusTransitionNotAllowedException("Cannot transition from IN_PROGRESS or COMPLETED");
        }

        repairOrderEntity.setEstimationStatus(estimationStatus);
        repairOrderEntity.setStatus(RepairOrderStatus.AWAITING_CUSTOMER);

        repairOrderRepository.save(repairOrderEntity);
        log.info("Successfully updated estimation status for repairOrderId: {} to status: {}", repairOrderId, estimationStatus);
    }

    @Transactional
    public void markEstimationAsCompleted(long repairOrderId, String pdfName) {
        log.info("Marking estimation status to COMPLETED for repairOrderId: {}", repairOrderId);
        RepairOrderEntity repairOrderEntity = findRepairOrderOrThrow(repairOrderId);

        repairOrderEntity.setEstimationPdfObjectKey(pdfName);
        repairOrderEntity.setEstimationStatus(EstimationStatus.COMPLETED);

        repairOrderRepository.save(repairOrderEntity);
        log.info("Successfully marked estimation status to COMPLETED for repairOrderId: {}", repairOrderId);
    }

    @Transactional
    public void markEstimationAsFailed(long repairOrderId) {
        log.info("Marking estimation status to FAILED for repairOrderId: {}", repairOrderId);
        RepairOrderEntity repairOrderEntity = findRepairOrderOrThrow(repairOrderId);

        repairOrderEntity.setEstimationPdfObjectKey(null);
        repairOrderEntity.setEstimationStatus(EstimationStatus.FAILED);

        repairOrderRepository.save(repairOrderEntity);
        log.info("Successfully marked estimation status to FAILED for repairOrderId: {}", repairOrderId);
    }

    @Transactional(readOnly = true)
    public EstimationInfo getEstimationInfo(long repairOrderId) {
        log.info("Searching for the estimation for repairOrderId: {}", repairOrderId);
        RepairOrderEntity repairOrder = findRepairOrderOrThrow(repairOrderId);

        List<WorkItemDto> workItems = repairOrder.getWorkItems().stream().map(WorkItemMapper::toDto).toList();

        return new EstimationInfo(repairOrder.getVin(), workItems);
    }

    @Transactional(readOnly = true)
    public EstimationPdfInfo getEstimationPdfStatus(long repairOrderId) {
        RepairOrderEntity repairOrderEntity = findRepairOrderOrThrow(repairOrderId);

        return new EstimationPdfInfo(repairOrderEntity.getEstimationStatus(), repairOrderEntity.getEstimationPdfObjectKey());
    }

    private RepairOrderEntity findRepairOrderOrThrow(long repairOrderId) {
        return repairOrderRepository.findById(repairOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order with id: " + repairOrderId + " not found"));
    }

}
