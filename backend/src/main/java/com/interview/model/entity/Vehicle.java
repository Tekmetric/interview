package com.interview.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 17)
    private String vin;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    // prevent SQL 'YEAR' collision
    @Column(name = "model_year", nullable = false)
    private Integer year;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany(mappedBy = "vehicles")
    private Set<ServiceOrder> serviceOrders = new HashSet<>();

    @PreRemove
    private void removeServiceOrdersFromVehicle() {
        for (ServiceOrder order : new HashSet<>(serviceOrders)) {
            order.removeVehicle(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vin, vehicle.getVin());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}