package com.interview.service;

import com.interview.dto.PageResponse;
import com.interview.dto.WorkOrderPartRequest;
import com.interview.dto.WorkOrderRequest;
import com.interview.dto.WorkOrderResponse;
import com.interview.dto.WorkOrderUpdateRequest;
import com.interview.entity.Part;
import com.interview.entity.WorkOrder;
import com.interview.entity.WorkOrderPart;
import com.interview.entity.WorkOrderStatus;
import com.interview.exception.InvalidRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.repository.PartRepository;
import com.interview.repository.WorkOrderRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;
    private final PartRepository partRepository;

    @Transactional
    public WorkOrderResponse create(WorkOrderRequest request) {
        WorkOrder savedWorkOrder = createWorkOrderFromRequest(request);
        MDC.put("workOrderId", savedWorkOrder.getId().toString());
        return savedWorkOrder.toResponse();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse get(UUID id) {
        return findEntity(id).toResponse();
    }

    @Transactional(readOnly = true)
    public PageResponse<WorkOrderResponse> list(WorkOrderStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(
            page,
            size,
            Sort.by("status").ascending().and(Sort.by("createdAt").descending())
        );

        Page<WorkOrder> workOrders = workOrderRepository.findAll(withFilters(status), pageRequest);
        Page<WorkOrderResponse> responsePage = workOrders.map(WorkOrder::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional
    public WorkOrderResponse update(UUID id, WorkOrderUpdateRequest request) {
        MDC.put("workOrderId", id.toString());
        WorkOrder workOrder = findEntity(id);
        updateWorkOrderFromRequest(workOrder, request);
        return workOrder.toResponse();
    }

    @Transactional
    public void delete(UUID id) {
        MDC.put("workOrderId", id.toString());
        WorkOrder workOrder = findEntity(id);
        workOrderRepository.delete(workOrder);
//        TODO: Should we delete the reference to the work order in the estimate that it is associated with?
    }

    @Transactional(readOnly = true)
    public WorkOrder findEntity(UUID id) {
        return workOrderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Work order " + id + " was not found"));
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> findAvailableForEstimateResponse(UUID estimateId) {
        return workOrderRepository.findAvailableForEstimateResponse(estimateId);
    }

    @Transactional
    public WorkOrder createWorkOrderFromRequest(WorkOrderRequest request) {
        WorkOrder workOrder = WorkOrder.from(request);
        updatePartsNeeded(workOrder, request.partsNeeded());
        return workOrderRepository.save(workOrder);
    }

    private void updateWorkOrderFromRequest(WorkOrder workOrder, WorkOrderUpdateRequest request) {
        workOrder.updateFrom(request);
        updatePartsNeeded(workOrder, request.partsNeeded());
    }

    private void updatePartsNeeded(WorkOrder workOrder, List<WorkOrderPartRequest> partsNeeded) {
        List<WorkOrderPartRequest> consolidatedPartsNeeded = consolidatePartsNeeded(partsNeeded);
        Map<UUID, Part> partsById = findPartsById(consolidatedPartsNeeded);
        List<WorkOrderPart> replacementParts = new ArrayList<>();

        for (WorkOrderPartRequest partRequest : consolidatedPartsNeeded) {
            replacementParts.add(WorkOrderPart.from(workOrder, partsById.get(partRequest.partId()), partRequest));
        }
        workOrder.replacePartsNeeded(replacementParts);
    }

    private Map<UUID, Part> findPartsById(List<WorkOrderPartRequest> partsNeeded) {
        Set<UUID> requestedPartIds = partsNeeded.stream()
            .map(WorkOrderPartRequest::partId)
            .collect(Collectors.toSet());

        Map<UUID, Part> partsById = partRepository.findAllById(requestedPartIds).stream()
            .collect(Collectors.toMap(Part::getId, Function.identity()));

        List<UUID> missingPartIds = requestedPartIds.stream()
            .filter(id -> !partsById.containsKey(id))
            .sorted()
            .toList();

        if (!missingPartIds.isEmpty()) {
            UUID missingPartId = missingPartIds.getFirst();
            MDC.put("partId", missingPartId.toString());
            throw new InvalidRequestException("Part " + missingPartId + " does not exist");
        }

        return partsById;
    }

    private List<WorkOrderPartRequest> consolidatePartsNeeded(List<WorkOrderPartRequest> partsNeeded) {
        Map<UUID, Integer> quantitiesByPartId = new LinkedHashMap<>();
        for (WorkOrderPartRequest partRequest : partsNeeded) {
            quantitiesByPartId.merge(partRequest.partId(), partRequest.quantity(), Integer::sum);
        }

        return quantitiesByPartId.entrySet().stream()
            .map(entry -> new WorkOrderPartRequest(entry.getKey(), entry.getValue()))
            .toList();
    }

    private Specification<WorkOrder> withFilters(WorkOrderStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

}
