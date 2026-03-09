package com.interview.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;

@Entity(name = "part_line_item")
public class PartLineItemEntity {
    @Id
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrderEntity workOrder;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "part_number", nullable = false)
    private UUID partNumber;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkOrderEntity getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrderEntity workOrder) {
        this.workOrder = workOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public UUID getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(UUID partNumber) {
        this.partNumber = partNumber;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof PartLineItemEntity that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
