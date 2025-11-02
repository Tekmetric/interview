package com.interview.models.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.models.user.Gender;
import com.interview.models.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public record UserResponse(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String username,
        LocalDate dateOfBirth,
        Integer age,
        Gender gender,
        String email,
        String phoneNumber,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {
    // Static factory method to create from User entity
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getUsername(),
                user.getDateOfBirth(),
                calculateAge(user.getDateOfBirth()),
                user.getGender(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private static Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}

