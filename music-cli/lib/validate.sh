#!/usr/bin/env bash
#
# validate.sh - Input validation functions
#

# Validate date format (YYYY-MM-DD)
# Returns 0 if valid, 1 if invalid
function validate_date() {
    local date="$1"

    # Check format with regex
    if [[ ! "$date" =~ ^[0-9]{4}-[0-9]{2}-[0-9]{2}$ ]]; then
        return 1
    fi

    # Extract components
    local year="${date:0:4}"
    local month="${date:5:2}"
    local day="${date:8:2}"

    # Validate ranges (force base-10 interpretation to handle leading zeros)
    if [[ $((10#$month)) -lt 1 ]] || [[ $((10#$month)) -gt 12 ]]; then
        return 1
    fi

    if [[ $((10#$day)) -lt 1 ]] || [[ $((10#$day)) -gt 31 ]]; then
        return 1
    fi

    # Basic month-day validation
    if [[ $((10#$month)) -eq 2 ]] && [[ $((10#$day)) -gt 29 ]]; then
        return 1
    fi

    if [[ "$month" =~ ^(04|06|09|11)$ ]] && [[ $((10#$day)) -gt 30 ]]; then
        return 1
    fi

    return 0
}

# Validate positive integer
# Returns 0 if valid, 1 if invalid
function validate_positive_integer() {
    local value="$1"

    # Check if it's a number
    if [[ ! "$value" =~ ^[0-9]+$ ]]; then
        return 1
    fi

    # Check if it's positive (greater than 0)
    if [[ "$value" -le 0 ]]; then
        return 1
    fi

    return 0
}

# Validate non-negative integer (0 or positive)
# Returns 0 if valid, 1 if invalid
function validate_non_negative_integer() {
    local value="$1"

    # Check if it's a number
    if [[ ! "$value" =~ ^[0-9]+$ ]]; then
        return 1
    fi

    return 0
}

# Validate non-empty string
# Returns 0 if valid, 1 if invalid
function validate_non_empty() {
    local value="$1"

    if [[ -z "$value" ]] || [[ "$value" =~ ^[[:space:]]*$ ]]; then
        return 1
    fi

    return 0
}

# Validate ID format (positive integer)
# Returns 0 if valid, 1 if invalid
function validate_id() {
    validate_positive_integer "$1"
}

# Validate entity type (ARTIST, SONG, or ALBUM)
# Returns 0 if valid, 1 if invalid
function validate_entity_type() {
    local type="$1"

    # Convert to uppercase for comparison
    type=$(echo "$type" | tr '[:lower:]' '[:upper:]')

    case "$type" in
        ARTIST|SONG|ALBUM)
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Validate URL format
# Returns 0 if valid, 1 if invalid
function validate_url() {
    local url="$1"

    # Basic URL validation (http or https)
    if [[ "$url" =~ ^https?://[a-zA-Z0-9./_-]+(:[0-9]+)?(/[a-zA-Z0-9./_-]*)?$ ]]; then
        return 0
    fi

    return 1
}

# Validate comma-separated list of IDs
# Returns 0 if valid, 1 if invalid
function validate_id_list() {
    local id_list="$1"

    # Empty list is valid
    if [[ -z "$id_list" ]]; then
        return 0
    fi

    # Split by comma and validate each ID
    IFS=',' read -ra ids <<< "$id_list"
    for id in "${ids[@]}"; do
        # Trim whitespace
        id="${id// /}"

        if ! validate_positive_integer "$id"; then
            return 1
        fi
    done

    return 0
}

# Validate output format (formatted or json)
# Returns 0 if valid, 1 if invalid
function validate_output_format() {
    local format="$1"

    case "$format" in
        formatted|json)
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Show validation error message
function validation_error() {
    local field="$1"
    local message="$2"

    log_error "Validation failed for $field: $message"
}

# Validate and show error if invalid
# Returns 0 if valid, 1 if invalid (and shows error)
function validate_date_or_error() {
    local date="$1"
    local field_name="${2:-date}"

    if ! validate_date "$date"; then
        validation_error "$field_name" "Invalid date format '$date'. Expected format: YYYY-MM-DD (e.g., 2021-01-31)"
        return 1
    fi

    return 0
}

function validate_positive_integer_or_error() {
    local value="$1"
    local field_name="${2:-value}"

    if ! validate_positive_integer "$value"; then
        validation_error "$field_name" "Invalid value '$value'. Must be a positive integer (greater than 0)"
        return 1
    fi

    return 0
}

function validate_non_empty_or_error() {
    local value="$1"
    local field_name="${2:-field}"

    if ! validate_non_empty "$value"; then
        validation_error "$field_name" "Value cannot be empty"
        return 1
    fi

    return 0
}

function validate_id_or_error() {
    local id="$1"
    local field_name="${2:-ID}"

    if ! validate_id "$id"; then
        validation_error "$field_name" "Invalid ID '$id'. Must be a positive integer"
        return 1
    fi

    return 0
}

function validate_url_or_error() {
    local url="$1"
    local field_name="${2:-URL}"

    if ! validate_url "$url"; then
        validation_error "$field_name" "Invalid URL format '$url'. Must be http:// or https://"
        return 1
    fi

    return 0
}
