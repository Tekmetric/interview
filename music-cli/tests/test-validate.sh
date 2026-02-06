#!/usr/bin/env bash
#
# test-validate.sh - Unit tests for validation functions
#

# Source test framework
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/test-common.sh"

# Test validate_date function
function test_validate_date_valid() {
    if validate_date "2021-01-31"; then
        pass "Valid date 2021-01-31"
    else
        fail "Valid date 2021-01-31 should pass"
    fi
}

function test_validate_date_invalid_format() {
    if ! validate_date "2021/01/31"; then
        pass "Invalid date format 2021/01/31"
    else
        fail "Invalid date format 2021/01/31 should fail"
    fi
}

function test_validate_date_invalid_month() {
    if ! validate_date "2021-13-01"; then
        pass "Invalid month 13"
    else
        fail "Invalid month 13 should fail"
    fi
}

function test_validate_date_invalid_day() {
    if ! validate_date "2021-02-30"; then
        pass "Invalid day 30 in February"
    else
        fail "Invalid day 30 in February should fail"
    fi
}

# Test validate_positive_integer function
function test_validate_positive_integer_valid() {
    if validate_positive_integer "42"; then
        pass "Valid positive integer 42"
    else
        fail "Valid positive integer 42 should pass"
    fi
}

function test_validate_positive_integer_zero() {
    if ! validate_positive_integer "0"; then
        pass "Zero is not positive"
    else
        fail "Zero should fail positive integer validation"
    fi
}

function test_validate_positive_integer_negative() {
    if ! validate_positive_integer "-5"; then
        pass "Negative number is not positive"
    else
        fail "Negative number should fail positive integer validation"
    fi
}

function test_validate_positive_integer_not_number() {
    if ! validate_positive_integer "abc"; then
        pass "String is not a number"
    else
        fail "String should fail positive integer validation"
    fi
}

# Test validate_non_empty function
function test_validate_non_empty_valid() {
    if validate_non_empty "Hello World"; then
        pass "Non-empty string is valid"
    else
        fail "Non-empty string should pass"
    fi
}

function test_validate_non_empty_empty() {
    if ! validate_non_empty ""; then
        pass "Empty string fails validation"
    else
        fail "Empty string should fail validation"
    fi
}

function test_validate_non_empty_whitespace() {
    if ! validate_non_empty "   "; then
        pass "Whitespace-only string fails validation"
    else
        fail "Whitespace-only string should fail validation"
    fi
}

# Test validate_entity_type function
function test_validate_entity_type_artist() {
    if validate_entity_type "ARTIST"; then
        pass "ARTIST is valid entity type"
    else
        fail "ARTIST should be valid entity type"
    fi
}

function test_validate_entity_type_song() {
    if validate_entity_type "SONG"; then
        pass "SONG is valid entity type"
    else
        fail "SONG should be valid entity type"
    fi
}

function test_validate_entity_type_album() {
    if validate_entity_type "ALBUM"; then
        pass "ALBUM is valid entity type"
    else
        fail "ALBUM should be valid entity type"
    fi
}

function test_validate_entity_type_lowercase() {
    if validate_entity_type "artist"; then
        pass "Lowercase artist is valid"
    else
        fail "Lowercase artist should be valid"
    fi
}

function test_validate_entity_type_invalid() {
    if ! validate_entity_type "INVALID"; then
        pass "INVALID is not a valid entity type"
    else
        fail "INVALID should not be valid entity type"
    fi
}

# Test validate_url function
function test_validate_url_http() {
    if validate_url "http://localhost:8080"; then
        pass "HTTP URL is valid"
    else
        fail "HTTP URL should be valid"
    fi
}

function test_validate_url_https() {
    if validate_url "https://api.example.com:443/path"; then
        pass "HTTPS URL is valid"
    else
        fail "HTTPS URL should be valid"
    fi
}

function test_validate_url_invalid() {
    if ! validate_url "not-a-url"; then
        pass "Invalid URL fails validation"
    else
        fail "Invalid URL should fail validation"
    fi
}

# Test format_duration function
function test_format_duration_seconds() {
    local result
    result=$(format_duration 45)

    if assert_equals "0:45" "$result" "Format 45 seconds"; then
        pass "Format 45 seconds"
    else
        fail "Format 45 seconds"
    fi
}

function test_format_duration_minutes() {
    local result
    result=$(format_duration 185)

    if assert_equals "3:05" "$result" "Format 185 seconds (3:05)"; then
        pass "Format 185 seconds"
    else
        fail "Format 185 seconds"
    fi
}

function test_format_duration_hours() {
    local result
    result=$(format_duration 3665)

    if assert_equals "1:01:05" "$result" "Format 3665 seconds (1:01:05)"; then
        pass "Format 3665 seconds"
    else
        fail "Format 3665 seconds"
    fi
}

# Run all tests
echo "$(color_bold "Running Validation Tests")"
echo ""

run_test test_validate_date_valid
run_test test_validate_date_invalid_format
run_test test_validate_date_invalid_month
run_test test_validate_date_invalid_day

run_test test_validate_positive_integer_valid
run_test test_validate_positive_integer_zero
run_test test_validate_positive_integer_negative
run_test test_validate_positive_integer_not_number

run_test test_validate_non_empty_valid
run_test test_validate_non_empty_empty
run_test test_validate_non_empty_whitespace

run_test test_validate_entity_type_artist
run_test test_validate_entity_type_song
run_test test_validate_entity_type_album
run_test test_validate_entity_type_lowercase
run_test test_validate_entity_type_invalid

run_test test_validate_url_http
run_test test_validate_url_https
run_test test_validate_url_invalid

run_test test_format_duration_seconds
run_test test_format_duration_minutes
run_test test_format_duration_hours

# Show results
show_results
