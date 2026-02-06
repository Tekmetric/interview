#!/usr/bin/env bash
#
# test-common.sh - Test framework and utilities
#

# Detect repository root from test directory
TEST_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$TEST_DIR/.." && pwd)"

# Source libraries
source "$REPO_ROOT/lib/colors.sh"
source "$REPO_ROOT/lib/common.sh"
source "$REPO_ROOT/lib/validate.sh"
source "$REPO_ROOT/lib/format.sh"
source "$REPO_ROOT/lib/api.sh"

# Test tracking variables
TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0

# Test result tracking
declare -a FAILED_TESTS=()

# Assertion functions
function assert_equals() {
    local expected="$1"
    local actual="$2"
    local message="${3:-Assertion failed}"

    if [[ "$expected" == "$actual" ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        echo "    Expected: $expected"
        echo "    Actual:   $actual"
        return 1
    fi
}

function assert_contains() {
    local haystack="$1"
    local needle="$2"
    local message="${3:-Assertion failed}"

    if [[ "$haystack" =~ $needle ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        echo "    String does not contain: $needle"
        echo "    Actual: $haystack"
        return 1
    fi
}

function assert_not_contains() {
    local haystack="$1"
    local needle="$2"
    local message="${3:-Assertion failed}"

    if [[ ! "$haystack" =~ $needle ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        echo "    String should not contain: $needle"
        echo "    Actual: $haystack"
        return 1
    fi
}

function assert_exit_code() {
    local expected="$1"
    local actual="$2"
    local message="${3:-Exit code assertion failed}"

    if [[ "$expected" -eq "$actual" ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        echo "    Expected exit code: $expected"
        echo "    Actual exit code:   $actual"
        return 1
    fi
}

function assert_true() {
    local condition="$1"
    local message="${2:-Assertion failed: condition is false}"

    if [[ "$condition" == "true" ]] || [[ "$condition" -eq 1 ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        return 1
    fi
}

function assert_false() {
    local condition="$1"
    local message="${2:-Assertion failed: condition is true}"

    if [[ "$condition" == "false" ]] || [[ "$condition" -eq 0 ]]; then
        return 0
    else
        echo "  $(color_error "✗") $message"
        return 1
    fi
}

# Test execution functions
function run_test() {
    local test_name="$1"

    ((TESTS_RUN++))

    echo "$(color_info "Running:") $test_name"

    if "$test_name"; then
        ((TESTS_PASSED++))
        echo "  $(color_success "✓") Passed"
    else
        ((TESTS_FAILED++))
        FAILED_TESTS+=("$test_name")
        echo "  $(color_error "✗") Failed"
    fi

    echo ""
}

# Show test results summary
function show_results() {
    echo "=========================================="
    echo "Test Results:"
    echo "  Total:  $TESTS_RUN"
    echo "  $(color_success "Passed: $TESTS_PASSED")"

    if [[ $TESTS_FAILED -gt 0 ]]; then
        echo "  $(color_error "Failed: $TESTS_FAILED")"
        echo ""
        echo "Failed tests:"
        for test in "${FAILED_TESTS[@]}"; do
            echo "  - $test"
        done
        echo "=========================================="
        exit 1
    else
        echo "=========================================="
        echo "$(color_success "All tests passed!")"
        exit 0
    fi
}

# Setup and teardown functions
function setup_test_env() {
    # Set test configuration
    export MUSIC_API_URL="${MUSIC_API_URL:-http://localhost:8080}"
    export NO_COLOR="${NO_COLOR:-false}"

    # Load config
    load_config
}

function cleanup_test_data() {
    # Function to clean up test data
    # To be implemented based on test needs
    :
}

# Helper function to pass a test (just for readability)
function pass() {
    local message="${1:-Test passed}"
    return 0
}

# Helper function to fail a test
function fail() {
    local message="${1:-Test failed}"
    echo "  $(color_error "✗") $message"
    return 1
}

# Initialize test environment
setup_test_env
