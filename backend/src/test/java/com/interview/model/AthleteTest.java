package com.interview.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AthleteTest {

    @Test
    @DisplayName("Should create athlete with all fields")
    void shouldCreateAthleteWithAllFields() {
        final var athlete = defaultAthlete().build();

        assertThat(athlete.getFirstName()).isEqualTo("Usain");
        assertThat(athlete.getLastName()).isEqualTo("Bolt");
        assertThat(athlete.getBirthTimestamp()).isEqualTo(524966400000L);
        assertThat(athlete.getNationality()).isEqualTo("Jamaica");
        assertThat(athlete.getDiscipline()).isEqualTo("100m");
        assertThat(athlete.getPersonalBest()).isEqualTo("9.58s");
        assertThat(athlete.getBio()).isEqualTo("Fastest man alive.");
    }

    @Test
    @DisplayName("Should create athlete with builder pattern")
    void shouldCreateAthleteWithBuilder() {
        final var athlete = Athlete.builder()
                .firstName("Mo")
                .lastName("Farah")
                .birthTimestamp(457747200000L) // 1983-03-23
                .nationality("Great Britain")
                .discipline("5000m")
                .personalBest("12:53.11")
                .bio("Distance runner")
                .build();

        assertThat(athlete.getFirstName()).isEqualTo("Mo");
        assertThat(athlete.getLastName()).isEqualTo("Farah");
        assertThat(athlete.getNationality()).isEqualTo("Great Britain");
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValues() {
        final var athlete = Athlete.builder()
                .firstName("Test")
                .lastName("Athlete")
                .birthTimestamp(1234567890000L)
                .build();

        assertThat(athlete.getPersonalBest()).isNull();
        assertThat(athlete.getBio()).isNull();
    }

    @Test
    @DisplayName("Should use toBuilder for immutable updates")
    void shouldUseToBuilderForUpdates() {
        final var originalAthlete = defaultAthlete().build();
        final var updatedAthlete = originalAthlete.toBuilder()
                .personalBest("9.57s")
                .bio("Updated bio")
                .build();

        assertThat(updatedAthlete.getFirstName()).isEqualTo(originalAthlete.getFirstName()); // unchanged
        assertThat(updatedAthlete.getPersonalBest()).isEqualTo("9.57s");
        assertThat(updatedAthlete.getBio()).isEqualTo("Updated bio");
    }

    private Athlete.AthleteBuilder defaultAthlete() {
        return Athlete.builder()
                .firstName("Usain")
                .lastName("Bolt")
                .birthTimestamp(524966400000L) // 1986-08-21
                .nationality("Jamaica")
                .discipline("100m")
                .personalBest("9.58s")
                .bio("Fastest man alive.");
    }
}
