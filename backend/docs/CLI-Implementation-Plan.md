# Music Library CLI - Implementation Plan

## Project Overview

This document outlines the implementation plan for the Music Library command-line interface client, a set of Bash scripts that provide a user-friendly terminal interface to the Music Library REST API.

**Key Approaches:**
1. **No Installation Required:** The CLI runs directly from the cloned repository. Users can `git clone` and immediately execute commands from the `bin/` directory without any installation steps.
2. **Test-Driven Development (TDD):** Testing is integrated into each implementation phase rather than deferred to the end. Tests are written alongside features and must pass before proceeding to the next task.

## Goals

1. Create a complete CLI client covering all API endpoints
2. Provide both interactive and non-interactive usage modes
3. Ensure robust error handling and user-friendly output
4. Support scripting and automation use cases
5. Maintain consistent UX across all commands
6. **Achieve high test coverage through integrated test-driven development**

## Development Methodology

This implementation follows **Test-Driven Development (TDD)** principles:

- **Test Framework First:** Build test infrastructure in Phase 1 before implementing features
- **Implement & Test Together:** Each action/feature includes its tests in the same phase
- **Verify Before Moving On:** All tests must pass before proceeding to the next action
- **No Deferred Testing:** Testing is not a separate phase but integrated throughout development
- **Continuous Validation:** Run full test suite regularly to catch regressions early

**Benefits of Integrated Testing:**
- Earlier bug detection (fix issues immediately)
- Better code quality (design with testability in mind)
- Faster overall development (no big testing phase at the end)
- Higher confidence in deliverables (continuous validation)
- Easier debugging (smaller change sets between test runs)

## Project Structure

**Design Philosophy:** The CLI is designed to run directly from the cloned repository without any installation. Users can clone and immediately start using the commands.

```
music-cli/
├── bin/
│   ├── music-artist         # Artist management CLI
│   ├── music-song           # Song management CLI
│   ├── music-album          # Album management CLI
│   ├── music-search         # Search CLI
│   └── music-config         # Configuration CLI
├── lib/
│   ├── common.sh            # Shared utilities and constants
│   ├── api.sh               # API communication functions
│   ├── format.sh            # Output formatting functions
│   ├── validate.sh          # Input validation functions
│   └── colors.sh            # Color definitions and functions
├── tests/
│   ├── test-common.sh       # Test framework/utilities
│   ├── test-artist.sh       # Artist command tests
│   ├── test-song.sh         # Song command tests
│   ├── test-album.sh        # Album command tests
│   └── test-search.sh       # Search command tests
├── examples/
│   ├── bulk-import.sh       # Example: Import from CSV
│   ├── backup-all.sh        # Example: Backup all data
│   └── artist-report.sh     # Example: Generate artist report
├── .music-cli/              # Local configuration directory (git-ignored)
│   └── config               # User configuration (optional, created on first run)
├── test-cli.sh              # Test runner
└── README.md                # User documentation
```

**Usage Model:**
```bash
# Clone and run immediately
git clone <repo-url> music-cli
cd music-cli

# Run commands directly from bin/
./bin/music-artist list
./bin/music-song create --title "Song Title" --artist-id 1 --length 240 --release-date 2021-01-01

# Or add to PATH for convenience (optional)
export PATH="$PWD/bin:$PATH"
music-artist list
```

## Implementation Phases

### Phase 1: Foundation (Core Libraries)

**Objective:** Build the shared infrastructure that all commands will use.

**Tasks:**

1. **Create project directory structure**
   - Create all directories (bin/, lib/, tests/, examples/, .music-cli/)
   - Set up version control (initialize git repository)
   - Create .gitignore:
     ```
     .music-cli/config
     *.tmp
     test-results/
     ```
   - Create .music-cli/.gitkeep (to track directory in git)

2. **Implement lib/common.sh**
   - Detect script directory (for loading other lib files)
   - Constants (default API URL, page size, etc.)
   - Configuration file handling (load from .music-cli/config in repo, fallback to ~/.music-cli/config, fallback to defaults)
   - Argument parsing helpers
   - Exit code constants
   - Logging functions (info, warn, error, debug)
   - Dependency checking with helpful installation messages (check for bash 4.0+, curl, jq, column)

3. **Implement lib/api.sh**
   - Base HTTP request function (with error handling)
   - GET request wrapper
   - POST request wrapper
   - PUT request wrapper
   - DELETE request wrapper
   - HTTP status code handling (404, 400, 500, etc.)
   - Response parsing (extract body, headers, status)
   - URL building helpers

