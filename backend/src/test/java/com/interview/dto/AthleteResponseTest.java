package com.interview.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AthleteResponseTest {

    @Test
    void shouldCreateAthleteResponseUsingBuilder() {
        AthleteResponse response = AthleteResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .nationality("USA")
                .discipline("100m Sprint")
                .personalBest("9.99s")
                .bio("Fast runner")
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(response.getNationality()).isEqualTo("USA");
        assertThat(response.getDiscipline()).isEqualTo("100m Sprint");
        assertThat(response.getPersonalBest()).isEqualTo("9.99s");
        assertThat(response.getBio()).isEqualTo("Fast runner");
    }

    @Test
    void shouldCreateAthleteResponseWithAllArgsConstructor() {
        AthleteResponse response = new AthleteResponse(
                1L, "John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldCreateAthleteResponseWithNoArgsConstructor() {
        AthleteResponse response = new AthleteResponse();
        response.setId(1L);
        response.setFirstName("John");
        response.setBirthDate(LocalDate.of(1990, 1, 1));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void shouldHandleNullValues() {
        AthleteResponse response = AthleteResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .nationality("USA")
                .discipline("100m Sprint")
                .personalBest(null)
                .bio(null)
                .build();

        assertThat(response.getPersonalBest()).isNull();
        assertThat(response.getBio()).isNull();
    }
}
