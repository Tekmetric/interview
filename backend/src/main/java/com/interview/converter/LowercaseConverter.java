package com.interview.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class LowercaseConverter implements AttributeConverter<String, String> {


    @Override
    public String convertToDatabaseColumn(String attribute) {
        //lowercase and trim if value is present
        return null != attribute ? attribute.trim().toLowerCase() : null;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        //nothing to do here
        return dbData;
    }

}
