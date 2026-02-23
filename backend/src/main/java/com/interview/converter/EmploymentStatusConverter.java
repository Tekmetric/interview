package com.interview.converter;

import com.interview.entity.EmploymentStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class EmploymentStatusConverter implements AttributeConverter<EmploymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(EmploymentStatus attribute) {
        return attribute == null ? null : String.valueOf(attribute.getCode());
    }

    @Override
    public EmploymentStatus convertToEntityAttribute(String dbData) {
        return EmploymentStatus.fromCode(dbData);
    }
}
