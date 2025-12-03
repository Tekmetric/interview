package com.interview.commands;

import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;

/**
 * Command for upserting a Person.
 *
 * @param email       The email of the new Person, or identifier for an existing Person
 * @param firstName   The first name of the Person to be upserted
 * @param lastName    The last name of the Person to be upserted
 * @param phoneNumber The phone number of the Person to be upserted
 * @param address     The address of the Person to be upserted
 */
public record UpsertPersonCommand(
        Email email,
        String firstName,
        String lastName,
        String phoneNumber,
        Address address
) {
}
