package com.interview.repository.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.interview.domain.Vin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;

@Entity(name = "vehicle")
public class VehicleEntity {
    @Id
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Column(name = "vin", unique = true, nullable = false, length = 17)
    private Vin vin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Vin getVin() {
        return vin;
    }

    public void setVin(Vin vin) {
        this.vin = vin;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof VehicleEntity that)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
