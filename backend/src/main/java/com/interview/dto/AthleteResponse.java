package com.interview.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for athlete data.
 * Decouples API contract from domain model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String nationality;
    private String discipline;
    private String personalBest;
    private String bio;
}
