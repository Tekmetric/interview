package com.interview.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.model.Athlete;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AthleteMapperTest {

    @Test
    void shouldMapRequestToDomain() {
        AthleteRequest request = new AthleteRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        Athlete athlete = AthleteMapper.toDomain(request);

        assertThat(athlete.getId()).isNull(); // ID not set from request
        assertThat(athlete.getFirstName()).isEqualTo("John");
        assertThat(athlete.getLastName()).isEqualTo("Doe");
        assertThat(athlete.getBirthTimestamp()).isNotNull();
        assertThat(athlete.getNationality()).isEqualTo("USA");
        assertThat(athlete.getDiscipline()).isEqualTo("100m Sprint");
        assertThat(athlete.getPersonalBest()).isEqualTo("9.99s");
        assertThat(athlete.getBio()).isEqualTo("Fast runner");
    }

    @Test
    void shouldMapDomainToResponse() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .birthTimestamp(631152000000L) // 1990-01-01 in UTC
                .nationality("USA")
                .discipline("100m Sprint")
                .personalBest("9.99s")
                .bio("Fast runner")
                .build();

        AthleteResponse response = AthleteMapper.toResponse(athlete);

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
    void shouldUpdateExistingAthleteFromRequest() {
        Athlete existing = Athlete.builder()
                .id(1L)
                .firstName("Old")
                .lastName("Name")
                .birthTimestamp(500000000L)
                .nationality("UK")
                .discipline("200m")
                .personalBest("20.00s")
                .bio("Old bio")
                .build();

        AthleteRequest request = new AthleteRequest(
                "John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", "9.99s", "Fast runner");

        AthleteMapper.updateFromRequest(existing, request);

        assertThat(existing.getId()).isEqualTo(1L); // ID unchanged
        assertThat(existing.getFirstName()).isEqualTo("John");
        assertThat(existing.getLastName()).isEqualTo("Doe");
        assertThat(existing.getBirthTimestamp()).isNotNull();
        assertThat(existing.getNationality()).isEqualTo("USA");
        assertThat(existing.getDiscipline()).isEqualTo("100m Sprint");
        assertThat(existing.getPersonalBest()).isEqualTo("9.99s");
        assertThat(existing.getBio()).isEqualTo("Fast runner");
    }

    @Test
    void shouldHandleNullPersonalBestAndBioInRequest() {
        AthleteRequest request =
                new AthleteRequest("John", "Doe", LocalDate.of(1990, 1, 1), "USA", "100m Sprint", null, null);

        Athlete athlete = AthleteMapper.toDomain(request);

        assertThat(athlete.getPersonalBest()).isNull();
        assertThat(athlete.getBio()).isNull();
    }

    @Test
    void shouldHandleNullPersonalBestAndBioInDomain() {
        Athlete athlete = Athlete.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .birthTimestamp(631152000000L)
                .nationality("USA")
                .discipline("100m Sprint")
                .personalBest(null)
                .bio(null)
                .build();

        AthleteResponse response = AthleteMapper.toResponse(athlete);

        assertThat(response.getPersonalBest()).isNull();
        assertThat(response.getBio()).isNull();
    }

    @Test
    void shouldConvertLocalDateToTimestampAndBack() {
        LocalDate originalDate = LocalDate.of(1990, 1, 1);
        AthleteRequest request = new AthleteRequest("John", "Doe", originalDate, "USA", "100m", "9.99s", "Bio");

        // Request -> Domain (LocalDate -> Long)
        Athlete athlete = AthleteMapper.toDomain(request);

        // Domain -> Response (Long -> LocalDate)
        AthleteResponse response = AthleteMapper.toResponse(athlete);

        assertThat(response.getBirthDate()).isEqualTo(originalDate);
    }
}
