#!/usr/bin/env bash
#
# common.sh - Shared utilities and constants
#

# Exit codes
readonly EXIT_SUCCESS=0
readonly EXIT_ERROR=1
readonly EXIT_USAGE=2
readonly EXIT_DEPENDENCY=3
readonly EXIT_SERVER=4

# Default configuration values
readonly DEFAULT_API_URL="http://localhost:8080"
readonly DEFAULT_PAGE_SIZE=20
readonly DEFAULT_OUTPUT_FORMAT="formatted"

# Global variables (will be set by load_config)
API_URL=""
PAGE_SIZE=""
OUTPUT_FORMAT=""

# Dependency check cache (to avoid checking multiple times)
DEPENDENCIES_CHECKED=false

# Logging functions
function log_error() {
    echo "$(color_error "ERROR:") $*" >&2
}

function log_success() {
    if [[ "${OUTPUT_FORMAT:-formatted}" == "json" ]]; then
        echo "$(color_success "✓") $*" >&2
    else
        echo "$(color_success "✓") $*"
    fi
}

function log_warning() {
    echo "$(color_warning "WARNING:") $*" >&2
}

function log_info() {
    if [[ "${OUTPUT_FORMAT:-formatted}" == "json" ]]; then
        echo "$(color_info "ℹ") $*" >&2
    else
        echo "$(color_info "ℹ") $*"
    fi
}

function log_debug() {
    if [[ "${DEBUG:-false}" == "true" ]]; then
        echo "$(color_highlight "DEBUG:") $*" >&2
    fi
}

