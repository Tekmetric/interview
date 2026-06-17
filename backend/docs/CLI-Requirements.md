# Overview
This project is a command-line interface (CLI) client for the Music Library API. The CLI client provides a user-friendly shell-based interface to interact with the RESTful API, allowing users to manage artists, songs, and albums from the terminal.

# Architecture

## Technology Stack
- **Shell Scripts**: Bash scripts for all CLI commands
- **curl**: HTTP client for API communication
- **jq**: JSON processor for parsing and formatting API responses
- **Standard Unix Tools**: grep, sed, awk for text processing

## Design Principles
- Each entity type (artist, song, album) has its own script
- Consistent command structure across all scripts
- Clear error messages and user feedback
- Support for both interactive and scripted usage
- JSON output option for automation/scripting
- Human-readable output by default

# Command Structure

## Naming Convention
All CLI scripts follow the pattern: `music-<entity>` where entity is one of: `artist`, `song`, `album`, or `search`.

**Core Scripts:**
- `music-artist` - Manage artists
- `music-song` - Manage songs
- `music-album` - Manage albums
- `music-search` - Search across all entities
- `music-config` - Configure API endpoint and settings

## Command Syntax
```bash
music-<entity> <action> [options]
```

**Standard Actions:**
- `list` - List all entities (with pagination)
- `get <id>` - Get entity by ID
- `create` - Create new entity (interactive or via options)
- `update <id>` - Update existing entity
- `delete <id>` - Delete entity
- `help` - Show command usage

