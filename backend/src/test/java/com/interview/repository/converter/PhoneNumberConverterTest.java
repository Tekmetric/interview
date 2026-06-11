package com.interview.repository.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.PhoneNumber;
import org.junit.jupiter.api.Test;

class PhoneNumberConverterTest {

    private final PhoneNumberConverter converter = new PhoneNumberConverter();

    @Test
    void convertToDatabaseColumnReturnsPhoneNumberString() {
        final PhoneNumber phoneNumber = new PhoneNumber("(555) 867-5309");
        assertThat(converter.convertToDatabaseColumn(phoneNumber)).isEqualTo("(555) 867-5309");
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsPhoneNumber() {
        final PhoneNumber result = converter.convertToEntityAttribute("(555) 867-5309");
        assertThat(result).isEqualTo(new PhoneNumber("(555) 867-5309"));
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
