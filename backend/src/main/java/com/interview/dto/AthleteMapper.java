package com.interview.dto;

import com.interview.model.Athlete;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Mapper for converting between Athlete domain model and DTOs.
 * Handles conversion between LocalDate (API) and Long timestamp (database).
 */
public class AthleteMapper {

    /**
     * Converts request DTO to domain model.
     *
     * @param request the request DTO
     * @return domain model
     */
    public static Athlete toDomain(final AthleteRequest request) {
        return Athlete.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthTimestamp(toTimestamp(request.getBirthDate()))
                .nationality(request.getNationality())
                .discipline(request.getDiscipline())
                .personalBest(request.getPersonalBest())
                .bio(request.getBio())
                .build();
    }

    /**
     * Converts domain model to response DTO.
     *
     * @param athlete the domain model
     * @return response DTO
     */
    public static AthleteResponse toResponse(final Athlete athlete) {
        return AthleteResponse.builder()
                .id(athlete.getId())
                .firstName(athlete.getFirstName())
                .lastName(athlete.getLastName())
                .birthDate(toLocalDate(athlete.getBirthTimestamp()))
                .nationality(athlete.getNationality())
                .discipline(athlete.getDiscipline())
                .personalBest(athlete.getPersonalBest())
                .bio(athlete.getBio())
                .build();
    }

    /**
     * Updates an existing domain model with data from request DTO.
     *
     * @param athlete the existing domain model to update
     * @param request the request DTO with new data
     */
    public static void updateFromRequest(final Athlete athlete, final AthleteRequest request) {
        athlete.setFirstName(request.getFirstName());
        athlete.setLastName(request.getLastName());
        athlete.setBirthTimestamp(toTimestamp(request.getBirthDate()));
        athlete.setNationality(request.getNationality());
        athlete.setDiscipline(request.getDiscipline());
        athlete.setPersonalBest(request.getPersonalBest());
        athlete.setBio(request.getBio());
    }

    /**
     * Converts LocalDate to epoch milliseconds timestamp.
     *
     * @param date the LocalDate to convert
     * @return epoch milliseconds, or null if date is null
     */
    private static Long toTimestamp(final LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

    /**
     * Converts epoch milliseconds timestamp to LocalDate.
     *
     * @param timestamp the epoch milliseconds to convert
     * @return LocalDate in UTC, or null if timestamp is null
     */
    private static LocalDate toLocalDate(final Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC")).toLocalDate();
    }
}