## Global Options
All commands support these options:
- `--api-url <url>` - Override default API URL (default: http://localhost:8080)
- `--json` - Output raw JSON instead of formatted text
- `--verbose` - Show verbose output including HTTP requests
- `--quiet` - Suppress non-essential output
- `--help` - Show help information

# Functional Requirements

## Artist Management (`music-artist`)

### List Artists
```bash
music-artist list [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display artists in a formatted table (ID, Name, Songs, Albums)
- Support pagination with --page and --size options
- Support sorting by name
- Show pagination info (current page, total pages, total items)
- Return exit code 0 on success

**Example Output:**
```
ID    Name                 Songs  Albums
--    ----                 -----  ------
1     The Beatles          15     5
2     Queen                12     4
3     Pink Floyd           20     8

Page 1 of 3 (60 total artists)
```

### Get Artist
```bash
music-artist get <id> [--with-songs] [--with-albums]
```

**Requirements:**
- Display artist details
- Optionally include songs list with --with-songs
- Optionally include albums list with --with-albums
- Return exit code 1 if artist not found
- Show meaningful error message for 404

**Example Output:**
```
Artist #1
Name: The Beatles

Songs (15):
  - Hey Jude (7:11)
  - Let It Be (4:03)
  ...

Albums (5):
  - Abbey Road (1969-09-26)
  - Let It Be (1970-05-08)
  ...
```

### Create Artist
```bash
music-artist create --name "Artist Name"
# OR interactive mode
music-artist create
```

**Requirements:**
- Support --name option for non-interactive creation
- If --name not provided, prompt user interactively
- Validate that name is not empty
- Display created artist ID and details
- Return exit code 0 on success, 1 on failure

**Example Output:**
```
Artist created successfully!
ID: 42
Name: Led Zeppelin
```

### Update Artist
```bash
music-artist update <id> --name "New Name"
# OR interactive mode
music-artist update <id>
```

**Requirements:**
- Support --name option for non-interactive update
- If --name not provided, prompt user interactively with current values
- Return exit code 1 if artist not found
- Display updated artist details

### Delete Artist
```bash
music-artist delete <id> [--force]
```

**Requirements:**
- Show warning about cascade deletion of songs and albums
- Require confirmation unless --force is specified
- Display count of songs and albums that will be deleted
- Return exit code 0 on success, 1 if not found or canceled

**Example Output:**
```
WARNING: Deleting artist "The Beatles" will also delete:
  - 15 songs
  - 5 albums

Are you sure? (yes/no): yes
Artist deleted successfully.
```

### List Artist's Songs
```bash
music-artist songs <id> [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display songs in formatted table
- Support pagination
- Show song title, length, release date

### List Artist's Albums
```bash
music-artist albums <id> [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display albums in formatted table
- Support pagination
- Show album title, release date, song count

## Song Management (`music-song`)

### List Songs
```bash
music-song list [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display songs in formatted table (ID, Title, Artist, Length, Release Date, Albums)
- Support pagination and sorting
- Format length as MM:SS
- Show album count for each song

**Example Output:**
```
ID    Title              Artist         Length  Release     Albums
--    -----              ------         ------  -------     ------
1     Hey Jude           The Beatles    7:11    1968-08-26  2
2     Bohemian Rhapsody  Queen          5:54    1975-10-31  1

Page 1 of 10 (200 total songs)
```

### Get Song
```bash
music-song get <id>
```

**Requirements:**
- Display song details including all associated albums
- Format length as MM:SS
- Show artist name (not just ID)
- List all albums the song appears on

**Example Output:**
```
Song #1
Title: Hey Jude
Artist: The Beatles
Length: 7:11 (431 seconds)
Release Date: 1968-08-26

Albums (2):
  - Hey Jude (Single) [1968]
  - Past Masters [1988]
```

### Create Song
```bash
music-song create --title "Title" --artist-id N --length SECONDS --release-date YYYY-MM-DD [--album-ids N,N,N]
# OR interactive mode
music-song create
```

**Requirements:**
- Support both command-line options and interactive mode
- In interactive mode, allow artist search/selection
- In interactive mode, allow multiple album selection
- Validate required fields: title, artist-id, length, release-date
- Validate date format (YYYY-MM-DD)
- Validate length is positive integer
- Display created song details
- Return exit code 1 on validation failure or 404 (artist/album not found)

**Interactive Mode Example:**
```
Title: Stairway to Heaven
Artist name (to search): Led Zeppelin
Found artists:
  1. Led Zeppelin (ID: 10)
Select artist ID: 10
Length in seconds: 482
Release Date (YYYY-MM-DD): 1971-11-08
Add to albums? (y/n): y
Search for album: IV
Found albums:
  1. Led Zeppelin IV (ID: 20) [1971-11-08]
Select album ID (or 'done'): 20
Select album ID (or 'done'): done

Song created successfully!
ID: 123
```

### Update Song
```bash
music-song update <id> --title "Title" [--artist-id N] [--length SECONDS] [--release-date DATE] [--album-ids N,N,N]
# OR interactive mode
music-song update <id>
```

**Requirements:**
- Support partial updates (only specified fields)
- In interactive mode, show current values as defaults
- Allow updating album associations
- Validate all fields as in create

### Delete Song
```bash
music-song delete <id> [--force]
```

**Requirements:**
- Require confirmation unless --force specified
- Show which albums the song appears on
- Note that albums will NOT be deleted

**Example Output:**
```
Delete song "Hey Jude"?
This song appears on 2 albums (albums will not be deleted).

Are you sure? (yes/no): yes
Song deleted successfully.
```

## Album Management (`music-album`)

### List Albums
```bash
music-album list [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display albums in formatted table (ID, Title, Artist, Release Date, Songs)
- Support pagination and sorting
- Show song count for each album

**Example Output:**
```
ID    Title              Artist         Release     Songs
--    -----              ------         -------     -----
1     Abbey Road         The Beatles    1969-09-26  17
2     A Night at Opera   Queen          1975-11-21  12

Page 1 of 5 (100 total albums)
```

### Get Album
```bash
music-album get <id>
```

**Requirements:**
- Display album details including all songs
- Show artist name
- List all songs with track numbers (if ordering exists) or alphabetically

**Example Output:**
```
Album #1
Title: Abbey Road
Artist: The Beatles
Release Date: 1969-09-26

Songs (17):
  - Come Together (4:19)
  - Something (3:02)
  - Here Comes the Sun (3:05)
  ...
```

### Create Album
```bash
music-album create --title "Title" --artist-id N --release-date YYYY-MM-DD [--song-ids N,N,N]
# OR interactive mode
music-album create
```

**Requirements:**
- Support both command-line options and interactive mode
- In interactive mode, allow artist search/selection
- In interactive mode, allow multiple song selection
- Validate required fields: title, artist-id, release-date
- Validate date format
- Display created album details

### Update Album
```bash
music-album update <id> --title "Title" [--artist-id N] [--release-date DATE] [--song-ids N,N,N]
# OR interactive mode
music-album update <id>
```

**Requirements:**
- Support partial updates
- Allow updating song associations
- In interactive mode, show current values

### Delete Album
```bash
music-album delete <id> [--force]
```

**Requirements:**
- Require confirmation unless --force specified
- Show how many songs are on the album
- Note that songs will NOT be deleted

### List Album's Songs
```bash
music-album songs <id> [--page N] [--size N] [--sort field,direction]
```

**Requirements:**
- Display songs in formatted table
- Support pagination
- Show title, artist, length, release date

## Search (`music-search`)

### General Search
```bash
music-search <query> [--page N] [--size N]
```

**Requirements:**
- Search across all entity types
- Display results grouped by type (Artists, Songs, Albums)
- Show relevant details for each entity type
- Support pagination

**Example Output:**
```
Search results for "queen":

Artists (1):
  #2    Queen

Songs (3):
  #15   Killer Queen           Queen          2:59    1974-10-11
  #16   Queen of Hearts        Various        3:45    1980-05-15
  #42   God Save the Queen     Sex Pistols    3:20    1977-05-27

Albums (2):
  #3    A Night at the Opera   Queen          1975-11-21
  #4    Queen II               Queen          1974-03-08

Page 1 of 1 (6 total results)
```

### Type-Filtered Search
```bash
music-search <query> --type {artist|song|album} [--page N] [--size N]
```

**Requirements:**
- Search within specific entity type
- Use same display format as general search
- Validate type parameter

**Example:**
```bash
music-search "love" --type song
```

### Artist-Based Search
```bash
music-search --artist "Artist Name" [--page N] [--size N]
```

**Requirements:**
- Find all songs and albums by artist
- Group results by type
- Does not return the artist entity itself

**Example:**
```bash
music-search --artist "The Beatles"
```

## Configuration (`music-config`)

### View Configuration
```bash
music-config show
```

**Requirements:**
- Display current API URL
- Display current output format preference
- Display any saved defaults

**Example Output:**
```
Music Library CLI Configuration
API URL: http://localhost:8080
Output Format: formatted
Default Page Size: 20
```

### Set API URL
```bash
music-config set-url <url>
```

**Requirements:**
- Save API URL to config file
- Validate URL format
- Test connection to API

### Set Defaults
```bash
music-config set-default <key> <value>
```

**Requirements:**
- Support setting page-size default
- Support setting output-format default (formatted|json)
- Save to config file (~/.music-cli/config)

# Technical Requirements

## Error Handling

### HTTP Error Codes
- **404 Not Found**: Display user-friendly message "Entity not found with ID: X"
- **400 Bad Request**: Display validation errors from API response
- **500 Server Error**: Display "Server error occurred. Please try again later."
- **Connection Refused**: Display "Cannot connect to API at <url>. Is the server running?"

### Exit Codes
- **0**: Success
- **1**: General error (not found, validation failure, user canceled)
- **2**: Invalid arguments/usage
- **3**: Network/connection error
- **4**: Server error (500)

### Error Output
- All error messages go to stderr
- Error messages are clear and actionable
- Include relevant context (entity type, ID, etc.)

## Output Formatting

### Human-Readable Format
- Use column alignment for tables
- Include headers for tables
- Use borders/separators for readability
- Format durations as MM:SS or HH:MM:SS
- Format dates as YYYY-MM-DD
- Use colors for better readability (optional, with --no-color to disable)

### JSON Format (--json)
- Output raw JSON response from API
- Pretty-print JSON by default
- Include --compact for single-line JSON

### Pagination Info
Always display when listing:
- Current page number
- Total pages
- Total items
- Items on current page

## Data Validation

### Required Field Validation
- Check all required fields before making API call
- Display clear message for missing fields
- Show which fields are required

### Format Validation
- Date: YYYY-MM-DD format, validate as valid date
- Length: Positive integer
- IDs: Positive integers
- Names/Titles: Non-empty strings

### Confirmation Prompts
- Delete operations require confirmation
- Show impact of destructive operations
- Allow --force to skip confirmation (for scripting)

## Configuration File

### Location
`~/.music-cli/config`

### Format
Simple key=value format or JSON

**Example:**
```
api_url=http://localhost:8080
page_size=20
output_format=formatted
```

### Precedence
1. Command-line options (highest)
2. Environment variables (MUSIC_API_URL)
3. Config file
4. Built-in defaults (lowest)

## Dependencies

### Required
- bash (version 4.0+)
- curl
- jq (for JSON parsing)

### Optional
- column (for better table formatting)
- tput (for colors)

### Installation Check
Scripts should check for required dependencies on first run and provide helpful error messages if missing.

**Example:**
```
Error: 'jq' is required but not installed.

Install with:
  Ubuntu/Debian: sudo apt-get install jq
  macOS:         brew install jq
  RHEL/CentOS:   sudo yum install jq
```

## Scripting Support

### Non-Interactive Mode
All commands support non-interactive usage via command-line options for use in scripts.

### JSON Output
With --json flag, output raw JSON suitable for piping to jq or other tools.

**Example:**
```bash
# Get all artist IDs
music-artist --json list | jq -r '.content[].id'

# Create multiple songs from a file
while IFS=, read title artist_id length date; do
  music-song create --title "$title" --artist-id "$artist_id" --length "$length" --release-date "$date"
done < songs.csv
```

**Note:** Global options like `--json`, `--api-url`, `--verbose` can be specified either before or after the action for convenience:
- `music-artist --json list` (recommended)
- `music-artist list --json` (also supported)

### Exit Codes
Consistent exit codes allow proper error handling in scripts.

```bash
if music-artist get 999 > /dev/null 2>&1; then
  echo "Artist exists"
else
  echo "Artist not found"
fi
```

# User Experience Requirements

## Help System

### Command Help
Every command supports --help showing:
- Usage syntax
- Available actions
- Options and flags
- Examples

**Example:**
```bash
music-artist --help
```

Output:
```
Usage: music-artist <action> [options]

Actions:
  list                List all artists
  get <id>            Get artist by ID
  create              Create new artist
  update <id>         Update artist
  delete <id>         Delete artist
  songs <id>          List artist's songs
  albums <id>         List artist's albums

Options:
  --name <name>           Artist name (for create/update)
  --page <n>              Page number (default: 0)
  --size <n>              Page size (default: 20)
  --sort <field,dir>      Sort field and direction
  --with-songs            Include songs in output
  --with-albums           Include albums in output
  --force                 Skip confirmation prompts
  --json                  Output raw JSON
  --api-url <url>         Override API URL
  --help                  Show this help

Global Options:
  --verbose               Show verbose output
  --quiet                 Suppress non-essential output

Examples:
  music-artist list --page 1 --size 10
  music-artist create --name "The Beatles"
  music-artist get 1 --with-songs
  music-artist delete 5 --force
  music-artist songs 1 --sort title,asc
```

### Error Messages
- Clear and specific
- Suggest corrective action
- Include examples when relevant

**Example:**
```
Error: Invalid date format '2021-13-01'
Expected format: YYYY-MM-DD
Example: 2021-01-31
```

## Progress Indicators

### Loading Indicators
For operations that may take time:
- Show spinner or progress indicator
- Can be suppressed with --quiet

### Confirmation Messages
After successful operations:
- Confirm action completed
- Show relevant details (ID, name, etc.)
- Suggest next steps when appropriate

**Example:**
```
Artist created successfully!
ID: 42
Name: Led Zeppelin

Next steps:
  - Add songs: music-song create
  - Add albums: music-album create
  - View details: music-artist get 42
```

# Testing Requirements

## Unit Tests
- Test argument parsing
- Test data validation
- Test error handling
- Test output formatting

## Integration Tests
- Test against real API
- Test all CRUD operations
- Test pagination
- Test search functionality
- Test error scenarios (404, 400, etc.)

## Test Script
Provide `test-cli.sh` that:
- Starts test API instance
- Runs all integration tests
- Reports results
- Cleans up test data

# Installation & Distribution

## Installation Script
Provide `install.sh` that:
- Checks for dependencies
- Copies scripts to /usr/local/bin or ~/.local/bin
- Creates config directory
- Sets executable permissions
- Creates initial config file

## Uninstallation
Provide `uninstall.sh` that:
- Removes scripts
- Optionally removes config (with confirmation)

## Directory Structure
```
music-cli/
├── bin/
│   ├── music-artist
│   ├── music-song
│   ├── music-album
│   ├── music-search
│   └── music-config
├── lib/
│   ├── common.sh          # Shared functions
│   ├── api.sh             # API interaction functions
│   ├── format.sh          # Output formatting functions
│   └── validate.sh        # Validation functions
├── tests/
│   ├── test-artist.sh
│   ├── test-song.sh
│   ├── test-album.sh
│   └── test-search.sh
├── install.sh
├── uninstall.sh
├── test-cli.sh
└── README.md
```

# Documentation Requirements

## README.md
- Installation instructions
- Quick start guide
- Examples for each command
- Troubleshooting section

## Man Pages (Optional)
- Provide man pages for each command
- Install to /usr/local/share/man or ~/.local/share/man

## Examples Directory
Provide example scripts showing:
- Bulk import from CSV
- Backup/export all data
- Generate reports
- Common workflows

# Future Enhancements (Nice to Have)

## Auto-completion
Bash/Zsh completion scripts for:
- Command names
- Action names
- Option names
- Entity IDs (from cache)

## Caching
- Cache entity lists for tab completion
- Cache artist/album names for search
- Configurable TTL
- Manual cache refresh command

## Interactive Mode
A REPL-style interactive mode:
```bash
music-cli

music> artist list
music> song create
music> exit
```

## Bulk Operations
- Import from CSV/JSON
- Export to CSV/JSON
- Batch create/update/delete

## WebSocket Integration
Subscribe to real-time notifications:
```bash
music-watch [--artists] [--songs] [--albums]
```

Displays notifications as they occur.