4. **Implement lib/validate.sh**
   - Validate date format (YYYY-MM-DD)
   - Validate positive integer
   - Validate non-empty string
   - Validate ID format
   - Validate enum values (entity types)

5. **Implement lib/format.sh**
   - Format duration (seconds → MM:SS or HH:MM:SS)
   - Format date
   - Format table output (aligned columns)
   - Format pagination info
   - Format artist details
   - Format song details
   - Format album details
   - Format search results
   - JSON pretty-print wrapper

6. **Implement lib/colors.sh**
   - Color constants (RED, GREEN, YELLOW, BLUE, RESET)
   - Check if terminal supports colors
   - Colored output functions (success, error, warning, info)
   - --no-color support

7. **Create Test Framework (tests/test-common.sh)**
   - Test result tracking (pass/fail counts)
   - Assertion functions (assert_equals, assert_contains, assert_exit_code)
   - Test API setup helpers (start test server, create test data)
   - Test cleanup helpers (delete test data, stop server)
   - Color-coded test output
   - Test data fixtures

8. **Unit Tests for Library Functions**
   - Test validation functions (date format, integers, strings)
   - Test formatting functions (duration, table output)
   - Test utility functions

**Deliverables:**
- Complete lib/ directory with all shared libraries
- Test framework ready for integration tests
- Unit tests for validation and formatting functions
- Documentation for each library module

**Estimated Effort:** 3 days

---

### Phase 2: Configuration Management

**Objective:** Implement the music-config command for managing CLI settings that work from the repository directory.

**Tasks:**

