package com.interview.repository;

import static com.interview.test.QueryAssert.assertThatQuery;
import static org.assertj.core.api.Assertions.assertThat;

import com.interview.repository.entity.CustomerEntity;
import com.interview.repository.entity.LaborLineItemEntity;
import com.interview.repository.entity.PartLineItemEntity;
import com.interview.repository.entity.VehicleEntity;
import com.interview.repository.entity.WorkOrderEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Sql({"/datasets/vehicle-data.sql", "/datasets/work-order-data.sql"})
class WorkOrderRepositoryIT {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID VEHICLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID WORK_ORDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000021");
    private static final UUID PART_LINE_ITEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000031");
    private static final UUID LABOR_LINE_ITEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000041");

    @Autowired
    WorkOrderRepository workOrderRepository;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    SessionFactory sessionFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = sessionFactory.getStatistics();
    }

    @Test
    void savePersistsWorkOrder() {
        final WorkOrderEntity entity = workOrderEntity();

        statistics.clear();
        final WorkOrderEntity saved = workOrderRepository.save(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasInsertCount(1).hasNoOtherOperations();

        entityManager.clear();

        final WorkOrderEntity found = entityManager.find(WorkOrderEntity.class, saved.getId());
        assertThat(found).usingRecursiveComparison()
                .ignoringFields("customer", "vehicle", "partLineItems", "laborLineItems")
                .isEqualTo(saved);
        assertThat(found.getCustomer().getId()).isEqualTo(saved.getCustomer().getId());
        assertThat(found.getVehicle().getId()).isEqualTo(saved.getVehicle().getId());
    }

    @Test
    void findByIdReturnsWorkOrder() {
        final WorkOrderEntity entity = workOrderEntity();
        entityManager.persistAndFlush(entity);
        entityManager.clear();

        statistics.clear();
        final Optional<WorkOrderEntity> result = workOrderRepository.findById(entity.getId());
        assertThatQuery(statistics).hasQueryCount(0).hasNoOtherOperations();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("customer", "vehicle", "partLineItems", "laborLineItems")
                .isEqualTo(entity);
        assertThat(result.get().getCustomer().getId()).isEqualTo(entity.getCustomer().getId());
        assertThat(result.get().getVehicle().getId()).isEqualTo(entity.getVehicle().getId());
    }

    @Test
    void findAllReturnsPage() {
        entityManager.persistAndFlush(workOrderEntity());
        entityManager.persistAndFlush(workOrderEntity());
        entityManager.clear();

        statistics.clear();
        final Page<WorkOrderEntity> page = workOrderRepository.findAll(PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent()).hasSize(3);
    }

    @Test
    void deleteRemovesWorkOrder() {
        final WorkOrderEntity entity = workOrderEntity();
        entityManager.persistAndFlush(entity);
        final UUID id = entity.getId();

        statistics.clear();
        workOrderRepository.delete(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        entityManager.clear();

        assertThat(workOrderRepository.findById(id)).isEmpty();
    }

    @Test
    void findAllWithNoFiltersReturnsAll() {
        entityManager.persistAndFlush(workOrderEntity());
        entityManager.clear();

        statistics.clear();
        final Page<WorkOrderEntity> page =
                workOrderRepository.findAll(null, null, PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findAllFiltersByCustomerId() {
        statistics.clear();
        final Page<WorkOrderEntity> page =
                workOrderRepository.findAll(CUSTOMER_ID, null, PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent())
                .extracting(WorkOrderEntity::getId)
                .containsExactly(WORK_ORDER_ID);
    }

    @Test
    void findAllFiltersByCustomerIdReturnsEmptyForUnknownCustomer() {
        statistics.clear();
        final Page<WorkOrderEntity> page =
                workOrderRepository.findAll(UUID.randomUUID(), null, PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent()).isEmpty();
    }

    @Test
    void findAllFiltersByVehicleId() {
        statistics.clear();
        final Page<WorkOrderEntity> page =
                workOrderRepository.findAll(null, VEHICLE_ID, PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent())
                .extracting(WorkOrderEntity::getId)
                .containsExactly(WORK_ORDER_ID);
    }

    @Test
    void findAllFiltersByCustomerIdAndVehicleId() {
        statistics.clear();
        final Page<WorkOrderEntity> page =
                workOrderRepository.findAll(CUSTOMER_ID, VEHICLE_ID, PageRequest.of(0, 10));
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();

        assertThat(page.getContent())
                .extracting(WorkOrderEntity::getId)
                .containsExactly(WORK_ORDER_ID);
    }

    @Test
    void findByIdWithPartLineItemsReturnsWorkOrderWithParts() {
        statistics.clear();
        final Optional<WorkOrderEntity> result = workOrderRepository.findByIdWithPartLineItems(WORK_ORDER_ID);
        assertThatQuery(statistics).hasQueryCount(1).hasCollectionFetchCount(0).hasNoOtherOperations();

        assertThat(result).isPresent();
        assertThat(result.get().getPartLineItems())
                .extracting(PartLineItemEntity::getId)
                .containsExactly(PART_LINE_ITEM_ID);
    }

    @Test
    void findByIdWithPartLineItemsReturnsEmptyForUnknownId() {
        statistics.clear();
        assertThat(workOrderRepository.findByIdWithPartLineItems(UUID.randomUUID())).isEmpty();
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void findByIdWithLaborLineItemsReturnsWorkOrderWithLabor() {
        statistics.clear();
        final Optional<WorkOrderEntity> result = workOrderRepository.findByIdWithLaborLineItems(WORK_ORDER_ID);
        assertThatQuery(statistics).hasQueryCount(1).hasCollectionFetchCount(0).hasNoOtherOperations();

        assertThat(result).isPresent();
        assertThat(result.get().getLaborLineItems())
                .extracting(LaborLineItemEntity::getId)
                .containsExactly(LABOR_LINE_ITEM_ID);
    }

    @Test
    void findByIdWithLaborLineItemsReturnsEmptyForUnknownId() {
        statistics.clear();
        assertThat(workOrderRepository.findByIdWithLaborLineItems(UUID.randomUUID())).isEmpty();
        assertThatQuery(statistics).hasQueryCount(1).hasNoOtherOperations();
    }

    @Test
    void saveCascadesPartLineItem() {
        final WorkOrderEntity entity = workOrderEntity();
        final PartLineItemEntity partLineItem = new PartLineItemEntity();
        partLineItem.setName("Brake Pad");
        partLineItem.setQuantity(4);
        partLineItem.setPartNumber(UUID.randomUUID());
        entity.addPartLineItem(partLineItem);

        statistics.clear();
        workOrderRepository.save(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasInsertCount(2).hasNoOtherOperations();

        entityManager.clear();

        final WorkOrderEntity found =
                workOrderRepository.findByIdWithPartLineItems(entity.getId()).orElseThrow();
        assertThat(found.getPartLineItems())
                .extracting(PartLineItemEntity::getName)
                .containsExactly("Brake Pad");
    }

    @Test
    void saveCascadesLaborLineItem() {
        final WorkOrderEntity entity = workOrderEntity();
        final LaborLineItemEntity laborLineItem = new LaborLineItemEntity();
        laborLineItem.setName("Brake Inspection");
        laborLineItem.setQuantity(1);
        laborLineItem.setServiceCode(UUID.randomUUID());
        entity.addLaborLineItem(laborLineItem);

        statistics.clear();
        workOrderRepository.save(entity);
        entityManager.flush();
        assertThatQuery(statistics).hasInsertCount(2).hasNoOtherOperations();

        entityManager.clear();

        final WorkOrderEntity found =
                workOrderRepository.findByIdWithLaborLineItems(entity.getId()).orElseThrow();
        assertThat(found.getLaborLineItems())
                .extracting(LaborLineItemEntity::getName)
                .containsExactly("Brake Inspection");
    }

    @Test
    void deleteOrphansPartLineItem() {
        final WorkOrderEntity workOrder =
                workOrderRepository.findByIdWithPartLineItems(WORK_ORDER_ID).orElseThrow();
        final PartLineItemEntity partLineItem = workOrder.getPartLineItems().iterator().next();
        workOrder.removePartLineItem(partLineItem);

        statistics.clear();
        workOrderRepository.save(workOrder);
        entityManager.flush();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        entityManager.clear();

        final WorkOrderEntity found =
                workOrderRepository.findByIdWithPartLineItems(WORK_ORDER_ID).orElseThrow();
        assertThat(found.getPartLineItems()).isEmpty();
    }

    @Test
    void deleteOrphansLaborLineItem() {
        final WorkOrderEntity workOrder =
                workOrderRepository.findByIdWithLaborLineItems(WORK_ORDER_ID).orElseThrow();
        final LaborLineItemEntity laborLineItem = workOrder.getLaborLineItems().iterator().next();
        workOrder.removeLaborLineItem(laborLineItem);

        statistics.clear();
        workOrderRepository.save(workOrder);
        entityManager.flush();
        assertThatQuery(statistics).hasDeleteCount(1).hasNoOtherOperations();

        entityManager.clear();

        final WorkOrderEntity found =
                workOrderRepository.findByIdWithLaborLineItems(WORK_ORDER_ID).orElseThrow();
        assertThat(found.getLaborLineItems()).isEmpty();
    }

    private WorkOrderEntity workOrderEntity() {
        final WorkOrderEntity entity = new WorkOrderEntity();
        entity.setScheduledStartDateTime(Instant.parse("2026-04-01T09:00:00Z"));
        entity.setCustomer(entityManager.find(CustomerEntity.class, CUSTOMER_ID));
        entity.setVehicle(entityManager.find(VehicleEntity.class, VEHICLE_ID));
        return entity;
    }
}
