package com.interview.entity;

import com.interview.dto.WorkOrderRequest;
import com.interview.dto.WorkOrderResponse;
import com.interview.dto.WorkOrderSummaryResponse;
import com.interview.dto.WorkOrderUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "work_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    @Column(nullable = false)
    private String summary;

    @Column
    private String notes;

    @Column(name = "labor_rate", nullable = false)
    private BigDecimal laborRate;

    @Column(name = "labor_time", nullable = false)
    private BigDecimal laborTime;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkOrderPart> partsNeeded = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static WorkOrder from(WorkOrderRequest request) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setVehicleId(request.vehicleId());
        workOrder.updateFrom(request.toUpdateRequest());
        return workOrder;
    }

    public void updateFrom(WorkOrderUpdateRequest request) {
        status = request.status();
        summary = request.summary();
        notes = request.notes();
        laborRate = request.laborRate();
        laborTime = request.laborTime();
    }

    public void replacePartsNeeded(List<WorkOrderPart> replacementParts) {
        partsNeeded.clear();
        for (WorkOrderPart workOrderPart : replacementParts) {
            workOrderPart.setWorkOrder(this);
            partsNeeded.add(workOrderPart);
        }
    }

    public BigDecimal calculateLaborCost() {
        return laborRate.multiply(laborTime);
    }

    public BigDecimal getTotalCost() {
        BigDecimal partsCost = partsNeeded.stream()
            .map(WorkOrderPart::calculateCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return calculateLaborCost().add(partsCost);
    }

    public WorkOrderResponse toResponse() {
        return new WorkOrderResponse(
            id,
            vehicleId,
            status,
            summary,
            notes,
            laborRate,
            laborTime,
            calculateLaborCost(),
            getTotalCost(),
            createdAt,
            updatedAt,
            partsNeeded.stream()
                .sorted(Comparator.comparing(workOrderPart -> workOrderPart.getPart().getName()))
                .map(WorkOrderPart::toResponse)
                .toList()
        );
    }

    public WorkOrderSummaryResponse toSummaryResponse() {
        return new WorkOrderSummaryResponse(
            id,
            vehicleId,
            status,
            summary,
            notes,
            laborRate,
            laborTime,
            calculateLaborCost(),
            getTotalCost(),
            createdAt,
            updatedAt
        );
    }
}
