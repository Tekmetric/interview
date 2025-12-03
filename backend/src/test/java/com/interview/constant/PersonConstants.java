package com.interview.constant;

import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;

import java.util.List;
import java.util.UUID;

/**
 * Constants for testing Person behavior.
 */
public class PersonConstants {
    public static final UUID ID = UUID.randomUUID();
    public static final Email EMAIL = new Email("testy@testmetric.com");
    public static final String FIRST_NAME = "Testy";
    public static final String LAST_NAME = "Testerson";
    public static final String PHONE_NUMBER = "8329309400";

    public static final String ADDRESS_COUNTRY_CODE = "US";
    public static final String ADDRESS_POSTAL_CODE = "77024";
    public static final String ADDRESS_ADMINISTRATIVE_AREA = "TX";
    public static final String ADDRESS_LOCALITY = "Houston";
    public static final List<String> ADDRESS_LINES = List.of("730 Town and Country Blvd", "Suite 300");
    public static final Address ADDRESS =
            new Address(
                    ADDRESS_COUNTRY_CODE,
                    ADDRESS_POSTAL_CODE,
                    ADDRESS_ADMINISTRATIVE_AREA,
                    ADDRESS_LOCALITY,
                    ADDRESS_LINES
            );
}
