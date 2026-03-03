package com.interview.workorder.service;

import com.interview.common.error.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import com.interview.workorder.mapping.WorkOrderMapping;
import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;
import com.interview.workorder.dao.WorkOrderRepository;
import com.interview.workorder.entity.WorkOrder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderService {

    private final WorkOrderRepository repository;

    public WorkOrderService(WorkOrderRepository repository) {
        this.repository = repository;
    }

    public WorkOrderResponse create(WorkOrderRequest request) {
        WorkOrder workOrder = new WorkOrder();
        WorkOrderMapping.mapRequest(workOrder, request);
        LocalDateTime now = LocalDateTime.now();
        workOrder.setCreatedAt(now);
        workOrder.setUpdatedAt(now);

        return WorkOrderMapping.mapResponse(repository.save(workOrder));
    }

    public List<WorkOrderResponse> list() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(WorkOrderMapping::mapResponse)
                .toList();
    }

    public WorkOrderResponse getById(Long id) {
        return WorkOrderMapping.mapResponse(findByIdOrThrow(id));
    }

    public WorkOrderResponse update(Long id, WorkOrderRequest request) {
        WorkOrder existing = findByIdOrThrow(id);
        WorkOrderMapping.mapRequest(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());

        return WorkOrderMapping.mapResponse(repository.save(existing));
    }

    public void delete(Long id) {
        WorkOrder existing = findByIdOrThrow(id);
        repository.delete(existing);
    }

    private WorkOrder findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order with id " + id + " was not found"));
    }
}
