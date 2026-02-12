package com.interview.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.interview.enums.ContactMethod;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerResponse(
    Long id,
    Long version,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    LocalDate dateOfBirth,
    ContactMethod preferredContactMethod,
    LocalDateTime createdDate,
    LocalDateTime updatedDate,
    String createdBy,
    String updatedBy
) {}