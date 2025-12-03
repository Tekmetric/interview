package com.interview.domain.dto;

import com.interview.domain.entity.PersonEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.interview.constant.PersonConstants.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonTest {
    private PersonEntity baseEntity;

    @BeforeEach
    void setUp() {
        baseEntity = new PersonEntity();
        baseEntity.setId(ID);
        baseEntity.setEmail(EMAIL);
        baseEntity.setFirstName(FIRST_NAME);
        baseEntity.setLastName(LAST_NAME);
        baseEntity.setPhoneNumber(PHONE_NUMBER);
        baseEntity.setAddress(ADDRESS);
    }

    @Nested
    class FromEntity {

        @Test
        void success() {
            var person = Person.fromEntity(baseEntity);
            assertThat(person.email()).isEqualTo(EMAIL);
            assertThat(person.firstName()).isEqualTo(FIRST_NAME);
            assertThat(person.lastName()).isEqualTo(LAST_NAME);
            assertThat(person.phoneNumber()).isEqualTo(PHONE_NUMBER);
            assertThat(person.address()).isEqualTo(ADDRESS);
        }

        @Test
        void illegalAddress() {
            baseEntity.setAddressCountryCode("Aldova");
            var exception = assertThrows(IllegalArgumentException.class, () -> Person.fromEntity(baseEntity));
            assertThat(exception.getMessage()).isEqualTo("Invalid country code: Aldova");
        }
    }
}
