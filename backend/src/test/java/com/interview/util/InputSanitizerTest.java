package com.interview.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InputSanitizer Unit Tests")
class InputSanitizerTest {

    @Nested
    @DisplayName("Sanitize Tests")
    class SanitizeTests {

        @Test
        @DisplayName("Should sanitize HTML tags")
        void shouldSanitizeHtmlTags() {
            String input = "<script>alert('xss')</script>";

            String result = InputSanitizer.sanitize(input);

            assertThat(result).isEqualTo("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;");
        }

        @Test
        @DisplayName("Should sanitize special characters")
        void shouldSanitizeSpecialCharacters() {
            String input = "Name & Company < > \" '";

            String result = InputSanitizer.sanitize(input);

            assertThat(result).isEqualTo("Name &amp; Company &lt; &gt; &quot; &#39;");
        }

        @Test
        @DisplayName("Should trim whitespace")
        void shouldTrimWhitespace() {
            String input = "  John Doe  ";

            String result = InputSanitizer.sanitize(input);

            assertThat(result).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should handle normal text without changes")
        void shouldHandleNormalText() {
            String input = "John Doe";

            String result = InputSanitizer.sanitize(input);

            assertThat(result).isEqualTo("John Doe");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should handle null, empty, and whitespace-only strings")
        void shouldHandleNullEmptyAndWhitespace(String input) {
            String result = InputSanitizer.sanitize(input);

            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "John", "Jane", "Mary-Jane", "O'Connor", "José", "Jean-Luc",
            "Anne Marie", "St. John", "van der Berg", "D'Angelo"
        })
        @DisplayName("Should accept valid names")
        void shouldAcceptValidNames(String name) {
            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "John123", "Jane@Doe", "Mary<script>", "John&Jane",
            "Test$Name", "Name#Tag", "User%Name", "Name+Tag",
            "Name=Value", "Name[0]", "Name{test}", "Name\\Path"
        })
        @DisplayName("Should reject names with invalid characters")
        void shouldRejectNamesWithInvalidCharacters(String name) {
            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should reject null, empty, and whitespace-only names")
        void shouldRejectNullEmptyAndWhitespaceNames(String name) {
            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should accept names with spaces")
        void shouldAcceptNamesWithSpaces() {
            String name = "John Doe";

            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept names with apostrophes")
        void shouldAcceptNamesWithApostrophes() {
            String name = "O'Connor";

            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept names with hyphens")
        void shouldAcceptNamesWithHyphens() {
            String name = "Mary-Jane";

            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should accept names with dots")
        void shouldAcceptNamesWithDots() {
            String name = "St. John";

            boolean result = InputSanitizer.isValidName(name);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "test@example.com", "user.name@domain.co.uk", "user+tag@example.org",
            "user123@test-domain.com", "a@b.co", "very.long.email.address@example.com"
        })
        @DisplayName("Should accept valid emails")
        void shouldAcceptValidEmails(String email) {
            boolean result = InputSanitizer.isValidEmail(email);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "invalid", "test@", "@example.com", "test@@example.com",
            "test.example.com", "test@example", "test @example.com",
            "test@exam ple.com", "<script>@example.com", "test@<script>.com"
        })
        @DisplayName("Should reject invalid emails")
        void shouldRejectInvalidEmails(String email) {
            boolean result = InputSanitizer.isValidEmail(email);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should reject null, empty, and whitespace-only emails")
        void shouldRejectNullEmptyAndWhitespaceEmails(String email) {
            boolean result = InputSanitizer.isValidEmail(email);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Phone Validation Tests")
    class PhoneValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "+1-555-0101", "555-0101", "(555) 123-4567", "+44 20 7946 0958",
            "555.123.4567", "555 123 4567", "15551234567", "+1 (555) 123-4567"
        })
        @DisplayName("Should accept valid phone numbers")
        void shouldAcceptValidPhoneNumbers(String phone) {
            boolean result = InputSanitizer.isValidPhone(phone);

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "555-CALL-NOW", "call me", "phone123abc", "555<script>",
            "555@domain.com", "555#extension", "555%discount"
        })
        @DisplayName("Should reject invalid phone numbers")
        void shouldRejectInvalidPhoneNumbers(String phone) {
            boolean result = InputSanitizer.isValidPhone(phone);

            assertThat(result).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should accept null, empty, and whitespace-only phones (optional field)")
        void shouldAcceptNullEmptyAndWhitespacePhones(String phone) {
            boolean result = InputSanitizer.isValidPhone(phone);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Unsafe Content Detection Tests")
    class UnsafeContentDetectionTests {

        @Test
        @DisplayName("Should detect HTML tags as unsafe")
        void shouldDetectHtmlTagsAsUnsafe() {
            String input = "<script>alert('xss')</script>";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should detect special characters as unsafe")
        void shouldDetectSpecialCharactersAsUnsafe() {
            String input = "Name & Company < >";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should not detect safe content as unsafe")
        void shouldNotDetectSafeContentAsUnsafe() {
            String input = "John Doe Company";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should handle null input safely")
        void shouldHandleNullInputSafely() {
            boolean result = InputSanitizer.containsUnsafeContent(null);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should detect quotes as unsafe")
        void shouldDetectQuotesAsUnsafe() {
            String input = "John \"Doe\" Company";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should detect apostrophes in general content as unsafe")
        void shouldDetectApostrophesAsUnsafe() {
            String input = "John's Company";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should handle content with only whitespace differences")
        void shouldHandleWhitespaceContent() {
            String input = "  John Doe  ";

            boolean result = InputSanitizer.containsUnsafeContent(input);

            assertThat(result).isFalse(); // Only whitespace difference, not unsafe content
        }
    }
}