1. **Implement bin/music-config**
   - Parse command-line arguments
   - Implement `show` action (display current config and config file locations)
   - Implement `set-url` action (set and validate API URL)
   - Implement `set-default` action (set page-size, output-format)
   - Test API connectivity
   - Create .music-cli/config in repo directory on first run (if it doesn't exist)
   - Display informative message about where config is stored

2. **Configuration precedence**
   - Command-line options override everything (highest priority)
   - Environment variables (MUSIC_API_URL) override config files
   - Repository config file (.music-cli/config) if it exists
   - User home config file (~/.music-cli/config) if it exists
   - Built-in defaults (lowest priority)

3. **Help system**
   - Implement --help flag
   - Show usage examples
   - List all configuration options
   - Explain configuration precedence

4. **Testing (tests/test-config.sh)**
   - Test `show` action displays current configuration
   - Test `set-url` validates and saves URL to repo config
   - Test `set-default` saves default values
   - Test configuration precedence (CLI > env > repo config > home config > defaults)
   - Test API connectivity check
   - Test invalid inputs are rejected
   - Test config file creation in .music-cli/ directory
   - Run tests after implementation

**Deliverables:**
- Working music-config command
- Config file creation/management in repository
- Complete integration tests for music-config
- All tests passing

**Estimated Effort:** 1 day

---

### Phase 3: Artist Management CLI

**Objective:** Implement the music-artist command with all CRUD operations using test-driven development.

**Tasks:**

1. **Implement bin/music-artist - Basic Structure**
   - Main script structure
   - Argument parsing (actions and options)
   - Help text
   - Load configuration
   - Dispatch to action handlers

2. **Implement & Test List Action**
   - **Implementation:** API call to GET /api/artists, pagination, table formatting, --json support
   - **Tests:** Test list displays table, test pagination parameters, test --json output, test empty results
   - **Verify:** All list tests passing before moving on

3. **Implement & Test Get Action**
   - **Implementation:** GET /api/artists/{id}, display details, --with-songs, --with-albums options
   - **Tests:** Test get by valid ID, test 404 handling, test --with-songs flag, test --with-albums flag
   - **Verify:** All get tests passing before moving on

4. **Implement & Test Create Action**
   - **Implementation:** Parse --name option, interactive mode, validation, POST /api/artists
   - **Tests:** Test create with --name flag, test validation (empty name), test successful creation, test error handling
   - **Verify:** All create tests passing before moving on

5. **Implement & Test Update Action**
   - **Implementation:** Get current details, --name option, interactive mode, PUT /api/artists/{id}
   - **Tests:** Test update with --name, test update non-existent artist, test successful update
   - **Verify:** All update tests passing before moving on

6. **Implement & Test Delete Action**
   - **Implementation:** Get artist details, show counts, confirmation prompt, DELETE /api/artists/{id}
   - **Tests:** Test delete with --force, test confirmation prompt, test cascade delete, test delete non-existent
   - **Verify:** All delete tests passing before moving on

7. **Implement & Test Songs Action**
   - **Implementation:** GET /api/artists/{id}/songs, format table, pagination
   - **Tests:** Test songs list, test pagination, test artist not found
   - **Verify:** All songs tests passing before moving on

8. **Implement & Test Albums Action**
   - **Implementation:** GET /api/artists/{id}/albums, format table, pagination
   - **Tests:** Test albums list, test pagination, test artist not found
   - **Verify:** All albums tests passing before moving on

9. **Integration Testing (tests/test-artist.sh)**
   - Test complete workflows (create → get → update → delete)
   - Test error scenarios (network errors, API down)
   - Test edge cases (special characters in names, long names)
   - Test all options combinations

**Deliverables:**
- Complete music-artist command
- All 7 actions implemented and tested
- Complete test suite (tests/test-artist.sh) with all tests passing
- Help documentation

**Estimated Effort:** 3-4 days

---

### Phase 4: Song Management CLI

**Objective:** Implement the music-song command with all CRUD operations using test-driven development.

**Tasks:**

1. **Implement bin/music-song - Basic Structure**
   - Main script structure
   - Argument parsing
   - Help text
   - Action dispatch

2. **Implement & Test List Action**
   - **Implementation:** GET /api/songs, format duration as MM:SS, show artist/album count, pagination
   - **Tests:** Test list displays formatted table, test duration formatting, test pagination, test --json output
   - **Verify:** All list tests passing before moving on

3. **Implement & Test Get Action**
   - **Implementation:** GET /api/songs/{id}, display details with albums, format duration
   - **Tests:** Test get by valid ID, test 404 handling, test album list display, test duration formatting
   - **Verify:** All get tests passing before moving on

4. **Implement & Test Create Action - Non-Interactive**
   - **Implementation:** Parse --title, --artist-id, --length, --release-date, --album-ids options, validate, POST
   - **Tests:** Test create with all options, test validation (invalid date, negative length), test 404 for artist/album, test successful creation
   - **Verify:** All create tests passing before moving on

5. **Implement & Test Create Action - Interactive**
   - **Implementation:** Interactive prompts with artist/album search, validation, confirmation
   - **Tests:** Test interactive mode with input redirection, test artist search, test album selection
   - **Verify:** All interactive tests passing before moving on

6. **Implement & Test Update Action**
   - **Implementation:** GET current details, partial updates, interactive mode, PUT /api/songs/{id}
   - **Tests:** Test update single field, test update multiple fields, test partial updates, test 404 handling
   - **Verify:** All update tests passing before moving on

7. **Implement & Test Delete Action**
   - **Implementation:** GET song details, show albums, confirmation, DELETE /api/songs/{id}
   - **Tests:** Test delete with --force, test confirmation prompt, test delete non-existent
   - **Verify:** All delete tests passing before moving on

8. **Integration Testing (tests/test-song.sh)**
   - Test complete workflows (create → get → update → delete)
   - Test song-album associations
   - Test error scenarios and edge cases
   - Test all option combinations

**Deliverables:**
- Complete music-song command
- All 5 actions implemented and tested
- Interactive and non-interactive modes
- Complete test suite (tests/test-song.sh) with all tests passing
- Help documentation

**Estimated Effort:** 3-4 days

---

### Phase 5: Album Management CLI

**Objective:** Implement the music-album command with all CRUD operations using test-driven development.

**Tasks:**

1. **Implement bin/music-album - Basic Structure**
   - Main script structure
   - Argument parsing
   - Help text
   - Action dispatch

2. **Implement & Test List Action**
   - **Implementation:** GET /api/albums, format table with artist/release date/song count, pagination
   - **Tests:** Test list displays table, test pagination, test --json output, test empty results
   - **Verify:** All list tests passing before moving on

3. **Implement & Test Get Action**
   - **Implementation:** GET /api/albums/{id}, display details with song list, show artist name
   - **Tests:** Test get by valid ID, test 404 handling, test song list display
   - **Verify:** All get tests passing before moving on

4. **Implement & Test Create Action - Non-Interactive**
   - **Implementation:** Parse --title, --artist-id, --release-date, --song-ids options, validate, POST
   - **Tests:** Test create with all options, test validation (invalid date), test 404 for artist/songs, test successful creation
   - **Verify:** All create tests passing before moving on

5. **Implement & Test Create Action - Interactive**
   - **Implementation:** Interactive prompts with artist/song search, validation, confirmation
   - **Tests:** Test interactive mode with input redirection, test artist search, test song selection
   - **Verify:** All interactive tests passing before moving on

6. **Implement & Test Update Action**
   - **Implementation:** GET current details, partial updates, interactive mode, PUT /api/albums/{id}
   - **Tests:** Test update single field, test update song associations, test 404 handling
   - **Verify:** All update tests passing before moving on

7. **Implement & Test Delete Action**
   - **Implementation:** GET album details, show song count, confirmation, DELETE /api/albums/{id}
   - **Tests:** Test delete with --force, test confirmation prompt, test delete non-existent
   - **Verify:** All delete tests passing before moving on

8. **Implement & Test Songs Action**
   - **Implementation:** GET /api/albums/{id}/songs, format table, pagination
   - **Tests:** Test songs list, test pagination, test album not found
   - **Verify:** All songs tests passing before moving on

9. **Integration Testing (tests/test-album.sh)**
   - Test complete workflows (create → get → update → delete)
   - Test album-song associations
   - Test error scenarios and edge cases
   - Test all option combinations

**Deliverables:**
- Complete music-album command
- All 6 actions implemented and tested
- Interactive and non-interactive modes
- Complete test suite (tests/test-album.sh) with all tests passing
- Help documentation

**Estimated Effort:** 3-4 days

---

### Phase 6: Search CLI

**Objective:** Implement the music-search command with all search modes using test-driven development.

**Tasks:**

1. **Implement bin/music-search - Basic Structure**
   - Main script structure
   - Argument parsing
   - Help text

2. **Implement & Test General Search**
   - **Implementation:** Parse query, GET /api/search?q={query}, format grouped by entity type, pagination
   - **Tests:** Test general search with results, test grouping by type, test empty results, test pagination
   - **Verify:** All general search tests passing before moving on

3. **Implement & Test Type-Filtered Search**
   - **Implementation:** Parse --type option, validate type, GET /api/search?q={query}&type={type}
   - **Tests:** Test search for each type (artist/song/album), test invalid type handling, test type-specific formatting
   - **Verify:** All filtered search tests passing before moving on

4. **Implement & Test Artist-Based Search**
   - **Implementation:** Parse --artist option, GET /api/search?artist={name}, format songs and albums
   - **Tests:** Test artist-based search, test no results, test grouping by type (songs/albums only)
   - **Verify:** All artist-based search tests passing before moving on

5. **Implement & Test Result Formatting**
   - **Implementation:** Format artist/song/album results, show counts, pagination info
   - **Tests:** Test formatting for each entity type, test result counts, test --json output
   - **Verify:** All formatting tests passing before moving on

6. **Integration Testing (tests/test-search.sh)**
   - Test all three search modes together
   - Test search with special characters
   - Test search with empty results
   - Test pagination across all modes
   - Test error scenarios

**Deliverables:**
- Complete music-search command
- All 3 search modes implemented and tested
- Formatted grouped output
- Complete test suite (tests/test-search.sh) with all tests passing
- Help documentation

**Estimated Effort:** 2 days

---

### Phase 7: Test Runner & End-to-End Integration

**Objective:** Create comprehensive test runner and perform end-to-end integration testing.

**Tasks:**

1. **Create test-cli.sh Test Runner**
   - Start test API server (mvn spring-boot:run in background)
   - Wait for server to be ready (health check)
   - Run all test suites in order (config, artist, song, album, search)
   - Collect and aggregate test results
   - Report summary with pass/fail counts per suite
   - Generate test report (optional HTML/markdown output)
   - Stop server and cleanup test data
   - Exit with appropriate code (0 if all pass, 1 if any fail)

2. **End-to-End Integration Tests**
   - Test complete workflows across multiple commands
   - Create artist → create songs → create album → search → delete workflow
   - Test data consistency across commands
   - Test error propagation across related entities
   - Test concurrent operations (if applicable)

3. **Cross-Platform Testing**
   - Test on macOS (bash, zsh)
   - Test on Linux (bash)
   - Verify dependency detection works on all platforms
   - Test with different terminal types

4. **Error Scenario Testing**
   - Test behavior when API server is down
   - Test network timeout scenarios
   - Test with malformed API responses
   - Test with very large datasets (pagination stress test)

5. **Manual Testing & UAT**
   - User acceptance testing with sample workflows
   - Test all interactive modes manually
   - Verify help text is comprehensive
   - Test error messages are user-friendly
   - Collect feedback and fix issues

6. **Test Documentation**
   - Document how to run tests
   - Document test coverage
   - Create troubleshooting guide for test failures

**Deliverables:**
- Working test-cli.sh test runner
- End-to-end integration tests
- Cross-platform verification
- Test documentation
- All tests passing (100% pass rate)

**Estimated Effort:** 2-3 days

---

### Phase 8: Documentation & Usability

**Objective:** Make the CLI easy to use directly from the cloned repository.

**Tasks:**

1. **Create README.md**
   - Project description and features
   - **Requirements section:**
     - bash 4.0+
     - curl
     - jq
     - column (optional, for better formatting)
   - **Quick Start (no installation required):**
     ```bash
     git clone <repo-url> music-cli
     cd music-cli
     ./bin/music-artist list
     ```
   - **Optional PATH setup** for convenience:
     ```bash
     export PATH="$PWD/bin:$PATH"
     music-artist list
     ```
   - Command reference (brief overview, link to --help for details)
   - Configuration section (repo config vs home config vs env vars)
   - Examples for common tasks
   - Troubleshooting (dependency issues, API connection, etc.)
   - Contributing guidelines

2. **Create .gitignore**
   - Ignore .music-cli/config (user-specific configuration)
   - Ignore test artifacts
   - Keep .music-cli/ directory structure in repo

3. **Create Example Scripts (examples/)**
   - bulk-import.sh: Import artists/songs/albums from CSV
   - backup-all.sh: Export all data to JSON files
   - artist-report.sh: Generate formatted report of artists with song/album counts
   - find-duplicates.sh: Search for potential duplicate entries
   - Each example script demonstrates running from repo with ./bin/ prefix

4. **Enhance Dependency Checking**
   - Update all bin/ scripts to check dependencies on first run
   - Show helpful error messages with installation instructions
   - Detect OS (macOS, Linux) and provide platform-specific install commands
   - Make dependency check fast (cache result after first check)

5. **In-Command Documentation**
   - Comprehensive --help for each command
   - Usage examples in help text showing ./bin/ prefix
   - Error messages with suggestions
   - Tips in output (e.g., "Next steps: ...")
   - Document running from repository vs adding to PATH

6. **Create CONTRIBUTING.md**
   - How to run tests
   - Code style guidelines
   - How to add new features
   - PR process

**Deliverables:**
- Complete README.md with no-install quick start
- .gitignore properly configured
- Example scripts demonstrating repo-based usage
- Enhanced dependency checking with helpful messages
- Help text for all commands
- CONTRIBUTING.md

**Estimated Effort:** 2 days

---

### Phase 9: Polish & Enhancements

**Objective:** Improve user experience and add nice-to-have features.

**Tasks:**

1. **Color & Formatting Improvements**
   - Add colors to output (success=green, error=red, warning=yellow)
   - Improve table formatting with proper column alignment
   - Add borders to tables (optional, configurable)
   - Better pagination display

2. **Enhanced Error Messages**
   - More specific error messages
   - Suggestions for fixing errors
   - Show partial matches when entity not found
   - Network error handling improvements

3. **Usability Enhancements**
   - Confirmation messages show more context
   - Progress indicators for slow operations
   - Better interactive prompts (with defaults shown)
   - Tab completion scripts (bash/zsh)

4. **Performance Optimizations**
   - Cache entity lists for faster lookups
   - Parallel API calls where possible
   - Reduce jq invocations

5. **Additional Features**
   - Export commands (artist/song/album to JSON/CSV)
   - Import commands (from JSON/CSV)
   - Bulk operations (batch delete, batch update)
   - Filter options for list commands
   - Sorting options for all list outputs

6. **Security**
   - Sanitize inputs to prevent injection
   - Validate URLs
   - Secure config file permissions (600)

**Deliverables:**
- Polished user experience
- Enhanced error handling
- Optional enhancements implemented
- Updated documentation

**Estimated Effort:** 2-3 days

---

## Implementation Guidelines

### Coding Standards

**Script Structure for Repository-Based Execution:**
```bash
#!/usr/bin/env bash

# Strict error handling
set -euo pipefail

# Detect script directory and repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Load shared libraries using absolute paths
source "$REPO_ROOT/lib/common.sh"
source "$REPO_ROOT/lib/api.sh"
source "$REPO_ROOT/lib/format.sh"
source "$REPO_ROOT/lib/validate.sh"
source "$REPO_ROOT/lib/colors.sh"

# Check dependencies (from common.sh)
check_dependencies

# Load configuration (from common.sh)
load_config

# Main script logic...
```

**Bash Best Practices:**
```bash
# Always quote variables
echo "$variable"
echo "${array[@]}"

# Use local for function variables
function some_function() {
    local var="value"
    echo "$var"
}

# Use [[ ]] instead of [ ]
if [[ "$var" == "value" ]]; then
    echo "matched"
fi

# Check command success
if curl -s "$url" > /dev/null; then
    echo "success"
fi

# Function naming: verb_noun
function fetch_artist() {
    local artist_id="$1"
    # ...
}

# Use shellcheck for linting
```

**Error Handling Pattern:**
```bash
function api_get() {
    local url="$1"
    local response
    local http_code

    response=$(curl -s -w "\n%{http_code}" "$url")
    http_code=$(echo "$response" | tail -n1)

    case "$http_code" in
        200)
            echo "$response" | sed '$d'  # Remove status code line
            return 0
            ;;
        404)
            error "Not found: $url"
            return 1
            ;;
        *)
            error "HTTP $http_code: $url"
            return 1
            ;;
    esac
}
```

**Output Formatting Pattern:**
```bash
function format_artist_list() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        echo "$json" | jq '.'
        return
    fi

    echo "ID    Name                 Songs  Albums"
    echo "--    ----                 -----  ------"

    echo "$json" | jq -r '.content[] |
        [.id, .name, .songCount, .albumCount] |
        @tsv' |
    column -t

    # Pagination info
    local total=$(echo "$json" | jq -r '.totalElements')
    local page=$(echo "$json" | jq -r '.number')
    echo ""
    echo "Page $((page + 1)) of $(echo "$json" | jq -r '.totalPages') ($total total)"
}
```

### Dependency Management

**Check Dependencies on First Run:**
```bash
function check_dependencies() {
    local missing=()
    local optional_missing=()

    # Required dependencies
    command -v bash >/dev/null 2>&1 || missing+=("bash")
    command -v curl >/dev/null 2>&1 || missing+=("curl")
    command -v jq >/dev/null 2>&1 || missing+=("jq")

    # Optional dependencies (for better formatting)
    command -v column >/dev/null 2>&1 || optional_missing+=("column")

    # Check bash version (need 4.0+)
    if [[ -n "${BASH_VERSION}" ]]; then
        local bash_major="${BASH_VERSION%%.*}"
        if [[ "$bash_major" -lt 4 ]]; then
            error "Bash 4.0 or higher required (found: $BASH_VERSION)"
            exit 3
        fi
    fi

    if [[ ${#missing[@]} -gt 0 ]]; then
        error "Missing required dependencies: ${missing[*]}"
        echo ""
        echo "Install with:"
        echo "  Ubuntu/Debian: sudo apt-get install ${missing[*]}"
        echo "  macOS:         brew install ${missing[*]}"
        echo "  RHEL/CentOS:   sudo yum install ${missing[*]}"
        echo ""
        echo "After installing dependencies, run the command again."
        exit 3
    fi

    if [[ ${#optional_missing[@]} -gt 0 ]]; then
        # Optional: warn about missing optional dependencies
        # (don't exit, just note it)
        debug "Optional dependencies not found: ${optional_missing[*]} (formatting may be basic)"
    fi
}
```

### Configuration Management

**Config File Format (.music-cli/config or ~/.music-cli/config):**
```bash
API_URL=http://localhost:8080
PAGE_SIZE=20
OUTPUT_FORMAT=formatted
NO_COLOR=false
```

**Loading Config with Precedence:**
```bash
function load_config() {
    # Set built-in defaults
    API_URL="http://localhost:8080"
    PAGE_SIZE=20
    OUTPUT_FORMAT="formatted"
    NO_COLOR=false

    # Load from user home config if exists (lower priority)
    local home_config="${HOME}/.music-cli/config"
    if [[ -f "$home_config" ]]; then
        source "$home_config"
    fi

    # Load from repository config if exists (higher priority)
    local repo_config="${REPO_ROOT}/.music-cli/config"
    if [[ -f "$repo_config" ]]; then
        source "$repo_config"
    fi

    # Environment variables override config files (even higher priority)
    API_URL="${MUSIC_API_URL:-$API_URL}"
    PAGE_SIZE="${MUSIC_PAGE_SIZE:-$PAGE_SIZE}"
    OUTPUT_FORMAT="${MUSIC_OUTPUT_FORMAT:-$OUTPUT_FORMAT}"

    # Command-line options override everything (handled in argument parsing)
    # This is the highest priority
}
```

**Config File Locations (in priority order):**
1. **Command-line options** (highest priority) - `--api-url`, `--json`, etc.
2. **Environment variables** - `MUSIC_API_URL`, `MUSIC_PAGE_SIZE`, etc.
3. **Repository config** - `<repo>/.music-cli/config` (git-ignored, user-specific)
4. **Home config** - `~/.music-cli/config` (shared across repos)
5. **Built-in defaults** (lowest priority)

### Testing Strategy

**Integrated TDD Workflow:**

1. **Before Implementation:**
   - Review requirements for the action/feature
   - Identify test cases (happy path, error cases, edge cases)
   - Set up test data fixtures if needed

2. **During Implementation:**
   - Write implementation code
   - Write corresponding tests immediately
   - Run tests frequently (after each significant change)
   - Fix any failing tests before moving on

3. **After Implementation:**
   - Run full test suite for the command
   - Verify 100% of tests pass
   - Document any known limitations
   - Only then proceed to next action

**Test Structure:**
```bash
#!/usr/bin/env bash

source "$(dirname "$0")/test-common.sh"

# Test naming convention: test_<command>_<action>_<scenario>
function test_artist_list_shows_table() {
    local output
    output=$(music-artist list 2>&1)

    assert_exit_code 0 "$?"
    assert_contains "$output" "ID.*Name.*Songs.*Albums"
    pass "artist list shows table header"
}

function test_artist_list_pagination() {
    local output
    output=$(music-artist list --page 0 --size 5 2>&1)

    assert_exit_code 0 "$?"
    assert_contains "$output" "Page 1 of"
    pass "artist list supports pagination"
}

function test_artist_create_success() {
    local output
    output=$(music-artist create --name "Test Artist" 2>&1)

    assert_exit_code 0 "$?"
    assert_contains "$output" "Artist created successfully"
    pass "artist create works with valid input"
}

function test_artist_create_invalid() {
    local output
    output=$(music-artist create --name "" 2>&1)

    assert_exit_code 1 "$?"
    assert_contains "$output" "Name cannot be empty"
    pass "artist create rejects empty name"
}

# Run all tests
run_test test_artist_list_shows_table
run_test test_artist_list_pagination
run_test test_artist_create_success
run_test test_artist_create_invalid

# Show results and exit with appropriate code
show_results
```

**Test Categories:**
- **Happy Path:** Test successful operations with valid inputs
- **Validation:** Test input validation and error messages
- **Error Handling:** Test 404, 400, 500 responses from API
- **Edge Cases:** Test empty results, special characters, very long inputs
- **Options:** Test all command-line flags and combinations
- **Interactive Mode:** Test interactive prompts using input redirection

**Running Tests During Development:**
```bash
# Run tests for specific command after implementing an action
./tests/test-artist.sh

# Run all tests to check for regressions
./test-cli.sh

# Run specific test function for debugging
bash -x ./tests/test-artist.sh 2>&1 | grep "test_artist_create"
```

## Timeline & Milestones

**Development Approach:** Test-Driven Development (TDD)
- Write tests as you implement each feature
- All tests must pass before moving to next action/phase
- Continuous integration of testing throughout development

### Week 1: Foundation & Configuration (Phases 1-2)
- **Day 1:** Project setup, directory structure, common.sh, colors.sh
- **Day 2:** API client library (api.sh), validation library (validate.sh)
- **Day 3:** Formatting library (format.sh), test framework (test-common.sh), unit tests
- **Day 4:** Configuration management (music-config) with tests
- **Day 5:** Buffer/catch-up day, ensure all Phase 1-2 tests passing

**Milestone 1:** Foundation complete with test framework, music-config working, all tests passing

### Week 2: Artist & Song Management (Phases 3-4)
- **Day 1:** music-artist basic structure + list/get actions with tests
- **Day 2:** music-artist create/update/delete actions with tests
- **Day 3:** music-artist songs/albums actions, complete integration tests
- **Day 4:** music-song basic structure + list/get/create actions with tests
- **Day 5:** music-song update/delete actions, complete integration tests

**Milestone 2:** music-artist and music-song complete, all tests passing

### Week 3: Album & Search (Phases 5-6)
- **Day 1:** music-album basic structure + list/get/create actions with tests
- **Day 2:** music-album update/delete/songs actions, complete integration tests
- **Day 3:** music-search all three search modes with tests
- **Day 4:** Complete all search integration tests, verify all commands
- **Day 5:** Buffer/catch-up day, ensure all Phase 3-6 tests passing

**Milestone 3:** All commands complete (artist, song, album, search), all tests passing

### Week 4: Integration, Testing & Documentation (Phases 7-8)
- **Day 1:** Create test-cli.sh runner, end-to-end integration tests
- **Day 2:** Cross-platform testing, error scenario testing, UAT
- **Day 3:** README.md with no-install quick start, .gitignore, dependency checking
- **Day 4:** Example scripts, CONTRIBUTING.md, help text enhancements
- **Day 5:** Final testing sweep, documentation review, polish

**Milestone 4:** Complete CLI runnable from repository with comprehensive documentation

### Week 5: Polish & Release (Phase 9 - Optional)
- **Day 1-2:** UX enhancements, color/formatting improvements
- **Day 3:** Performance optimizations, additional features
- **Day 4:** Security hardening, final bug fixes
- **Day 5:** Release preparation, final QA

**Final Milestone:** Production-ready CLI v1.0 with 100% test coverage

## Success Criteria

### Functional Requirements
- [ ] All commands implemented (artist, song, album, search, config)
- [ ] All CRUD operations working correctly
- [ ] Commands run directly from repository without installation
- [ ] Scripts properly detect repository root and load libraries
- [ ] Both interactive and non-interactive modes functional
- [ ] Comprehensive error handling with user-friendly messages
- [ ] Pagination working on all list endpoints
- [ ] JSON output mode available for all commands
- [ ] Configuration management working (CLI > env > repo config > home config > defaults)

### Testing Requirements
- [ ] Test framework built and operational (Phase 1)
- [ ] Unit tests for all library functions passing
- [ ] Integration tests for music-config passing (Phase 2)
- [ ] Integration tests for music-artist passing (Phase 3)
- [ ] Integration tests for music-song passing (Phase 4)
- [ ] Integration tests for music-album passing (Phase 5)
- [ ] Integration tests for music-search passing (Phase 6)
- [ ] End-to-end workflow tests passing (Phase 7)
- [ ] 95%+ test coverage overall
- [ ] All tests pass on macOS and Linux
- [ ] Test runner (test-cli.sh) fully functional

### Documentation & Usability
- [ ] Clear README.md with quick start (no installation required)
- [ ] --help text comprehensive for all commands
- [ ] Example scripts demonstrating common workflows
- [ ] .gitignore properly configured for user configs
- [ ] Dependency checking with helpful OS-specific install instructions
- [ ] Test documentation complete
- [ ] CONTRIBUTING.md with development guidelines

### Quality & UX
- [ ] Positive user feedback on UX
- [ ] Consistent formatting across all commands
- [ ] No shellcheck warnings
- [ ] Code follows documented standards
- [ ] All dependencies properly detected and documented

## Risks & Mitigation

**Risk:** Bash version compatibility issues
- **Mitigation:** Target bash 4.0+, test on multiple platforms, document requirements clearly

**Risk:** jq not available on user system
- **Mitigation:** Check in install script, provide clear installation instructions

**Risk:** API changes breaking CLI
- **Mitigation:** Version the CLI, document API version compatibility

**Risk:** Complex interactive flows difficult to test
- **Mitigation:** Use input redirection for testing, manual test plans

**Risk:** Inconsistent output formatting across commands
- **Mitigation:** Centralize formatting in lib/format.sh, code review

## Next Steps

1. **Review this plan** with stakeholders
2. **Set up repository**
   - Create music-cli/ directory
   - Initialize git repository
   - Create initial directory structure (bin/, lib/, tests/, examples/, .music-cli/)
   - Create .gitignore (ignore .music-cli/config, test artifacts)
3. **Begin Phase 1** (foundation libraries)
   - Implement repository-aware library loading
   - Build dependency checking
   - Create test framework
4. **Establish testing workflow**
   - Run tests after each implementation
   - Verify all pass before moving to next task
5. **Daily standups** to track progress
6. **Weekly demos** of completed features

## Resources

**Documentation:**
- Bash scripting guide: https://www.gnu.org/software/bash/manual/
- jq manual: https://stedolan.github.io/jq/manual/
- curl documentation: https://curl.se/docs/

**Tools:**
- shellcheck: Shell script linting
- shfmt: Shell script formatting
- bats: Bash automated testing system (alternative to custom framework)

**References:**
- CLI-Requirements.md: Detailed requirements specification
- USAGE.md: API usage guide
- SWAGGER.md: API documentation
