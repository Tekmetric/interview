package com.interview.commands;

import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;

/**
 * Command for creating a Person.
 *
 * @param email       The email of the new Person. Must be unique
 * @param firstName   The first name of the new Person
 * @param lastName    The last name of the new Person
 * @param phoneNumber The phone number of the new Person
 * @param address     The address of the new Person
 */
public record CreatePersonCommand(
        Email email,
        String firstName,
        String lastName,
        String phoneNumber,
        Address address
) {
}
