package com.interview.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "work_order")
@Table(indexes = @Index(name = "idx_work_order_scheduled_start_date_time", columnList = "scheduled_start_date_time"))
public class WorkOrderEntity {
    @Id
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "scheduled_start_date_time")
    private LocalDateTime scheduledStartDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @OneToMany(mappedBy = "workOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartLineItemEntity> partLineItems = new HashSet<>();

    @OneToMany(mappedBy = "workOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LaborLineItemEntity> laborLineItems = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getScheduledStartDateTime() {
        return scheduledStartDateTime;
    }

    public void setScheduledStartDateTime(LocalDateTime scheduledStartDateTime) {
        this.scheduledStartDateTime = scheduledStartDateTime;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public VehicleEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.vehicle = vehicle;
    }

    public Set<PartLineItemEntity> getPartLineItems() {
        return Collections.unmodifiableSet(partLineItems);
    }

    public void setPartLineItems(Set<PartLineItemEntity> partLineItems) {
        this.partLineItems = partLineItems;
    }

    public Set<LaborLineItemEntity> getLaborLineItems() {
        return Collections.unmodifiableSet(laborLineItems);
    }

    public void setLaborLineItems(Set<LaborLineItemEntity> laborLineItems) {
        this.laborLineItems = laborLineItems;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof WorkOrderEntity that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
