package com.interview.model.entity;

import com.interview.model.enums.ServiceOrderStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "service_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceOrderStatus status = ServiceOrderStatus.PENDING;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "service_order_vehicle",
            joinColumns = @JoinColumn(name = "service_order_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"),
            indexes = {
                @Index(name = "idx_so_vehicle_vehicle_id", columnList = "vehicle_id"),
                @Index(name = "idx_so_vehicle_order_id", columnList = "service_order_id")
            }
    )
    private Set<Vehicle> vehicles = new HashSet<>();

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
        vehicle.getServiceOrders().add(this);
    }

    public void removeVehicle(Vehicle vehicle) {
        this.vehicles.remove(vehicle);
        vehicle.getServiceOrders().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceOrder)) return false;
        ServiceOrder order = (ServiceOrder) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}