package com.interview.domain.entity;

import com.interview.commands.CreatePersonCommand;
import com.interview.domain.dto.Address;
import com.interview.domain.dto.Email;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.interview.constant.PersonConstants.*;

class PersonEntityTest {

    @Test
    void fromCommand() {
        var command = new CreatePersonCommand(EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);

        var person = PersonEntity.from(command);

        assertThat(person.getEmail()).isEqualTo(EMAIL.value());
        assertThat(person.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(person.getLastName()).isEqualTo(LAST_NAME);
        assertThat(person.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(person.getAddress()).isEqualTo(ADDRESS);
    }

    @Test
    void stringify() {
        var person = new PersonEntity(ID, EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);

        assertThat(person.toString()).isEqualTo(
                String.format(
                        "Person[id=%s, email=%s, firstName=%s, lastName=%s, phoneNumber=%s, address=%s]",
                        ID,
                        EMAIL,
                        FIRST_NAME,
                        LAST_NAME,
                        PHONE_NUMBER,
                        ADDRESS
                )
        );
    }

    @Nested
    class EqualsAndHashCode {

        private static final PersonEntity DEFAULT_PERSON =
                new PersonEntity(ID, EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);

        @Test
        void equalsSelf() {
            assertThat(DEFAULT_PERSON).isEqualTo(DEFAULT_PERSON);
            //noinspection EqualsWithItself
            assertThat(DEFAULT_PERSON.equals(DEFAULT_PERSON)).isTrue();
            assertThat(DEFAULT_PERSON.hashCode()).isEqualTo(DEFAULT_PERSON.hashCode());
        }

        @Test
        void equalsSame() {
            var equivalentPerson = new PersonEntity(ID, EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);

            assertThat(DEFAULT_PERSON).isEqualTo(equivalentPerson);
            assertThat(DEFAULT_PERSON.equals(equivalentPerson)).isTrue();
            assertThat(DEFAULT_PERSON.hashCode()).isEqualTo(equivalentPerson.hashCode());
        }

        @Test
        void differentId() {
            var personWithDifferentId =
                    new PersonEntity(UUID.randomUUID(), EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, ADDRESS);

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentId);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentId)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentId.hashCode());
        }

        @Test
        void differentEmail() {
            var personWithDifferentEmail =
                    new PersonEntity(
                            ID,
                            new Email("jesty@testmetric.com"),
                            FIRST_NAME,
                            LAST_NAME,
                            PHONE_NUMBER,
                            ADDRESS
                    );

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentEmail);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentEmail)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentEmail.hashCode());
        }

        @Test
        void differentFirstName() {
            var personWithDifferentFirstName =
                    new PersonEntity(ID, EMAIL, "Jesty", LAST_NAME, PHONE_NUMBER, ADDRESS);

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentFirstName);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentFirstName)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentFirstName.hashCode());
        }

        @Test
        void differentLastName() {
            var personWithDifferentLastName =
                    new PersonEntity(ID, EMAIL, FIRST_NAME, "Jesterson", PHONE_NUMBER, ADDRESS);

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentLastName);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentLastName)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentLastName.hashCode());
        }

        @Test
        void differentAddress() {
            var differentAddress = new Address("US", "06901", "CT", "Stamford", List.of("888 Washington Blvd"));
            var personWithDifferentAddress =
                    new PersonEntity(ID, EMAIL, FIRST_NAME, LAST_NAME, PHONE_NUMBER, differentAddress);

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentAddress);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentAddress)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentAddress.hashCode());
        }

        @Test
        void differentPhoneNumber() {
            var personWithDifferentPhoneNumber =
                    new PersonEntity(ID, EMAIL, FIRST_NAME, LAST_NAME, "8329309401", ADDRESS);

            assertThat(DEFAULT_PERSON).isNotEqualTo(personWithDifferentPhoneNumber);
            assertThat(DEFAULT_PERSON.equals(personWithDifferentPhoneNumber)).isFalse();
            assertThat(DEFAULT_PERSON.hashCode()).isNotEqualTo(personWithDifferentPhoneNumber.hashCode());
        }
    }
}
