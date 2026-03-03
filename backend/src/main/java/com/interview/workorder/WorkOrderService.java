package com.interview.workorder;

import com.interview.common.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
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
        mapRequest(workOrder, request);
        LocalDateTime now = LocalDateTime.now();
        workOrder.setCreatedAt(now);
        workOrder.setUpdatedAt(now);

        return mapResponse(repository.save(workOrder));
    }

    public List<WorkOrderResponse> list() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    public WorkOrderResponse getById(Long id) {
        return mapResponse(findByIdOrThrow(id));
    }

    public WorkOrderResponse update(Long id, WorkOrderRequest request) {
        WorkOrder existing = findByIdOrThrow(id);
        mapRequest(existing, request);
        existing.setUpdatedAt(LocalDateTime.now());

        return mapResponse(repository.save(existing));
    }

    public void delete(Long id) {
        WorkOrder existing = findByIdOrThrow(id);
        repository.delete(existing);
    }

    private WorkOrder findByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order with id " + id + " was not found"));
    }

    private void mapRequest(WorkOrder workOrder, WorkOrderRequest request) {
        workOrder.setCustomerName(request.customerName());
        workOrder.setVin(request.vin());
        workOrder.setIssueDescription(request.issueDescription());
        workOrder.setStatus(request.status());
    }

    private WorkOrderResponse mapResponse(WorkOrder workOrder) {
        return new WorkOrderResponse(
                workOrder.getId(),
                workOrder.getCustomerName(),
                workOrder.getVin(),
                workOrder.getIssueDescription(),
                workOrder.getStatus(),
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }
}
