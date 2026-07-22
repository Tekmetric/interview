package com.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity(name = "car")
@EntityListeners(AuditingEntityListener.class)
public class CarEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(length = 512, unique = true)
    private String vin;

    @Column(name = "model_year")
    private int year;

    @Column(length = 512)
    private String make;

    @Column(length = 512)
    private String model;

    @Column(length = 512)
    private String trim;

    @Column(length = 512)
    private String color;

    private int mileage;

    private String transmission;

    private String drivetrain;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;
}
