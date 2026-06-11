package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    static final Instant SCHEDULED_START = Instant.parse("2026-04-01T09:00:00Z");
    static final UUID CUSTOMER_ID = UUID.randomUUID();
    static final UUID VEHICLE_ID = UUID.randomUUID();

    @Mock
    WorkOrderRepository workOrderRepository;

    @Mock
    WorkOrderEntityMapper workOrderEntityMapper;

    @Mock
    PartLineItemEntityMapper partLineItemEntityMapper;

    @Mock
    LaborLineItemEntityMapper laborLineItemEntityMapper;

    @InjectMocks
    WorkOrderService workOrderService;

    @Test
    void findAllReturnsPageOfWorkOrders() {
        final WorkOrderEntity entity = workOrderEntity();
        final WorkOrder workOrder = new WorkOrder(entity.getId(), SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(workOrderRepository.findAll(null, null, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(workOrderEntityMapper.toDomain(entity)).thenReturn(workOrder);

        final Page<WorkOrder> result = workOrderService.findAll(null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(workOrderEntityMapper).toDomain(entity);
    }

    @Test
    void findAllEmptyPage() {
        final PageRequest pageable = PageRequest.of(0, 20);
        when(workOrderRepository.findAll(null, null, pageable)).thenReturn(Page.empty());

        final Page<WorkOrder> result = workOrderService.findAll(null, null, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findAllByCustomerIdReturnsFilteredPage() {
        final WorkOrderEntity entity = workOrderEntity();
        final WorkOrder workOrder = new WorkOrder(entity.getId(), SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(workOrderRepository.findAll(CUSTOMER_ID, null, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(workOrderEntityMapper.toDomain(entity)).thenReturn(workOrder);

        final Page<WorkOrder> result = workOrderService.findAll(CUSTOMER_ID, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(workOrderRepository).findAll(CUSTOMER_ID, null, pageable);
    }

    @Test
    void findAllByVehicleIdReturnsFilteredPage() {
        final WorkOrderEntity entity = workOrderEntity();
        final WorkOrder workOrder = new WorkOrder(entity.getId(), SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(workOrderRepository.findAll(null, VEHICLE_ID, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(workOrderEntityMapper.toDomain(entity)).thenReturn(workOrder);

        final Page<WorkOrder> result = workOrderService.findAll(null, VEHICLE_ID, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(workOrderRepository).findAll(null, VEHICLE_ID, pageable);
    }

    @Test
    void findAllByCustomerIdAndVehicleIdReturnsFilteredPage() {
        final WorkOrderEntity entity = workOrderEntity();
        final WorkOrder workOrder = new WorkOrder(entity.getId(), SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final PageRequest pageable = PageRequest.of(0, 20);
        when(workOrderRepository.findAll(CUSTOMER_ID, VEHICLE_ID, pageable))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(workOrderEntityMapper.toDomain(entity)).thenReturn(workOrder);

        final Page<WorkOrder> result = workOrderService.findAll(CUSTOMER_ID, VEHICLE_ID, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(workOrderRepository).findAll(CUSTOMER_ID, VEHICLE_ID, pageable);
    }

    @Test
    void findByIdWithLineItemsReturnsDetail() {
        final UUID id = UUID.randomUUID();
        final WorkOrderEntity partWorkOrderEntity = workOrderEntity(id);
        final WorkOrderEntity laborWorkOrderEntity = workOrderEntity(id);
        final PartLineItemEntity partEntity = partLineItemEntity();
        final LaborLineItemEntity laborEntity = laborLineItemEntity();
        partWorkOrderEntity.addPartLineItem(partEntity);
        laborWorkOrderEntity.addLaborLineItem(laborEntity);

        final WorkOrder workOrder = new WorkOrder(id, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final PartLineItem partLineItem =
                new PartLineItem(partEntity.getId(), "Oil Filter", 1, partEntity.getPartNumber());
        final LaborLineItem laborLineItem =
                new LaborLineItem(laborEntity.getId(), "Oil Change", 1, laborEntity.getServiceCode());

        when(workOrderRepository.findByIdWithPartLineItems(id)).thenReturn(Optional.of(partWorkOrderEntity));
        when(workOrderRepository.findByIdWithLaborLineItems(id)).thenReturn(Optional.of(laborWorkOrderEntity));
        when(workOrderEntityMapper.toDomain(partWorkOrderEntity)).thenReturn(workOrder);
        when(partLineItemEntityMapper.toDomain(partWorkOrderEntity.getPartLineItems()))
                .thenReturn(List.of(partLineItem));
        when(laborLineItemEntityMapper.toDomain(laborWorkOrderEntity.getLaborLineItems()))
                .thenReturn(List.of(laborLineItem));

        final WorkOrderDetail result = workOrderService.findByIdWithLineItems(id);

        assertThat(result.workOrder()).isEqualTo(workOrder);
        assertThat(result.partLineItems()).containsExactly(partLineItem);
        assertThat(result.laborLineItems()).containsExactly(laborLineItem);
    }

    @Test
    void findByIdWithLineItemsThrowsWorkOrderNotFound() {
        final UUID id = UUID.randomUUID();
        when(workOrderRepository.findByIdWithPartLineItems(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.findByIdWithLineItems(id))
                .isInstanceOf(WorkOrderNotFound.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void createSavesAndReturnsWorkOrder() {
        final UUID savedId = UUID.randomUUID();
        final WorkOrder input = new WorkOrder(null, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final WorkOrderEntity entity = new WorkOrderEntity();
        final WorkOrderEntity savedEntity = workOrderEntity(savedId);
        final WorkOrder expected = new WorkOrder(savedId, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);

        when(workOrderEntityMapper.toEntity(input)).thenReturn(entity);
        when(workOrderRepository.save(entity)).thenReturn(savedEntity);
        when(workOrderEntityMapper.toDomain(savedEntity)).thenReturn(expected);

        final WorkOrder result = workOrderService.create(input);

        assertThat(result).isEqualTo(expected);
        verify(workOrderEntityMapper).toEntity(input);
    }

    @Test
    void updateUpdatesAndReturnsWorkOrder() {
        final UUID id = UUID.randomUUID();
        final WorkOrderEntity existingEntity = workOrderEntity(id);
        final WorkOrder input = new WorkOrder(id, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);
        final WorkOrder expected = new WorkOrder(id, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID);

        when(workOrderRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(workOrderRepository.save(existingEntity)).thenReturn(existingEntity);
        when(workOrderEntityMapper.toDomain(existingEntity)).thenReturn(expected);

        final WorkOrder result = workOrderService.update(id, input);

        assertThat(result).isEqualTo(expected);
        verify(workOrderEntityMapper).updateEntity(input, existingEntity);
    }

    @Test
    void updateThrowsWorkOrderNotFound() {
        final UUID id = UUID.randomUUID();
        when(workOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                        () -> workOrderService.update(id, new WorkOrder(id, SCHEDULED_START, CUSTOMER_ID, VEHICLE_ID)))
                .isInstanceOf(WorkOrderNotFound.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void deleteDeletesWorkOrder() {
        final UUID id = UUID.randomUUID();
        final WorkOrderEntity entity = workOrderEntity(id);
        when(workOrderRepository.findById(id)).thenReturn(Optional.of(entity));

        workOrderService.delete(id);

        verify(workOrderRepository).delete(entity);
    }

    @Test
    void deleteThrowsWorkOrderNotFound() {
        final UUID id = UUID.randomUUID();
        when(workOrderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.delete(id))
                .isInstanceOf(WorkOrderNotFound.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void createPartLineItemAddsAndReturnsItem() {
        final UUID workOrderId = UUID.randomUUID();
        final UUID partNumber = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);
        final PartLineItem input = new PartLineItem(null, "Oil Filter", 1, partNumber);
        final PartLineItemEntity lineItemEntity = new PartLineItemEntity();
        final PartLineItem expected = new PartLineItem(lineItemEntity.getId(), "Oil Filter", 1, partNumber);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(partLineItemEntityMapper.toEntity(input)).thenReturn(lineItemEntity);
        when(workOrderRepository.save(workOrder)).thenReturn(workOrder);
        when(partLineItemEntityMapper.toDomain(lineItemEntity)).thenReturn(expected);

        final PartLineItem result = workOrderService.createPartLineItem(workOrderId, input);

        assertThat(result).isEqualTo(expected);
        assertThat(lineItemEntity.getWorkOrder()).isEqualTo(workOrder);
    }

    @Test
    void createPartLineItemThrowsWorkOrderNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.createPartLineItem(
                        workOrderId, new PartLineItem(null, "x", 1, UUID.randomUUID())))
                .isInstanceOf(WorkOrderNotFound.class);
    }

    @Test
    void deletePartLineItemRemovesItem() {
        final UUID workOrderId = UUID.randomUUID();
        final UUID lineItemId = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);
        final PartLineItemEntity lineItem = new PartLineItemEntity();
        lineItem.setId(lineItemId);
        workOrder.addPartLineItem(lineItem);

        when(workOrderRepository.findByIdWithPartLineItems(workOrderId)).thenReturn(Optional.of(workOrder));

        workOrderService.deletePartLineItem(workOrderId, lineItemId);

        assertThat(workOrder.getPartLineItems()).isEmpty();
        verify(workOrderRepository).save(workOrder);
    }

    @Test
    void deletePartLineItemNoOpWhenNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);

        when(workOrderRepository.findByIdWithPartLineItems(workOrderId)).thenReturn(Optional.of(workOrder));

        workOrderService.deletePartLineItem(workOrderId, UUID.randomUUID());

        verify(workOrderRepository).save(workOrder);
    }

    @Test
    void deletePartLineItemThrowsWorkOrderNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findByIdWithPartLineItems(workOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.deletePartLineItem(workOrderId, UUID.randomUUID()))
                .isInstanceOf(WorkOrderNotFound.class);
    }

    @Test
    void createLaborLineItemAddsAndReturnsItem() {
        final UUID workOrderId = UUID.randomUUID();
        final UUID serviceCode = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);
        final LaborLineItem input = new LaborLineItem(null, "Oil Change", 1, serviceCode);
        final LaborLineItemEntity lineItemEntity = new LaborLineItemEntity();
        final LaborLineItem expected = new LaborLineItem(lineItemEntity.getId(), "Oil Change", 1, serviceCode);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrder));
        when(laborLineItemEntityMapper.toEntity(input)).thenReturn(lineItemEntity);
        when(workOrderRepository.save(workOrder)).thenReturn(workOrder);
        when(laborLineItemEntityMapper.toDomain(lineItemEntity)).thenReturn(expected);

        final LaborLineItem result = workOrderService.createLaborLineItem(workOrderId, input);

        assertThat(result).isEqualTo(expected);
        assertThat(lineItemEntity.getWorkOrder()).isEqualTo(workOrder);
    }

    @Test
    void createLaborLineItemThrowsWorkOrderNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.createLaborLineItem(
                        workOrderId, new LaborLineItem(null, "x", 1, UUID.randomUUID())))
                .isInstanceOf(WorkOrderNotFound.class);
    }

    @Test
    void deleteLaborLineItemRemovesItem() {
        final UUID workOrderId = UUID.randomUUID();
        final UUID lineItemId = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);
        final LaborLineItemEntity lineItem = new LaborLineItemEntity();
        lineItem.setId(lineItemId);
        workOrder.addLaborLineItem(lineItem);

        when(workOrderRepository.findByIdWithLaborLineItems(workOrderId)).thenReturn(Optional.of(workOrder));

        workOrderService.deleteLaborLineItem(workOrderId, lineItemId);

        assertThat(workOrder.getLaborLineItems()).isEmpty();
        verify(workOrderRepository).save(workOrder);
    }

    @Test
    void deleteLaborLineItemNoOpWhenNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        final WorkOrderEntity workOrder = workOrderEntity(workOrderId);

        when(workOrderRepository.findByIdWithLaborLineItems(workOrderId)).thenReturn(Optional.of(workOrder));

        workOrderService.deleteLaborLineItem(workOrderId, UUID.randomUUID());

        verify(workOrderRepository).save(workOrder);
    }

    @Test
    void deleteLaborLineItemThrowsWorkOrderNotFound() {
        final UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findByIdWithLaborLineItems(workOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workOrderService.deleteLaborLineItem(workOrderId, UUID.randomUUID()))
                .isInstanceOf(WorkOrderNotFound.class);
    }

    private static WorkOrderEntity workOrderEntity() {
        return workOrderEntity(UUID.randomUUID());
    }

    private static WorkOrderEntity workOrderEntity(UUID id) {
        final WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(id);
        entity.setScheduledStartDateTime(SCHEDULED_START);
        return entity;
    }

    private static PartLineItemEntity partLineItemEntity() {
        final PartLineItemEntity entity = new PartLineItemEntity();
        entity.setName("Oil Filter");
        entity.setQuantity(1);
        entity.setPartNumber(UUID.randomUUID());
        return entity;
    }

    private static LaborLineItemEntity laborLineItemEntity() {
        final LaborLineItemEntity entity = new LaborLineItemEntity();
        entity.setName("Oil Change");
        entity.setQuantity(1);
        entity.setServiceCode(UUID.randomUUID());
        return entity;
    }
}
