package com.interview.entity;

import com.interview.dto.EstimateRequest;
import com.interview.dto.EstimateResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "estimates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estimate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstimateStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "estimate_work_orders",
        joinColumns = @JoinColumn(name = "estimate_id"),
        inverseJoinColumns = @JoinColumn(name = "work_order_id")
    )
    @Builder.Default
    private List<WorkOrder> workOrders = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static Estimate from(EstimateRequest request) {
        Estimate estimate = new Estimate();
        estimate.setCustomerId(request.customerId());
        estimate.setVehicleId(request.vehicleId());
        estimate.setStatus(EstimateStatus.PENDING);
        return estimate;
    }

    public BigDecimal getTotalCost() {
        return workOrders.stream()
            .filter(workOrder -> workOrder.getStatus() != WorkOrderStatus.REFUSED)
            .map(WorkOrder::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTime() {
        return workOrders.stream()
            .filter(workOrder -> workOrder.getStatus() != WorkOrderStatus.REFUSED)
            .map(WorkOrder::getLaborTime)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean containsWorkOrder(UUID workOrderId) {
        return workOrders.stream().anyMatch(workOrder -> workOrder.getId().equals(workOrderId));
    }

    public EstimateResponse toResponse() {
        return toResponse(workOrders);
    }

    public EstimateResponse toResponse(List<WorkOrder> responseWorkOrders) {
        return new EstimateResponse(
            id,
            customerId,
            vehicleId,
            status,
            getTotalCost(responseWorkOrders),
            getTotalTime(responseWorkOrders),
            createdAt,
            updatedAt,
            responseWorkOrders.stream()
                .sorted(Comparator.comparing((WorkOrder workOrder) -> workOrder.getStatus() == WorkOrderStatus.REFUSED)
                    .thenComparing(WorkOrder::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(WorkOrder::toSummaryResponse)
                .toList()
        );
    }

    private BigDecimal getTotalCost(List<WorkOrder> responseWorkOrders) {
        return responseWorkOrders.stream()
            .filter(workOrder -> workOrder.getStatus() != WorkOrderStatus.REFUSED)
            .map(WorkOrder::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalTime(List<WorkOrder> responseWorkOrders) {
        return responseWorkOrders.stream()
            .filter(workOrder -> workOrder.getStatus() != WorkOrderStatus.REFUSED)
            .map(WorkOrder::getLaborTime)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
