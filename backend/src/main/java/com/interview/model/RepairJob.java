package com.interview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "repairjob")
@EntityListeners(AuditingEntityListener.class)
public class RepairJob {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // job name
    @NotBlank
    @Column(name = "job_name", nullable = false, updatable = false)
    private String jobName;

    // who owns the vehicle
    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;

    // what is being repaired
    @NotBlank
    @Column(name = "repair_description", nullable = false)
    private String repairDescription;

    // vehicle details
    @NotBlank
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @NotBlank
    @Column(name = "make", nullable = false)
    private String make;

    @NotBlank
    @Column(name = "model", nullable = false)
    private String model;

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    @Column(nullable = false)
    private LocalDateTime lastModified;

    @Enumerated(STRING)
    private RepairStatus status;
}
