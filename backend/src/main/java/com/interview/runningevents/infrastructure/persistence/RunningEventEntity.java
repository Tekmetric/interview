package com.interview.runningevents.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for storing running events in the database.
 * Maps to the "running_event" table defined in data.sql.
 */
@Entity
@Table(name = "running_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RunningEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "date_time", nullable = false)
    private Long dateTime;

    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "further_information", length = 1000)
    private String furtherInformation;
}
