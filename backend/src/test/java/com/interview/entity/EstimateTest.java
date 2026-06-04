package com.interview.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.dto.EstimateRequest;
import com.interview.dto.EstimateResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class EstimateTest {
    @Test
    void fromCreatesPendingEstimateFromRequest() {
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();

        Estimate estimate = Estimate.from(new EstimateRequest(customerId, vehicleId));

        assertThat(estimate.getCustomerId()).isEqualTo(customerId);
        assertThat(estimate.getVehicleId()).isEqualTo(vehicleId);
        assertThat(estimate.getStatus()).isEqualTo(EstimateStatus.PENDING);
        assertThat(estimate.getWorkOrders()).isEmpty();
    }

    @Test
    void derivedTotalsExcludeRefusedWorkOrders() {
        WorkOrder pendingWorkOrder = workOrder(
            WorkOrderStatus.PENDING,
            new BigDecimal("125.00"),
            new BigDecimal("1.50"),
            new BigDecimal("40.00"),
            2,
            LocalDateTime.now()
        );
        WorkOrder acceptedWorkOrder = workOrder(
            WorkOrderStatus.ACCEPTED,
            new BigDecimal("150.00"),
            new BigDecimal("2.00"),
            new BigDecimal("30.00"),
            1,
            LocalDateTime.now()
        );
        WorkOrder refusedWorkOrder = workOrder(
            WorkOrderStatus.REFUSED,
            new BigDecimal("200.00"),
            new BigDecimal("3.00"),
            new BigDecimal("500.00"),
            1,
            LocalDateTime.now()
        );
        Estimate estimate = estimate(List.of(pendingWorkOrder, acceptedWorkOrder, refusedWorkOrder));

        assertThat(estimate.getTotalTime()).isEqualByComparingTo("3.50");
        assertThat(estimate.getTotalCost()).isEqualByComparingTo("597.5000");
    }

    @Test
    void containsWorkOrderReturnsWhetherWorkOrderIsAssociated() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = workOrder(workOrderId, WorkOrderStatus.PENDING, LocalDateTime.now());
        Estimate estimate = estimate(List.of(workOrder));

        assertThat(estimate.containsWorkOrder(workOrderId)).isTrue();
        assertThat(estimate.containsWorkOrder(UUID.randomUUID())).isFalse();
    }

    @Test
    void toResponseMapsEstimateFieldsDerivedTotalsAndWorkOrderSummaries() {
        UUID estimateId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID vehicleId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 2, 11, 0);
        WorkOrder workOrder = workOrder(
            WorkOrderStatus.ACCEPTED,
            new BigDecimal("110.00"),
            new BigDecimal("2.25"),
            new BigDecimal("25.00"),
            2,
            LocalDateTime.of(2026, 1, 1, 12, 0)
        );
        Estimate estimate = Estimate.builder()
            .id(estimateId)
            .customerId(customerId)
            .vehicleId(vehicleId)
            .status(EstimateStatus.APPROVED)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .workOrders(List.of(workOrder))
            .build();

        EstimateResponse response = estimate.toResponse();

        assertThat(response.id()).isEqualTo(estimateId);
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.vehicleId()).isEqualTo(vehicleId);
        assertThat(response.status()).isEqualTo(EstimateStatus.APPROVED);
        assertThat(response.totalTime()).isEqualByComparingTo("2.25");
        assertThat(response.totalCost()).isEqualByComparingTo("297.5000");
        assertThat(response.createdAt()).isEqualTo(createdAt);
        assertThat(response.updatedAt()).isEqualTo(updatedAt);
        assertThat(response.workOrders()).hasSize(1);
        assertThat(response.workOrders().getFirst().id()).isEqualTo(workOrder.getId());
        assertThat(response.workOrders().getFirst().totalCost()).isEqualByComparingTo("297.5000");
    }

    @Test
    void toResponseAcceptsSpecificWorkOrdersForResponseCalculation() {
        WorkOrder associatedWorkOrder = workOrder(
            WorkOrderStatus.ACCEPTED,
            new BigDecimal("100.00"),
            new BigDecimal("5.00"),
            new BigDecimal("10.00"),
            1,
            LocalDateTime.now()
        );
        WorkOrder responseWorkOrder = workOrder(
            WorkOrderStatus.ACCEPTED,
            new BigDecimal("90.00"),
            new BigDecimal("1.00"),
            new BigDecimal("15.00"),
            2,
            LocalDateTime.now()
        );
        Estimate estimate = estimate(List.of(associatedWorkOrder));

        EstimateResponse response = estimate.toResponse(List.of(responseWorkOrder));

        assertThat(response.totalTime()).isEqualByComparingTo("1.00");
        assertThat(response.totalCost()).isEqualByComparingTo("120.0000");
        assertThat(response.workOrders()).extracting("id").containsExactly(responseWorkOrder.getId());
    }

    @Test
    void toResponseSortsRefusedWorkOrdersAtTheBottomThenByCreatedAt() {
        WorkOrder newestAccepted = workOrder(
            UUID.randomUUID(),
            WorkOrderStatus.ACCEPTED,
            LocalDateTime.of(2026, 1, 2, 9, 0)
        );
        WorkOrder oldestAccepted = workOrder(
            UUID.randomUUID(),
            WorkOrderStatus.PENDING,
            LocalDateTime.of(2026, 1, 1, 9, 0)
        );
        WorkOrder refused = workOrder(
            UUID.randomUUID(),
            WorkOrderStatus.REFUSED,
            LocalDateTime.of(2025, 12, 31, 9, 0)
        );
        Estimate estimate = estimate(List.of(newestAccepted, refused, oldestAccepted));

        EstimateResponse response = estimate.toResponse();

        assertThat(response.workOrders()).extracting("id")
            .containsExactly(oldestAccepted.getId(), newestAccepted.getId(), refused.getId());
    }

    private Estimate estimate(List<WorkOrder> workOrders) {
        return Estimate.builder()
            .id(UUID.randomUUID())
            .customerId(UUID.randomUUID())
            .vehicleId(UUID.randomUUID())
            .status(EstimateStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .workOrders(workOrders)
            .build();
    }

    private WorkOrder workOrder(UUID id, WorkOrderStatus status, LocalDateTime createdAt) {
        WorkOrder workOrder = workOrder(
            status,
            new BigDecimal("100.00"),
            new BigDecimal("1.00"),
            new BigDecimal("25.00"),
            1,
            createdAt
        );
        workOrder.setId(id);
        return workOrder;
    }

    private WorkOrder workOrder(
        WorkOrderStatus status,
        BigDecimal laborRate,
        BigDecimal laborTime,
        BigDecimal partPrice,
        int partQuantity,
        LocalDateTime createdAt
    ) {
        WorkOrder workOrder = WorkOrder.builder()
            .id(UUID.randomUUID())
            .vehicleId(UUID.randomUUID())
            .status(status)
            .summary("Replace spark plugs")
            .notes("Customer approved premium plugs.")
            .laborRate(laborRate)
            .laborTime(laborTime)
            .createdAt(createdAt)
            .updatedAt(createdAt.plusHours(1))
            .build();
        WorkOrderPart workOrderPart = WorkOrderPart.builder()
            .id(UUID.randomUUID())
            .workOrder(workOrder)
            .part(part(partPrice))
            .quantity(partQuantity)
            .build();
        workOrder.setPartsNeeded(List.of(workOrderPart));
        return workOrder;
    }

    private Part part(BigDecimal price) {
        return Part.builder()
            .id(UUID.randomUUID())
            .sku(41002)
            .manufacturer("Denso")
            .name("Iridium Spark Plug")
            .price(price)
            .build();
    }
}
