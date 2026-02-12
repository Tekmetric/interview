package com.interview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an athlete participating in a track and field event.
 * <p>
 * This entity holds basic personal and performance-related information
 * such as name, nationality, discipline, and personal best record.
 * </p>
 *
 * Used for storing and retrieving athlete data from the database.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private Long birthTimestamp;

    private String nationality;

    private String discipline;

    private String personalBest;

    private String bio;
}
