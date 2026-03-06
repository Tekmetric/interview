package com.interview.workorder.service;

import com.interview.common.error.ResourceNotFoundException;
import com.interview.customer.entity.Customer;
import com.interview.customer.service.CustomerService;
import com.interview.workorder.mapping.WorkOrderMapper;
import com.interview.workorder.dto.WorkOrderRequest;
import com.interview.workorder.dto.WorkOrderResponse;
import com.interview.workorder.dao.WorkOrderRepository;
import com.interview.workorder.entity.WorkOrder;
import com.interview.workorder.model.WorkOrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkOrderService {

    private final CustomerService customerService;
    private final WorkOrderRepository repository;
    private final WorkOrderMapper mapper;

    public WorkOrderResponse create(Long customerId, WorkOrderRequest request) {
        // TODO(prod): Enforce idempotency key for create requests to prevent duplicate writes on retries.
        WorkOrder workOrder = mapper.toEntity(request);
        workOrder.setCustomer(resolveCustomerById(customerId));
        return mapper.toResponse(repository.save(workOrder));
    }

    public Page<WorkOrderResponse> list(Long customerId, WorkOrderStatus status, Pageable pageable) {
        resolveCustomerById(customerId);
        if (status != null) {
            return repository.findAllByCustomer_IdAndStatus(customerId, status, pageable)
                    .map(mapper::toResponse);
        }
        return repository.findAllByCustomer_Id(customerId, pageable)
                .map(mapper::toResponse);
    }

    public WorkOrderResponse getById(Long customerId, Long id) {
        resolveCustomerById(customerId);
        return mapper.toResponse(findByIdAndCustomerIdOrThrow(id, customerId));
    }

    public WorkOrderResponse update(Long customerId, Long id, WorkOrderRequest request) {
        resolveCustomerById(customerId);
        WorkOrder existing = findByIdAndCustomerIdOrThrow(id, customerId);
        mapper.updateEntityFromRequest(request, existing);
        return mapper.toResponse(repository.save(existing));
    }

    public void delete(Long customerId, Long id) {
        resolveCustomerById(customerId);
        WorkOrder existing = findByIdAndCustomerIdOrThrow(id, customerId);
        repository.delete(existing);
    }

    private WorkOrder findByIdAndCustomerIdOrThrow(Long id, Long customerId) {
        return repository.findByIdAndCustomer_Id(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException(WorkOrder.class, id));
    }

    private Customer resolveCustomerById(Long customerId) {
        return customerService.findByIdOrThrow(customerId);
    }
}
