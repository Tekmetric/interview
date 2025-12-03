package com.interview.domain;

import com.interview.domain.dto.Address;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.interview.constant.PersonConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest {
    @Test
    void defaultConstructor() {
        var address =
                new Address(
                        ADDRESS_COUNTRY_CODE,
                        ADDRESS_POSTAL_CODE,
                        ADDRESS_ADMINISTRATIVE_AREA,
                        ADDRESS_LOCALITY,
                        ADDRESS_LINES
                );

        assertThat(address.countryCode()).isEqualTo(ADDRESS_COUNTRY_CODE);
        assertThat(address.postalCode()).isEqualTo(ADDRESS_POSTAL_CODE);
        assertThat(address.administrativeArea()).isEqualTo(ADDRESS_ADMINISTRATIVE_AREA);
        assertThat(address.locality()).isEqualTo(ADDRESS_LOCALITY);
        assertThat(address.addressLines()).isEqualTo(ADDRESS_LINES);
    }

    @Test
    void badCountryCode() {
        var exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new Address(
                                "USA",
                                ADDRESS_POSTAL_CODE,
                                ADDRESS_ADMINISTRATIVE_AREA,
                                ADDRESS_LOCALITY,
                                ADDRESS_LINES
                        )
                );

        assertThat(exception.getMessage()).isEqualTo("Invalid country code: USA");
    }

    @Test
    void missingPostalCode() {
        var exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new Address(
                                ADDRESS_COUNTRY_CODE,
                                "",
                                ADDRESS_ADMINISTRATIVE_AREA,
                                ADDRESS_LOCALITY,
                                ADDRESS_LINES
                        )
                );

        assertThat(exception.getMessage()).isEqualTo("No postal code provided");
    }

    @Test
    void missingAdministrativeArea() {
        var exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new Address(
                                ADDRESS_COUNTRY_CODE,
                                ADDRESS_POSTAL_CODE,
                                "",
                                ADDRESS_LOCALITY,
                                ADDRESS_LINES
                        )
                );

        assertThat(exception.getMessage()).isEqualTo("No administrative area provided");
    }

    @Test
    void missingLocality() {
        var exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new Address(
                                ADDRESS_COUNTRY_CODE,
                                ADDRESS_POSTAL_CODE,
                                ADDRESS_ADMINISTRATIVE_AREA,
                                "",
                                ADDRESS_LINES
                        )
                );

        assertThat(exception.getMessage()).isEqualTo("No locality provided");
    }

    @Test
    void missingAddressLines() {
        var exception =
                assertThrows(IllegalArgumentException.class, () ->
                        new Address(
                                ADDRESS_COUNTRY_CODE,
                                ADDRESS_POSTAL_CODE,
                                ADDRESS_ADMINISTRATIVE_AREA,
                                ADDRESS_LOCALITY,
                                List.of()
                        )
                );

        assertThat(exception.getMessage()).isEqualTo("No address lines provided");
    }

    @Test
    void stringify() {
        assertThat(ADDRESS.toString()).isEqualTo(
                String.format(
                        "Address[countryCode=%s, postalCode=%s, administrativeArea=%s, locality=%s, addressLines=%s]",
                        ADDRESS_COUNTRY_CODE,
                        ADDRESS_POSTAL_CODE,
                        ADDRESS_ADMINISTRATIVE_AREA,
                        ADDRESS_LOCALITY,
                        ADDRESS_LINES
                )
        );
    }
}
