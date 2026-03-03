package com.interview.workorder.service;

import com.interview.common.error.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import com.interview.workorder.mapping.WorkOrderMapper;
import com.interview.workorder.request.WorkOrderRequest;
import com.interview.workorder.response.WorkOrderResponse;
import com.interview.workorder.dao.WorkOrderRepository;
import com.interview.workorder.entity.WorkOrder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderService {

    private final WorkOrderRepository repository;
    private final WorkOrderMapper mapper;

    public WorkOrderService(WorkOrderRepository repository, WorkOrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public WorkOrderResponse create(WorkOrderRequest request) {
        WorkOrder workOrder = mapper.toEntity(request);
        LocalDateTime now = LocalDateTime.now();
        workOrder.setCreatedAt(now);
        workOrder.setUpdatedAt(now);

        return mapper.toResponse(repository.save(workOrder));
    }

    public List<WorkOrderResponse> list() {
        return mapper.toResponseList(repository.findAll(Sort.by(Sort.Direction.ASC, "id")));
    }

    public WorkOrderResponse getById(Long id) {
        return mapper.toResponse(findByIdOrThrow(id));
    }

    public WorkOrderResponse update(Long id, WorkOrderRequest request) {
        WorkOrder existing = findByIdOrThrow(id);
        mapper.updateEntityFromRequest(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        return mapper.toResponse(repository.save(existing));
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
