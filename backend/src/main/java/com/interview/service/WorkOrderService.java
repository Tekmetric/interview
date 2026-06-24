package com.interview.service;

import com.interview.exception.BadRequestException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.model.WorkOrder;
import com.interview.repository.WorkOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkOrderService {
    private final WorkOrderRepository workOrderRepository;

    public Optional<WorkOrder> get(Long id) {
        return workOrderRepository.findById(id)
                .map(WorkOrder::fromEntity);
    }

    public Page<WorkOrder> getAll(Pageable pageable) {
        return workOrderRepository.findAll(pageable)
                .map(WorkOrder::fromEntity);
    }

    @Transactional
    public WorkOrder create(WorkOrder workOrder) {
        var entity = workOrder.toEntity();
        entity.setId(null); // ID will be set by DB
        var created = workOrderRepository.save(entity);
        return WorkOrder.fromEntity(created);
    }

    @Transactional
    public WorkOrder update(WorkOrder workOrder) {
        if (workOrder.getId() == null) {
            throw new BadRequestException("WorkOrder ID is required for update");
        }
        var existing = workOrderRepository.findById(workOrder.getId())
                .orElseThrow(() -> new ResourceNotFoundException("WorkOrder not found: " + workOrder.getId()));
        existing.setParts(workOrder.toEntity().getParts());

        var updated = workOrderRepository.save(existing);
        return WorkOrder.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        workOrderRepository.deleteById(id);
    }
}
