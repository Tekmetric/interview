package com.interview.domain.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

/**
 * Value class wrapping a Person's email. Validates upon creation.
 *
 * @param value The underlying email value
 */
@JsonSerialize(using = Email.Serializer.class)
@JsonDeserialize(using = Email.Deserializer.class)
public record Email(String value) {

    public Email {
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
    }

    @Override
    @JsonSerialize
    public String value() {
        return value.toLowerCase();
    }

    private static final String EMAIL_REGEX =
            "^[\\w\\-.]+@([\\w-]+\\.)+[\\w-]{2,}$";

    @Override
    public String toString() {
        return value();
    }

    // custom serialize and deserialize objects

    static class Serializer extends JsonSerializer<Email> {
        @Override
        public void serialize(Email email, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(email.value());
        }
    }

    static class Deserializer extends JsonDeserializer<Email> {
        @Override
        public Email deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return new Email(p.getValueAsString());
        }
    }
}
