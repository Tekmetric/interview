package com.interview.service;

import com.interview.dto.EstimateRequest;
import com.interview.dto.EstimateResponse;
import com.interview.dto.EstimateUpdateRequest;
import com.interview.dto.PageResponse;
import com.interview.entity.Estimate;
import com.interview.entity.EstimateStatus;
import com.interview.entity.WorkOrder;
import com.interview.exception.InvalidRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.EstimateRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstimateService {
    private final EstimateRepository estimateRepository;
    private final WorkOrderService workOrderService;

    @Transactional
    public EstimateResponse create(EstimateRequest request) {
        Estimate estimate = Estimate.from(request);
        Estimate savedEstimate = estimateRepository.save(estimate);
        MDC.put("estimateId", savedEstimate.getId().toString());
        return savedEstimate.toResponse();
    }

    @Transactional(readOnly = true)
    public EstimateResponse get(UUID id) {
        Estimate estimate = findEntity(id);
        List<WorkOrder> workOrders = workOrderService.findAvailableForEstimateResponse(id);
        return estimate.toResponse(workOrders);
    }

    @Transactional(readOnly = true)
    public PageResponse<EstimateResponse> list(UUID customerId, EstimateStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return PageResponse.from(estimateRepository.findAll(withFilters(customerId, status), pageRequest)
            .map(Estimate::toResponse));
    }

    @Transactional
    public EstimateResponse update(UUID id, EstimateUpdateRequest request) {
        MDC.put("estimateId", id.toString());
        Estimate estimate = findEntity(id);
        updateEstimateFromRequest(estimate, request);
        return estimate.toResponse();
    }

    @Transactional
    public void delete(UUID id) {
        MDC.put("estimateId", id.toString());
        Estimate estimate = findEntity(id);
        estimateRepository.delete(estimate);
    }

    @Transactional
    public EstimateResponse addWorkOrder(UUID estimateId, com.interview.dto.WorkOrderRequest request) {
        MDC.put("estimateId", estimateId.toString());
        Estimate estimate = findEntity(estimateId);
        WorkOrder workOrder = workOrderService.createWorkOrderFromRequest(request);
        MDC.put("workOrderId", workOrder.getId().toString());
        addWorkOrderToEstimate(estimate, workOrder);
        return estimate.toResponse();
    }

    @Transactional
    public EstimateResponse addExistingWorkOrder(UUID estimateId, UUID workOrderId) {
        MDC.put("estimateId", estimateId.toString());
        MDC.put("workOrderId", workOrderId.toString());
        Estimate estimate = findEntity(estimateId);
        WorkOrder workOrder = workOrderService.findEntity(workOrderId);
        addWorkOrderToEstimate(estimate, workOrder);
        return estimate.toResponse();
    }

    public Estimate findEntity(UUID id) {
        return estimateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estimate " + id + " was not found"));
    }

    private void updateEstimateFromRequest(Estimate estimate, EstimateUpdateRequest request) {
        estimate.setStatus(request.status());
    }

    private void addWorkOrderToEstimate(Estimate estimate, WorkOrder workOrder) {
        if (estimate.containsWorkOrder(workOrder.getId())) {
            throw new InvalidRequestException(
                "Work order " + workOrder.getId() + "(Summary: " + workOrder.getSummary() + ") is already associated with estimate " + estimate.getId()
            );
        }

        estimate.getWorkOrders().add(workOrder);
    }

    private Specification<Estimate> withFilters(UUID customerId, EstimateStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (customerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("customerId"), customerId));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
