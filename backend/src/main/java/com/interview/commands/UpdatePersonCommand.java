package com.interview.commands;

import com.interview.domain.dto.Address;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Command for updating a Person.
 *
 * @param id          The ID of the Person to be updated
 * @param firstName   The updated first name, or {@code null}
 * @param lastName    The updated last name, or {@code null}
 * @param phoneNumber The updated phone number, or {@code null}
 * @param address     The updated address, or {@code null}
 */
public record UpdatePersonCommand(
        UUID id,
        @Nullable String firstName,
        @Nullable String lastName,
        @Nullable String phoneNumber,
        @Nullable Address address
) {
}
