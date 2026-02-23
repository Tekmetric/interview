package com.interview.converter;

import com.interview.entity.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class GenderConverter implements AttributeConverter<Gender, String> {

    @Override
    public String convertToDatabaseColumn(Gender attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCode());
    }

    @Override
    public Gender convertToEntityAttribute(String dbData) {
        return Gender.fromCode(dbData);
    }
}
