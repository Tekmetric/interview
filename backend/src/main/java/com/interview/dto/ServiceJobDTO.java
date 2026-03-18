package com.interview.dto;

import com.interview.model.enumeration.ServiceJobStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class ServiceJobDTO implements Serializable {

    private Long id;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Creation date is mandatory")
    private Instant creationDate;

    @NotNull(message = "Status is mandatory")
    private ServiceJobStatus status;

    private BigDecimal cost;

    @NotNull(message = "Vehicle ID is mandatory")
    private Long vehicleId;

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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceJobDTO that = (ServiceJobDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ServiceJobDTO{"
            + "id=" + id + ","
            + "description='" + description + "'"
            + "creationDate=" + creationDate + ","
            + "status=" + status + ","
            + "cost=" + cost + ","
            + "vehicleId=" + vehicleId + ","
            + "}";
    }
}
