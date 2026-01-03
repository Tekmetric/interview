package com.interview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Table(name = "repairjob")
public class RepairJob {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // job name
    private String jobName;

    // who owns the vehicle
    private String userId;

    // what is being repaired
    private String repairDescription;

    // vehicle details
    private String licensePlate;
    private String make;
    private String model;

    // when the repair job is created
    private LocalDate created;
    // when the repair job was last updated
    private LocalDate lastModified;
    @Enumerated(STRING)
    private RepairStatus status;
}
