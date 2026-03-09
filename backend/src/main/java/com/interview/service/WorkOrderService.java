package com.interview.service;

import com.interview.domain.LaborLineItem;
import com.interview.domain.PartLineItem;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderDetail;
import com.interview.repository.WorkOrderRepository;
import com.interview.repository.entity.LaborLineItemEntity;
import com.interview.repository.entity.PartLineItemEntity;
import com.interview.repository.entity.WorkOrderEntity;
import com.interview.repository.mapper.LaborLineItemEntityMapper;
import com.interview.repository.mapper.PartLineItemEntityMapper;
import com.interview.repository.mapper.WorkOrderEntityMapper;
import com.interview.service.exception.WorkOrderNotFound;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderService {
    private final WorkOrderEntityMapper workOrderEntityMapper;
    private final PartLineItemEntityMapper partLineItemEntityMapper;
    private final LaborLineItemEntityMapper laborLineItemEntityMapper;
    private final WorkOrderRepository workOrderRepository;

    public WorkOrderService(
            WorkOrderEntityMapper workOrderEntityMapper,
            PartLineItemEntityMapper partLineItemEntityMapper,
            LaborLineItemEntityMapper laborLineItemEntityMapper,
            WorkOrderRepository workOrderRepository) {
        this.workOrderEntityMapper = workOrderEntityMapper;
        this.partLineItemEntityMapper = partLineItemEntityMapper;
        this.laborLineItemEntityMapper = laborLineItemEntityMapper;
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional(readOnly = true)
    public Page<WorkOrder> findAll(UUID customerId, UUID vehicleId, Pageable pageable) {
        final Page<WorkOrderEntity> page = workOrderRepository.findAll(customerId, vehicleId, pageable);
        return page.map(workOrderEntityMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public WorkOrderDetail findByIdWithLineItems(UUID id) {
        final WorkOrderEntity workOrderWithParts =
                workOrderRepository.findByIdWithPartLineItems(id).orElseThrow(() -> new WorkOrderNotFound(id));
        final WorkOrderEntity workOrderWithLabors =
                workOrderRepository.findByIdWithLaborLineItems(id).orElseThrow(() -> new WorkOrderNotFound(id));
        final WorkOrder workOrder = workOrderEntityMapper.toDomain(workOrderWithParts);
        final List<PartLineItem> partLineItems =
                partLineItemEntityMapper.toDomain(workOrderWithParts.getPartLineItems());
        final List<LaborLineItem> laborLineItems =
                laborLineItemEntityMapper.toDomain(workOrderWithLabors.getLaborLineItems());
        return new WorkOrderDetail(workOrder, partLineItems, laborLineItems);
    }

    @Transactional
    public WorkOrder create(WorkOrder workOrder) {
        final WorkOrderEntity entity = workOrderEntityMapper.toEntity(workOrder);
        final WorkOrderEntity saved = workOrderRepository.save(entity);
        return workOrderEntityMapper.toDomain(saved);
    }

    @Transactional
    public WorkOrder update(UUID id, WorkOrder workOrder) {
        final WorkOrderEntity entity = workOrderRepository.findById(id).orElseThrow(() -> new WorkOrderNotFound(id));
        workOrderEntityMapper.updateEntity(workOrder, entity);
        final WorkOrderEntity saved = workOrderRepository.save(entity);
        return workOrderEntityMapper.toDomain(saved);
    }

    @Transactional
    public void delete(UUID id) {
        final WorkOrderEntity entity = workOrderRepository.findById(id).orElseThrow(() -> new WorkOrderNotFound(id));
        workOrderRepository.delete(entity);
    }

    @Transactional
    public PartLineItem createPartLineItem(UUID workOrderId, PartLineItem partLineItem) {
        final WorkOrderEntity workOrder =
                workOrderRepository.findById(workOrderId).orElseThrow(() -> new WorkOrderNotFound(workOrderId));
        final PartLineItemEntity lineItemEntity = partLineItemEntityMapper.toEntity(partLineItem);
        workOrder.addPartLineItem(lineItemEntity);
        workOrderRepository.save(workOrder);
        return partLineItemEntityMapper.toDomain(lineItemEntity);
    }

    @Transactional
    public void deletePartLineItem(UUID workOrderId, UUID lineItemId) {
        final WorkOrderEntity workOrder = workOrderRepository
                .findByIdWithPartLineItems(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFound(workOrderId));
        workOrder.getPartLineItems().stream()
                .filter(lineItem -> lineItem.getId().equals(lineItemId))
                .findFirst()
                .ifPresent(workOrder::removePartLineItem);
        workOrderRepository.save(workOrder);
    }

    @Transactional
    public LaborLineItem createLaborLineItem(UUID workOrderId, LaborLineItem laborLineItem) {
        final WorkOrderEntity workOrder =
                workOrderRepository.findById(workOrderId).orElseThrow(() -> new WorkOrderNotFound(workOrderId));
        final LaborLineItemEntity lineItemEntity = laborLineItemEntityMapper.toEntity(laborLineItem);
        workOrder.addLaborLineItem(lineItemEntity);
        workOrderRepository.save(workOrder);
        return laborLineItemEntityMapper.toDomain(lineItemEntity);
    }

    @Transactional
    public void deleteLaborLineItem(UUID workOrderId, UUID lineItemId) {
        final WorkOrderEntity workOrder = workOrderRepository
                .findByIdWithLaborLineItems(workOrderId)
                .orElseThrow(() -> new WorkOrderNotFound(workOrderId));
        workOrder.getLaborLineItems().stream()
                .filter(lineItem -> lineItem.getId().equals(lineItemId))
                .findFirst()
                .ifPresent(workOrder::removeLaborLineItem);
        workOrderRepository.save(workOrder);
    }
}
