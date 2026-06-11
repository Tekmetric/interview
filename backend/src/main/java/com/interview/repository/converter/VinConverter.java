package com.interview.repository.converter;

import com.interview.domain.Vin;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VinConverter implements AttributeConverter<Vin, String> {

    @Override
    public String convertToDatabaseColumn(Vin vin) {
        return vin == null ? null : vin.vinString();
    }

    @Override
    public Vin convertToEntityAttribute(String value) {
        return value == null ? null : new Vin(value);
    }
}
