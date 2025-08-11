package com.interview.jpa.entity;

import com.interview.jpa.entity.common.AuditFields;
import com.interview.jpa.entity.enums.PlaneEnum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "plane")
@DynamicUpdate
public class Plane extends AuditFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model")
    private String model;

    @Column(name = "seat_capacity")
    private Integer seatCapacity;

    @Column(name = "range_km")
    private Integer rangeKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
