package com.interview.infrastructure.persistence;

import com.interview.application.WorkOrderRepository;
import com.interview.domain.WorkOrder;
import com.interview.domain.WorkOrderStatus;
import com.interview.infrastructure.jpa.WorkOrderEntity;
import com.interview.infrastructure.jpa.WorkOrderEntity.WorkOrderStatusEntity;
import com.interview.infrastructure.jpa.WorkOrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

    private final WorkOrderJpaRepository jpaRepository;

    public WorkOrderRepositoryImpl(WorkOrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderEntity entity = toEntity(workOrder);
        WorkOrderEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<WorkOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorkOrder> findAll(Optional<WorkOrderStatus> status) {
        List<WorkOrderEntity> entities = status
                .map(s -> jpaRepository.findAllByStatus(toEntityStatus(s)))
                .orElseGet(jpaRepository::findAll);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByVehicleId(UUID vehicleId) {
        return jpaRepository.existsByVehicleId(vehicleId);
    }

    @Override
    public boolean existsByCustomerId(UUID customerId) {
        return jpaRepository.existsByCustomerId(customerId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private WorkOrderEntity toEntity(WorkOrder domain) {
        return new WorkOrderEntity(
                domain.getId(),
                domain.getCustomerId(),
                domain.getVehicleId(),
                domain.getDescription(),
                toEntityStatus(domain.getStatus()),
                domain.getCreatedAt()
        );
    }

    private WorkOrder toDomain(WorkOrderEntity entity) {
        return new WorkOrder(
                entity.getId(),
                entity.getCustomerId(),
                entity.getVehicleId(),
                entity.getDescription(),
                toDomainStatus(entity.getStatus()),
                entity.getCreatedAt()
        );
    }

    private WorkOrderStatusEntity toEntityStatus(WorkOrderStatus status) {
        return WorkOrderStatusEntity.valueOf(status.name());
    }

    private WorkOrderStatus toDomainStatus(WorkOrderStatusEntity status) {
        return WorkOrderStatus.valueOf(status.name());
    }
}
