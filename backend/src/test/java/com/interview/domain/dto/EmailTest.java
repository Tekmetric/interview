package com.interview.domain.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {
    @ParameterizedTest
    @FieldSource("VALID_EMAILS")
    void success(String validEmail) {
        var email = new Email(validEmail);

        assertThat(email.value()).isEqualTo(validEmail.toLowerCase());
    }

    @ParameterizedTest
    @FieldSource("INVALID_EMAILS")
    void badEmail(String invalidEmail) {
        var exception =
                assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));

        assertThat(exception.getMessage()).isEqualTo("Invalid email: " + invalidEmail);
    }

    private static final List<String> VALID_EMAILS =
            List.of(
                    "user@example.com",             // standard
                    "user.name@example.com",        // dot in local
                    "user_name@example.com",        // underscore in local
                    "user-name@example.com",        // hyphen in local
                    "user@example.co.uk",           // multi-part TLD
                    "user@sub.example.com",         // subdomain
                    "u@example.com",                // single-character local
                    "user123@example.com",          // digits in local
                    "USER@example.com",             // uppercase local
                    "user@EXAMPLE.COM",             // uppercase domain
                    "user@domain.info",             // uncommon but valid TLD
                    "user@domain.travel",           // longer TLD
                    "user@domain.io"                // short modern TLD
            );

    private static final List<String> INVALID_EMAILS =
            List.of(
                    "userexample.com",        // missing @
                    "user@@example.com",      // double @
                    "@example.com",           // empty local part
                    "user@",                  // empty domain
                    "user name@example.com",  // space in local
                    "user@.example.com",      // domain starts with dot
                    "user@example..com",      // consecutive dots in domain
                    "user@example.com.",      // domain ends with dot
                    "user@example",           // missing TLD
                    "user@example.c",         // TLD too short
                    "user@192.168.1.1",       // raw IP without brackets
                    " user@example.com",      // leading space
                    "user@example.com ",      // trailing space
                    "http://example.com"      // URL instead of email
            );

    @Nested
    class OfString {
        @Test
        void success() {
            var value = "user@example.com";
            var email = new Email(value);

            assertThat(email.value()).isEqualTo(value);
        }

        @Test
        void setLowerCase() {
            var value = "USER@example.com";
            var email = new Email(value);

            assertThat(email.value()).isEqualTo(value.toLowerCase());
        }
    }
}
