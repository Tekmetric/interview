package com.interview.repository.converter;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.domain.Vin;
import org.junit.jupiter.api.Test;

class VinConverterTest {

    private final VinConverter converter = new VinConverter();

    @Test
    void convertToDatabaseColumnReturnsVinString() {
        final Vin vin = new Vin("1HGBH41JXMN109186");
        assertThat(converter.convertToDatabaseColumn(vin)).isEqualTo("1HGBH41JXMN109186");
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsVin() {
        final Vin result = converter.convertToEntityAttribute("1HGBH41JXMN109186");
        assertThat(result).isEqualTo(new Vin("1HGBH41JXMN109186"));
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
