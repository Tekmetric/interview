package com.interview.domain.dto;

import java.util.List;

/**
 * Basic representation of an Address.
 *
 * @param countryCode        The ISO 3166-1 alpha-2 code for the country this address is based in
 * @param postalCode         The address's postal code
 * @param administrativeArea The administrative area (e.g. US state, Canadian province) this address is based in
 * @param locality           The locality (e.g. city) this address is based in
 * @param addressLines       Unstructured address lines describing the lower levels of an address. Usually consists of street address and unit number
 */
public record Address(
        String countryCode,
        String postalCode,
        String administrativeArea,
        String locality,
        List<String> addressLines
) {
    public Address {
        // address validations
        if (countryCode.length() != 2) {
            throw new IllegalArgumentException("Invalid country code: " + countryCode);
        }
        if (postalCode.isBlank()) {
            throw new IllegalArgumentException("No postal code provided");
        }
        if (administrativeArea.isBlank()) {
            throw new IllegalArgumentException("No administrative area provided");
        }
        if (locality.isBlank()) {
            throw new IllegalArgumentException("No locality provided");
        }
        if (addressLines.isEmpty()) {
            throw new IllegalArgumentException("No address lines provided");
        }
    }
}
