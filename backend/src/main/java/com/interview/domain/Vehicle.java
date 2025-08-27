package com.interview.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "idx_vehicle_type", columnList = "type"),
        @Index(name = "idx_vehicle_production_year", columnList = "productionYear")
})
public class Vehicle extends BaseEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(nullable = false)
    private Year productionYear;

    @Column(nullable = false, unique = true)
    private String vin;

    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String make;

    @Builder
    public Vehicle(VehicleType type, Year productionYear, String vin, String model, String make) {
        this.type = type;
        this.productionYear = productionYear;
        this.model = model;
        this.make = make;
        this.vin = vin;
    }

    @Getter
    @AllArgsConstructor
    public enum VehicleSearchableFields implements SearchableFields {
        ID("id"),
        VIN("vin"),
        TYPE("type"),
        PRODUCTION_YEAR("productionYear");

        private final String fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Vehicle))
            return false;

        Vehicle other = (Vehicle) o;

        return getId() != null &&
                getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
