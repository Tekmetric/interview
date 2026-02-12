package com.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating an athlete.
 * Decouples API contract from domain model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AthleteRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 30, message = "First name must not exceed 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 30, message = "Last name must not exceed 30 characters")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date must be in the past or present")
    private LocalDate birthDate;

    @NotBlank(message = "Nationality is required")
    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @NotBlank(message = "Discipline is required")
    @Size(max = 100, message = "Discipline must not exceed 100 characters")
    private String discipline;

    @Size(max = 20, message = "Personal best must not exceed 20 characters")
    private String personalBest;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}
