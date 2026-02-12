package com.interview.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SafeTextValidator Unit Tests")
class SafeTextValidatorTest {

    @Mock
    private SafeText mockAnnotation;

    @Mock
    private ConstraintValidatorContext mockContext;

    private SafeTextValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SafeTextValidator();
    }

    @Test
    @DisplayName("Should accept null values (let @NotBlank handle null validation)")
    void shouldAcceptNullValues() {
        when(mockAnnotation.type()).thenReturn(SafeText.TextType.GENERAL);
        validator.initialize(mockAnnotation);

        boolean result = validator.isValid(null, mockContext);

        assertThat(result).isTrue();
    }

    @Nested
    @DisplayName("NAME Type Validation Tests")
    class NameTypeTests {

        @BeforeEach
        void setUp() {
            when(mockAnnotation.type()).thenReturn(SafeText.TextType.NAME);
            validator.initialize(mockAnnotation);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "John", "José", "Mary-Jane", "O'Connor", "Jean-Luc",
            "Anne Marie", "St. John", "van der Berg", "D'Angelo"
        })
        @DisplayName("Should accept valid names")
        void shouldAcceptValidNames(String name) {
            boolean result = validator.isValid(name, mockContext);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "John123", "Jane@Doe", "Mary<script>", "John&Jane",
            "Test$Name", "Name#Tag",
        })
        @DisplayName("Should reject invalid names")
        void shouldRejectInvalidNames(String name) {
            boolean result = validator.isValid(name, mockContext);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @EmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should reject empty/whitespace names")
        void shouldRejectEmptyNames(String name) {
            boolean result = validator.isValid(name, mockContext);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("EMAIL Type Validation Tests")
    class EmailTypeTests {

        @BeforeEach
        void setUp() {
            when(mockAnnotation.type()).thenReturn(SafeText.TextType.EMAIL);
            validator.initialize(mockAnnotation);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com", "user.name@domain.co.uk", "user+tag@example.org",
            "user123@test-domain.com"
        })
        @DisplayName("Should accept valid emails")
        void shouldAcceptValidEmails(String email) {
            boolean result = validator.isValid(email, mockContext);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid", "test@", "@example.com", "test@@example.com",
            "test.example.com", "<script>@example.com"
        })
        @DisplayName("Should reject invalid emails")
        void shouldRejectInvalidEmails(String email) {
            boolean result = validator.isValid(email, mockContext);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("PHONE Type Validation Tests")
    class PhoneTypeTests {

        @BeforeEach
        void setUp() {
            when(mockAnnotation.type()).thenReturn(SafeText.TextType.PHONE);
            validator.initialize(mockAnnotation);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "+1-555-0101", "555-0101", "(555) 123-4567", "555.123.4567",
            "555 123 4567", "15551234567"
        })
        @DisplayName("Should accept valid phone numbers")
        void shouldAcceptValidPhoneNumbers(String phone) {
            boolean result = validator.isValid(phone, mockContext);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "555-CALL-NOW", "call me", "phone123abc", "555<script>",
            "555@domain.com"
        })
        @DisplayName("Should reject invalid phone numbers")
        void shouldRejectInvalidPhoneNumbers(String phone) {
            boolean result = validator.isValid(phone, mockContext);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Should accept empty phone numbers (optional field)")
        void shouldAcceptEmptyPhoneNumbers(String phone) {
            boolean result = validator.isValid(phone, mockContext);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("ADDRESS Type Validation Tests")
    class AddressTypeTests {

        @BeforeEach
        void setUp() {
            when(mockAnnotation.type()).thenReturn(SafeText.TextType.ADDRESS);
            validator.initialize(mockAnnotation);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "123 Main St", "456 Oak Ave, Apt 2B", "789 Pine Rd, City, State 12345",
            "Normal address text"
        })
        @DisplayName("Should accept safe address text")
        void shouldAcceptSafeAddressText(String address) {
            boolean result = validator.isValid(address, mockContext);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "<script>alert('xss')</script>", "Address & Company < >",
            "123 Main St <img src=x onerror=alert(1)>"
        })
        @DisplayName("Should reject unsafe address content")
        void shouldRejectUnsafeAddressContent(String address) {
            boolean result = validator.isValid(address, mockContext);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("GENERAL Type Validation Tests")
    class GeneralTypeTests {

        @BeforeEach
        void setUp() {
            when(mockAnnotation.type()).thenReturn(SafeText.TextType.GENERAL);
            validator.initialize(mockAnnotation);
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "Normal text", "Text with numbers 123", "Text-with-hyphens",
            "Text_with_underscores", "Text with (parentheses)"
        })
        @DisplayName("Should accept safe general text")
        void shouldAcceptSafeGeneralText(String text) {
            boolean result = validator.isValid(text, mockContext);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "<script>alert('xss')</script>", "Text & Company < >",
            "Text with \"quotes\"", "Text with 'apostrophes'"
        })
        @DisplayName("Should reject unsafe general content")
        void shouldRejectUnsafeGeneralContent(String text) {
            boolean result = validator.isValid(text, mockContext);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should accept empty general text")
        void shouldAcceptEmptyGeneralText() {
            boolean result = validator.isValid("", mockContext);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Cross-Type Consistency Tests")
    class CrossTypeConsistencyTests {

        @Test
        @DisplayName("Should consistently handle XSS attempts across all types")
        void shouldHandleXssConsistently() {
            String xssAttempt = "<script>alert('xss')</script>";
            SafeText.TextType[] dangerousTypes = {
                SafeText.TextType.NAME, SafeText.TextType.EMAIL,
                SafeText.TextType.PHONE, SafeText.TextType.ADDRESS,
                SafeText.TextType.GENERAL
            };

            for (SafeText.TextType type : dangerousTypes) {
                when(mockAnnotation.type()).thenReturn(type);
                validator.initialize(mockAnnotation);

                boolean result = validator.isValid(xssAttempt, mockContext);

                assertThat(result)
                    .as("XSS attempt should be rejected for type: " + type)
                    .isFalse();
            }
        }

        @Test
        @DisplayName("Should handle null consistently across all types")
        void shouldHandleNullConsistently() {
            SafeText.TextType[] allTypes = SafeText.TextType.values();

            for (SafeText.TextType type : allTypes) {
                when(mockAnnotation.type()).thenReturn(type);
                validator.initialize(mockAnnotation);

                boolean result = validator.isValid(null, mockContext);

                assertThat(result)
                    .as("Null should be accepted for type: " + type)
                    .isTrue();
            }
        }
    }
}