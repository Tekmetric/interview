package com.interview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.interview.model.enumeration.ServiceJobStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "service_job")
public class ServiceJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ServiceJobStatus status;

    @Column(name = "cost", precision = 21, scale = 2)
    private BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonIgnoreProperties(value = "serviceJobs", allowSetters = true)
    private Vehicle vehicle;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public ServiceJobStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceJobStatus status) {
        this.status = status;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceJob that = (ServiceJob) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ServiceJob{"
            + "id=" + id + 
            ", description='" + description + "'" +
            ", creationDate=" + creationDate + 
            ", status=" + status + 
            ", cost=" + cost + 
            '}';
    }
}
