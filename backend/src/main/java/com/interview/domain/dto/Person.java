package com.interview.domain.dto;

import com.interview.domain.entity.PersonEntity;

import java.util.UUID;

/**
 * Basic representation of a Person.
 *
 * @param id          The ID of the Person
 * @param email       The email of the Person. Unique
 * @param firstName   The first name of the Person
 * @param lastName    The last name of the Person
 * @param phoneNumber The phone number of the Person
 * @param address     The validated {@link Address} of the Person
 */
public record Person(
        UUID id,
        Email email,
        String firstName,
        String lastName,
        String phoneNumber,
        Address address
) {

    /**
     * @param entity {@link PersonEntity} to create the record from.
     */
    public static Person fromEntity(PersonEntity entity) throws IllegalArgumentException {
        return new Person(
                entity.getId(),
                new Email(entity.getEmail()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhoneNumber(),
                entity.getAddress()
        );
    }
}
