# Music Library CLI

A command-line interface for managing your music library. Easily browse, create, update, and search for artists, songs, and albums right from your terminal.

## Table of Contents

- [Quick Start](#quick-start)
- [Installation](#installation)
- [Configuration](#configuration)
- [Commands](#commands)
  - [music-artist](#music-artist)
  - [music-song](#music-song)
  - [music-album](#music-album)
  - [music-search](#music-search)
  - [music-config](#music-config)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Quick Start

**No installation required!** Just run commands directly from the repository:

```bash
# List all artists
./bin/music-artist list

# Search for songs
./bin/music-search "bohemian rhapsody"

# Get help for any command
./bin/music-artist --help
```

**Optional:** Add the `bin/` directory to your PATH for easier access:

```bash
export PATH="/path/to/music-cli/bin:$PATH"
music-artist list
```

## Installation

### Requirements

- **bash** 3.2 or higher (4.0+ recommended)
- **curl** - For API requests
- **jq** - For JSON processing

### Installing Dependencies

**macOS:**
```bash
brew install curl jq
```

**Ubuntu/Debian:**
```bash
sudo apt-get install curl jq
```

**RHEL/CentOS:**
```bash
sudo yum install curl jq
```

### Verify Installation

```bash
# The CLI will check dependencies automatically
./bin/music-artist list
```

If dependencies are missing, you'll see helpful install instructions.

## Configuration

The CLI uses a layered configuration system with the following precedence (highest to lowest):

1. **Command-line options** - `--api-url http://localhost:9090`
2. **Environment variables** - `MUSIC_API_URL`, `MUSIC_OUTPUT_FORMAT`, `MUSIC_PAGE_SIZE`
3. **User config file** - `~/.music-cli/config`
4. **Repository config** - `music-cli/.music-cli/config` (git-ignored)
5. **Built-in defaults** - API URL: `http://localhost:8080`, Page Size: `20`

### Managing Configuration

Use the `music-config` command to manage settings:

```bash
# Show current configuration
./bin/music-config show

# Set API URL
./bin/music-config set api.url http://api.example.com:9090

# Set default output format to JSON
./bin/music-config set output.format json

# Set default page size
./bin/music-config set page.size 50

# Remove a setting (revert to default)
./bin/music-config unset api.url

# Reset all configuration
./bin/music-config reset
```

### Environment Variables

```bash
# Override API URL
export MUSIC_API_URL="http://localhost:9090"

# Set default output format (formatted or json)
export MUSIC_OUTPUT_FORMAT="json"

# Set default page size
export MUSIC_PAGE_SIZE=50
```

## Commands

All commands support these global options:

- `--help` - Show help message
- `--api-url <url>` - Override API URL for this command
- `--json` - Output raw JSON instead of formatted tables

### music-artist

Manage artists in the music library.

#### List Artists

```bash
music-artist list [options]

Options:
  --page <n>        Page number (default: 0)
  --size <n>        Page size (default: 20)
  --sort <field>    Sort by field (default: name)
```

**Examples:**
```bash
# List all artists
music-artist list

# List with pagination
music-artist list --page 0 --size 10

# Sort by name descending
music-artist list --sort "name,desc"
```

#### Get Artist

```bash
music-artist get <id> [options]

Options:
  --with-songs      Include artist's songs
  --with-albums     Include artist's albums
```

**Examples:**
```bash
# Get artist by ID
music-artist get 1

# Get artist with songs
music-artist get 1 --with-songs

# Get artist with both songs and albums
music-artist get 1 --with-songs --with-albums
```

#### Create Artist

```bash
music-artist create [options]

Options:
  --name <name>     Artist name (if not provided, interactive prompt)
```

**Examples:**
```bash
# Interactive mode
music-artist create

# Direct mode
music-artist create --name "Led Zeppelin"
```

#### Update Artist

```bash
music-artist update <id> [options]

Options:
  --name <name>     New artist name (if not provided, interactive prompt)
```

**Examples:**
```bash
# Interactive mode
music-artist update 1

# Direct mode
music-artist update 1 --name "The Beatles (Remastered)"
```

#### Delete Artist

```bash
music-artist delete <id> [options]

Options:
  --force           Skip confirmation prompt
```

**Examples:**
```bash
# Delete with confirmation
music-artist delete 1

# Delete without confirmation
music-artist delete 1 --force
```

#### List Artist's Songs

```bash
music-artist songs <id> [options]

Options:
  --page <n>        Page number (default: 0)
  --size <n>        Page size (default: 20)
  --sort <field>    Sort by field (default: title)
```

#### List Artist's Albums

```bash
music-artist albums <id> [options]

Options:
  --page <n>        Page number (default: 0)
  --size <n>        Page size (default: 20)
  --sort <field>    Sort by field (default: title)
```

---

### music-song

Manage songs in the music library.

#### List Songs

```bash
music-song list [options]

Options:
  --page <n>          Page number (default: 0)
  --size <n>          Page size (default: 20)
  --sort <field>      Sort by field (default: title)
  --artist <id>       Filter by artist ID
```

**Examples:**
```bash
# List all songs
music-song list

# List songs by specific artist
music-song list --artist 1

# List with sorting
music-song list --sort "releaseDate,desc"
```

#### Get Song

```bash
music-song get <id> [options]

Options:
  --with-albums       Include album information
```

**Examples:**
```bash
# Get song by ID
music-song get 5

# Get song with album details
music-song get 5 --with-albums
```

#### Create Song

```bash
music-song create [options]

Options:
  --title <title>           Song title (required)
  --length <seconds>        Length in seconds (required)
  --release-date <date>     Release date YYYY-MM-DD (required)
  --artist-id <id>          Artist ID (required)
```

**Examples:**
```bash
# Interactive mode (prompts for all fields)
music-song create

# Direct mode
music-song create --title "Stairway to Heaven" --length 482 \
  --release-date 1971-11-08 --artist-id 1
```

#### Update Song

```bash
music-song update <id> [options]

Options:
  --title <title>           New song title
  --length <seconds>        New length in seconds
  --release-date <date>     New release date YYYY-MM-DD
  --artist-id <id>          New artist ID
```

**Examples:**
```bash
# Update just the title
music-song update 5 --title "New Title"

# Update multiple fields
music-song update 5 --title "New Title" --length 300
```

#### Delete Song

```bash
music-song delete <id> [options]

Options:
  --force             Skip confirmation prompt
```

**Examples:**
```bash
# Delete with confirmation
music-song delete 5

# Delete without confirmation
music-song delete 5 --force
```

---

### music-album

Manage albums in the music library.

#### List Albums

```bash
music-album list [options]

Options:
  --page <n>          Page number (default: 0)
  --size <n>          Page size (default: 20)
  --sort <field>      Sort by field (default: title)
  --artist <id>       Filter by artist ID
```

**Examples:**
```bash
# List all albums
music-album list

# List albums by specific artist
music-album list --artist 1

# Sort by release date
music-album list --sort "releaseDate,desc"
```

#### Get Album

```bash
music-album get <id> [options]

Options:
  --with-songs        Include full song details (title, duration)
```

**Examples:**
```bash
# Get album by ID
music-album get 1

# Get album with full song information
music-album get 1 --with-songs
```

#### Create Album

```bash
music-album create [options]

Options:
  --title <title>           Album title (required)
  --release-date <date>     Release date YYYY-MM-DD (required)
  --artist-id <id>          Artist ID (required)
  --song-ids <ids>          Comma-separated song IDs (optional)
```

**Examples:**
```bash
# Interactive mode
music-album create

# Create album with songs
music-album create --title "Led Zeppelin IV" --release-date 1971-11-08 \
  --artist-id 1 --song-ids "1,2,3"

# Create empty album
music-album create --title "Abbey Road" --release-date 1969-09-26 --artist-id 2
```

#### Update Album

```bash
music-album update <id> [options]

Options:
  --title <title>           New album title
  --release-date <date>     New release date YYYY-MM-DD
  --artist-id <id>          New artist ID
  --song-ids <ids>          New comma-separated song IDs
```

**Examples:**
```bash
# Update title only
music-album update 1 --title "New Title"

# Update song list
music-album update 1 --song-ids "1,2,3,4"
```

#### Delete Album

```bash
music-album delete <id> [options]

Options:
  --force             Skip confirmation prompt
```

---

### music-search

Search across artists, songs, and albums.

```bash
music-search <query> [options]

Options:
  --type <type>       Filter by entity type (artist, song, album)
  --page <n>          Page number (default: 0)
  --size <n>          Page size (default: 20)
```

**Examples:**
```bash
# Search everything
music-search "queen"

# Search only artists
music-search "beatles" --type artist

# Search only songs
music-search "rhapsody" --type song

# Search only albums
music-search "opera" --type album

# Search with pagination
music-search "the" --page 0 --size 10
```

---

### music-config

Manage CLI configuration settings.

```bash
music-config [action] [options]

Actions:
  show                Show current configuration (default)
  set <key> <value>   Set a configuration value
  unset <key>         Remove a configuration value
  reset               Reset all configuration to defaults
  path                Show configuration file path
```

**Configuration Keys:**
- `api.url` - API base URL (default: `http://localhost:8080`)
- `output.format` - Output format: `formatted` or `json` (default: `formatted`)
- `page.size` - Default page size for lists (default: `20`)

**Examples:**
```bash
# Show current settings
music-config show

# Set API URL
music-config set api.url http://api.example.com:9090

# Set output format to JSON
music-config set output.format json

# Set default page size
music-config set page.size 50

# Remove a setting (revert to default)
music-config unset api.url

# Reset everything
music-config reset

# Show config file location
music-config path
```

## Examples

### Common Workflows

**Add a new artist and their songs:**
```bash
# Create artist
music-artist create --name "Pink Floyd"
# Note the artist ID from output (e.g., ID: 3)

# Create songs
music-song create --title "Comfortably Numb" --length 382 \
  --release-date 1979-11-30 --artist-id 3

music-song create --title "Wish You Were Here" --length 334 \
  --release-date 1975-09-12 --artist-id 3
```

**Create an album with existing songs:**
```bash
# List songs by artist to get IDs
music-artist songs 3

# Create album with those songs
music-album create --title "The Wall" --release-date 1979-11-30 \
  --artist-id 3 --song-ids "10,11,12"
```

**Browse the library:**
```bash
# List all artists
music-artist list

# View an artist's complete catalog
music-artist get 1 --with-songs --with-albums

# Search for specific content
music-search "comfortably"
```

**Export data as JSON:**
```bash
# Get all artists as JSON
music-artist --json list > artists.json

# Get specific artist data
music-artist --json get 1 --with-songs > artist-1.json

# Search results as JSON
music-search --json "queen" > search-results.json
```

### Batch Operations

**Update multiple songs:**
```bash
#!/bin/bash
# Update release dates for songs 1-5
for id in {1..5}; do
  music-song update $id --release-date 2020-01-01
done
```

**Export entire catalog:**
```bash
#!/bin/bash
# Export all data to JSON files
music-artist --json list --size 1000 > all-artists.json
music-song --json list --size 1000 > all-songs.json
music-album --json list --size 1000 > all-albums.json
```

## Troubleshooting

### Connection Errors

If you see "Cannot connect to API":

```bash
# Check if the API is running
curl http://localhost:8080/api/artists

# Verify API URL configuration
music-config show

# Set correct API URL
music-config set api.url http://localhost:8080
```

### Missing Dependencies

If you see "Missing required dependencies":

```bash
# macOS
brew install curl jq

# Ubuntu/Debian
sudo apt-get install curl jq

# RHEL/CentOS
sudo yum install curl jq
```

### Date Validation Errors

Dates must be in `YYYY-MM-DD` format:

```bash
# ✓ Correct
music-song create --release-date 1981-09-06

# ✗ Wrong
music-song create --release-date 09-06-1981
music-song create --release-date 1981/09/06
```

### Permission Errors

If scripts aren't executable:

```bash
chmod +x bin/music-*
```

### Getting Help

Every command has built-in help:

```bash
music-artist --help
music-song --help
music-album --help
music-search --help
music-config --help
```

### Debug Mode

Enable debug output to see API requests:

```bash
export DEBUG=true
music-artist list
```

### JSON Output

Use `--json` to see raw API responses for debugging:

```bash
music-artist --json get 1 | jq '.'
```

## Advanced Usage

### Custom API Endpoints

```bash
# Use different API for testing
music-artist --api-url http://staging.example.com:8080 list

# Or set it globally
export MUSIC_API_URL="http://staging.example.com:8080"
```

### Output Formatting

```bash
# Formatted tables (default)
music-artist list

# Raw JSON output
music-artist --json list

# JSON with jq processing
music-artist --json list | jq '.content[].name'

# Set default to JSON
music-config set output.format json
```

### Pagination

```bash
# Get first page
music-song list --page 0 --size 10

# Get second page
music-song list --page 1 --size 10

# Get all results (large page size)
music-song list --size 1000
```

### Sorting

```bash
# Sort ascending (default)
music-song list --sort "title"

# Sort descending
music-song list --sort "title,desc"

# Sort by release date
music-song list --sort "releaseDate,desc"

# Sort albums by title
music-album list --sort "title,asc"
```

## Tips and Best Practices

1. **Use `--help` frequently** - Every command has detailed help
2. **Start with `list`** - Get familiar with what's in the library
3. **Use `--json` for scripting** - Easier to parse programmatically
4. **Set sensible defaults** - Use `music-config` for frequently used settings
5. **Use `--force` carefully** - Skips deletion confirmations
6. **Check IDs before deleting** - Use `get` to verify the right item
7. **Use search** - Faster than browsing when you know what you want

## License

This CLI is part of the Music Library API project.
