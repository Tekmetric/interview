package com.interview.repository.converter;

import com.interview.domain.PhoneNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PhoneNumberConverter implements AttributeConverter<PhoneNumber, String> {

    @Override
    public String convertToDatabaseColumn(PhoneNumber phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.phoneNumberString();
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String value) {
        return value == null ? null : new PhoneNumber(value);
    }
}