# Check for required dependencies
function check_dependencies() {
    # Return early if already checked
    if [[ "$DEPENDENCIES_CHECKED" == "true" ]]; then
        return 0
    fi

    local missing=()
    local optional_missing=()

    # Required dependencies
    command -v curl >/dev/null 2>&1 || missing+=("curl")
    command -v jq >/dev/null 2>&1 || missing+=("jq")

    # Optional dependencies (for better formatting)
    command -v column >/dev/null 2>&1 || optional_missing+=("column")

    # Check bash version (need 3.2+, recommend 4.0+)
    if [[ -n "${BASH_VERSION}" ]]; then
        local bash_major="${BASH_VERSION%%.*}"
        if [[ "$bash_major" -lt 3 ]]; then
            log_error "Bash 3.2 or higher required (found: $BASH_VERSION)"
            exit $EXIT_DEPENDENCY
        fi
        if [[ "$bash_major" -eq 3 ]]; then
            local bash_minor="${BASH_VERSION#*.}"
            bash_minor="${bash_minor%%.*}"
            if [[ "$bash_minor" -lt 2 ]]; then
                log_error "Bash 3.2 or higher required (found: $BASH_VERSION)"
                exit $EXIT_DEPENDENCY
            fi
            log_debug "Bash 3.x detected. Some features may be limited. Bash 4.0+ recommended."
        fi
    fi

    if [[ ${#missing[@]} -gt 0 ]]; then
        log_error "Missing required dependencies: ${missing[*]}"
        echo ""
        echo "Install with:"

        # Detect OS and provide appropriate install command
        if [[ "$(uname)" == "Darwin" ]]; then
            echo "  macOS:  brew install ${missing[*]}"
        elif [[ -f /etc/debian_version ]]; then
            echo "  Debian/Ubuntu: sudo apt-get install ${missing[*]}"
        elif [[ -f /etc/redhat-release ]]; then
            echo "  RHEL/CentOS: sudo yum install ${missing[*]}"
        else
            echo "  Please install: ${missing[*]}"
        fi

        echo ""
        echo "After installing dependencies, run the command again."
        exit $EXIT_DEPENDENCY
    fi

    if [[ ${#optional_missing[@]} -gt 0 ]]; then
        log_debug "Optional dependencies not found: ${optional_missing[*]} (formatting may be basic)"
    fi

    DEPENDENCIES_CHECKED=true
}

# Load configuration with precedence
function load_config() {
    # Set built-in defaults
    API_URL="$DEFAULT_API_URL"
    PAGE_SIZE="$DEFAULT_PAGE_SIZE"
    OUTPUT_FORMAT="$DEFAULT_OUTPUT_FORMAT"
    NO_COLOR="${NO_COLOR:-false}"

    # Load from user home config if exists (lower priority)
    local home_config="${HOME}/.music-cli/config"
    if [[ -f "$home_config" ]]; then
        log_debug "Loading config from: $home_config"
        # shellcheck disable=SC1090
        source "$home_config"
    fi

    # Load from repository config if exists (higher priority)
    if [[ -n "${REPO_ROOT:-}" ]]; then
        local repo_config="${REPO_ROOT}/.music-cli/config"
        if [[ -f "$repo_config" ]]; then
            log_debug "Loading config from: $repo_config"
            # shellcheck disable=SC1090
            source "$repo_config"
        fi
    fi

    # Environment variables override config files (even higher priority)
    API_URL="${MUSIC_API_URL:-$API_URL}"
    PAGE_SIZE="${MUSIC_PAGE_SIZE:-$PAGE_SIZE}"
    OUTPUT_FORMAT="${MUSIC_OUTPUT_FORMAT:-$OUTPUT_FORMAT}"
    NO_COLOR="${NO_COLOR:-false}"

    # Command-line options will override these in the main script
    log_debug "Configuration loaded: API_URL=$API_URL, PAGE_SIZE=$PAGE_SIZE, OUTPUT_FORMAT=$OUTPUT_FORMAT"
}

# Save configuration to repository config file
function save_config() {
    local key="$1"
    local value="$2"

    if [[ -z "${REPO_ROOT:-}" ]]; then
        log_error "REPO_ROOT not set, cannot save configuration"
        return $EXIT_ERROR
    fi

    local repo_config="${REPO_ROOT}/.music-cli/config"

    # Create config file if it doesn't exist
    if [[ ! -f "$repo_config" ]]; then
        mkdir -p "$(dirname "$repo_config")"
        touch "$repo_config"
        log_debug "Created config file: $repo_config"
    fi

    # Update or add the configuration value
    if grep -q "^${key}=" "$repo_config" 2>/dev/null; then
        # Update existing value (macOS and Linux compatible)
        if [[ "$(uname)" == "Darwin" ]]; then
            sed -i '' "s|^${key}=.*|${key}=${value}|" "$repo_config"
        else
            sed -i "s|^${key}=.*|${key}=${value}|" "$repo_config"
        fi
    else
        # Add new value
        echo "${key}=${value}" >> "$repo_config"
    fi

    log_debug "Saved ${key}=${value} to $repo_config"
}

# Get a configuration value
function get_config() {
    local key="$1"

    case "$key" in
        API_URL)
            echo "$API_URL"
            ;;
        PAGE_SIZE)
            echo "$PAGE_SIZE"
            ;;
        OUTPUT_FORMAT)
            echo "$OUTPUT_FORMAT"
            ;;
        NO_COLOR)
            echo "$NO_COLOR"
            ;;
        *)
            log_error "Unknown configuration key: $key"
            return $EXIT_ERROR
            ;;
    esac
}

# Show usage/help message
function show_usage() {
    local script_name="$1"
    local usage_text="$2"

    echo "Usage: $script_name $usage_text"
}

# Parse common command-line options
# Returns 0 if option was handled, 1 if not recognized
function parse_common_option() {
    local opt="$1"
    local value="${2:-}"

    case "$opt" in
        --api-url)
            API_URL="$value"
            return 0
            ;;
        --json)
            OUTPUT_FORMAT="json"
            return 0
            ;;
        --no-color)
            NO_COLOR="true"
            return 0
            ;;
        --verbose|--debug)
            DEBUG="true"
            return 0
            ;;
        --quiet)
            QUIET="true"
            return 0
            ;;
        *)
            return 1
            ;;
    esac
}

# Initialize the library (call this before using common functions)
function init_common() {
    # Source colors if not already loaded
    if ! declare -F supports_colors >/dev/null 2>&1; then
        if [[ -n "${REPO_ROOT:-}" ]] && [[ -f "${REPO_ROOT}/lib/colors.sh" ]]; then
            # shellcheck disable=SC1091
            source "${REPO_ROOT}/lib/colors.sh"
        fi
    fi
}
