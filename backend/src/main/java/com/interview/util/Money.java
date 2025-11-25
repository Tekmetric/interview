package com.interview.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.math.BigDecimal;

import lombok.Value;

/**
 * Utility type to represent a monetary amount using a long for performance.
 * This is basically an alternative to FastMoney from JSR-354.
 *
 * @see JpaConverter
 * @see JsonDeserializer
 */
@Value
public class Money {

    @JsonValue
    long rawAmount;

    /**
     * Convert to USD using {@link BigDecimal}.
     */
    public BigDecimal toUsd() {
        return new BigDecimal(rawAmount).movePointLeft(2);
    }

    @Override
    public String toString() {
        return "$" + toUsd().toString();
    }

    /**
     * Custom JPA converter for {@link Money} to atomic unit, e.g., cents.
     */
    @Converter
    public static class JpaConverter implements AttributeConverter<Money, Long> {
        @Override
        public Long convertToDatabaseColumn(final Money money) {
            return money != null ? money.getRawAmount() : null;
        }

        @Override
        public Money convertToEntityAttribute(final Long value) {
            return value != null ? new Money(value) : null;
        }
    }

    /**
     * Custom JSON deserializer for {@link Money} from atomic unit, e.g., cents.
     */
    public static class JsonDeserializer extends StdScalarDeserializer<Money> {

        protected JsonDeserializer() {
            super(Money.class);
        }

        @Override
        public Money deserialize(final JsonParser parser, final DeserializationContext ctx) throws IOException {
            final JsonToken token = parser.currentToken();
            if (token == JsonToken.VALUE_NUMBER_INT) {
                return new Money(parser.getLongValue());
            } else if (token == JsonToken.VALUE_NULL) {
                return null;
            } else {
                throw new InvalidFormatException(parser, "Unexpected token", token, _valueClass);
            }
        }
    }
}
