#!/usr/bin/env bash
#
# Music Library API Demo - CLI Version
#
# This script demonstrates all features of the Music Library API
# using the music-cli command-line tools.
#

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
API_URL="${MUSIC_API_URL:-http://localhost:8080}"
CLI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../music-cli/bin" && pwd)"
PAUSE_DURATION=3

# Function to print section headers
section() {
    echo ""
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${MAGENTA}$1${NC}"
    echo -e "${MAGENTA}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
}

# Function to print step headers
step() {
    echo ""
    echo -e "${CYAN}▶ Step $1: $2${NC}"
    echo ""
}

# Function to pause between steps
pause() {
    sleep $PAUSE_DURATION
}

# Function to execute CLI command
run_cli() {
    local cmd="$1"
    shift
    echo -e "${YELLOW}Command: $cmd $@${NC}"
    "$CLI_DIR/$cmd" "$@"
    echo ""
}

# Function to execute CLI command and capture JSON output
run_cli_json() {
    local cmd="$1"
    shift
    echo -e "${YELLOW}Command: $cmd $@${NC}" >&2
    "$CLI_DIR/$cmd" "$@"
}

# Variables to store IDs
TALKING_HEADS_ID=""
DEPECHE_MODE_ID=""
ABBA_ID=""
SONG_IDS=()
ALBUM_IDS=()

# ============================================================================
# SECTION 1: Initial Setup
# ============================================================================

section "SECTION 1: Initial Setup"

step 1 "Display Welcome Message"
cat << 'EOF'
╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║        MUSIC LIBRARY API - COMPREHENSIVE DEMO (CLI)           ║
║                                                               ║
║  This demo will showcase all features of the Music Library   ║
║  API using the command-line interface.                        ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
EOF
pause

step 2 "Check API Connectivity"
run_cli music-config show
pause

step 3 "List Existing Artists (Starting State)"
run_cli music-artist list
pause

# ============================================================================
# SECTION 2: Artist Management
# ============================================================================

section "SECTION 2: Artist Management"

step 4 "Create Artist: Talking Heads"
RESPONSE=$(run_cli_json music-artist --json create --name "Talking Heads")
TALKING_HEADS_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "Created Talking Heads with ID: $TALKING_HEADS_ID"
pause

step 5 "Create Artist: Depeche Mode"
RESPONSE=$(run_cli_json music-artist --json create --name "Depeche Mode")
DEPECHE_MODE_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "Created Depeche Mode with ID: $DEPECHE_MODE_ID"
pause

step 6 "Create Artist: ABBA"
RESPONSE=$(run_cli_json music-artist --json create --name "ABBA")
ABBA_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "Created ABBA with ID: $ABBA_ID"
pause

step 7 "List All Artists"
run_cli music-artist list --size 10
pause

step 8 "Get Details for Talking Heads"
run_cli music-artist get "$TALKING_HEADS_ID"
pause

step 9 "Update Talking Heads's Name"
run_cli music-artist update "$TALKING_HEADS_ID" --name "Talking Heads (New Wave Legends)"
pause

step 10 "List Artists Again (Verify Update)"
run_cli music-artist list --size 10
pause

# ============================================================================
# SECTION 3: Song Management
# ============================================================================

section "SECTION 3: Song Management"

step 11 "Create Song: Psycho Killer by Talking Heads"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Psycho Killer" \
    --length 264 \
    --release-date 1977-09-16 \
    --artist-id "$TALKING_HEADS_ID")
SONG_IDS[0]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[0]}"
pause

step 12 "Create Song: Once in a Lifetime by Talking Heads"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Once in a Lifetime" \
    --length 260 \
    --release-date 1980-12-15 \
    --artist-id "$TALKING_HEADS_ID")
SONG_IDS[1]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[1]}"
pause

step 13 "Create Song: Burning Down the House by Talking Heads"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Burning Down the House" \
    --length 241 \
    --release-date 1983-07-08 \
    --artist-id "$TALKING_HEADS_ID")
SONG_IDS[2]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[2]}"
pause

step 14 "Create Song: Enjoy the Silence by Depeche Mode"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Enjoy the Silence" \
    --length 373 \
    --release-date 1990-02-05 \
    --artist-id "$DEPECHE_MODE_ID")
SONG_IDS[3]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[3]}"
pause

step 15 "Create Song: Personal Jesus by Depeche Mode"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Personal Jesus" \
    --length 279 \
    --release-date 1989-08-29 \
    --artist-id "$DEPECHE_MODE_ID")
SONG_IDS[4]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[4]}"
pause

step 16 "Create Song: Dancing Queen by ABBA"
RESPONSE=$(run_cli_json music-song --json create \
    --title "Dancing Queen" \
    --length 230 \
    --release-date 1976-08-16 \
    --artist-id "$ABBA_ID")
SONG_IDS[5]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created song with ID: ${SONG_IDS[5]}"
pause

step 17 "List All Songs"
run_cli music-song list
pause

step 18 "Get Details for Psycho Killer"
run_cli music-song get "${SONG_IDS[0]}"
pause

step 19 "Update Psycho Killer Length to 266 Seconds"
run_cli music-song update "${SONG_IDS[0]}" --length 266
pause

step 20 "List Songs Filtered by Talking Heads"
run_cli music-song list --artist "$TALKING_HEADS_ID"
pause

step 21 "List Songs Sorted by Release Date (Descending)"
run_cli music-song list --sort "releaseDate,desc"
pause

# ============================================================================
# SECTION 4: Album Management
# ============================================================================

section "SECTION 4: Album Management"

step 22 "Create Album: Talking Heads: 77 by Talking Heads"
RESPONSE=$(run_cli_json music-album --json create \
    --title "Talking Heads: 77" \
    --release-date 1977-09-16 \
    --artist-id "$TALKING_HEADS_ID" \
    --song-ids "${SONG_IDS[0]}")
ALBUM_IDS[0]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created album with ID: ${ALBUM_IDS[0]}"
pause

step 23 "Create Album: Remain in Light by Talking Heads"
RESPONSE=$(run_cli_json music-album --json create \
    --title "Remain in Light" \
    --release-date 1980-10-08 \
    --artist-id "$TALKING_HEADS_ID" \
    --song-ids "${SONG_IDS[1]}")
ALBUM_IDS[1]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created album with ID: ${ALBUM_IDS[1]}"
pause

step 24 "Create Album: Speaking in Tongues by Talking Heads"
RESPONSE=$(run_cli_json music-album --json create \
    --title "Speaking in Tongues" \
    --release-date 1983-05-31 \
    --artist-id "$TALKING_HEADS_ID" \
    --song-ids "${SONG_IDS[2]}")
ALBUM_IDS[2]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created album with ID: ${ALBUM_IDS[2]}"
pause

step 25 "Create Album: Violator by Depeche Mode"
RESPONSE=$(run_cli_json music-album --json create \
    --title "Violator" \
    --release-date 1990-03-19 \
    --artist-id "$DEPECHE_MODE_ID" \
    --song-ids "${SONG_IDS[3]}")
ALBUM_IDS[3]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created album with ID: ${ALBUM_IDS[3]}"
pause

step 26 "Create Album: Arrival by ABBA"
RESPONSE=$(run_cli_json music-album --json create \
    --title "Arrival" \
    --release-date 1976-10-11 \
    --artist-id "$ABBA_ID" \
    --song-ids "${SONG_IDS[5]}")
ALBUM_IDS[4]=$(echo "$RESPONSE" | jq -r '.id')
echo "Created album with ID: ${ALBUM_IDS[4]}"
pause

step 27 "List All Albums"
run_cli music-album list
pause

step 28 "Get Details for Talking Heads: 77 Album (with songs)"
run_cli music-album get "${ALBUM_IDS[0]}" --with-songs
pause

step 29 "Update Talking Heads: 77 Album Title"
run_cli music-album update "${ALBUM_IDS[0]}" --title "Talking Heads: 77 (Deluxe Edition)"
pause

step 30 "List Albums Filtered by Talking Heads"
run_cli music-album list --artist "$TALKING_HEADS_ID"
pause

step 31 "Get Talking Heads with Albums"
run_cli music-artist get "$TALKING_HEADS_ID" --with-albums
pause

step 32 "Get Talking Heads with Songs"
run_cli music-artist get "$TALKING_HEADS_ID" --with-songs
pause

# ============================================================================
# SECTION 5: Search Functionality
# ============================================================================

section "SECTION 5: Search Functionality"

step 33 "Search Across All Entities for 'Heads'"
run_cli music-search "Heads"
pause

step 34 "Search for 'Psycho'"
run_cli music-search "Psycho"
pause

step 35 "Search for 'Silence'"
run_cli music-search "Silence"
pause

step 36 "Search for 'Depeche' (Artists Only)"
run_cli music-search "Depeche" --type artist
pause

step 37 "Search for 'Dancing' (Songs Only)"
run_cli music-search "Dancing" --type song
pause

step 38 "Search for 'Violator' (Albums Only)"
run_cli music-search "Violator" --type album
pause

# ============================================================================
# SECTION 6: Pagination and Sorting
# ============================================================================

section "SECTION 6: Pagination and Sorting"

step 39 "List Songs with Page Size 2 (First Page)"
run_cli music-song list --page 0 --size 2
pause

step 40 "List Songs with Page Size 2 (Second Page)"
run_cli music-song list --page 1 --size 2
pause

step 41 "List Albums Sorted by Release Date (Ascending)"
run_cli music-album list --sort "releaseDate,asc"
pause

step 42 "List Albums Sorted by Release Date (Descending)"
run_cli music-album list --sort "releaseDate,desc"
pause

# ============================================================================
# SECTION 7: Deletion and Cleanup
# ============================================================================

section "SECTION 7: Deletion and Cleanup"

step 43 "Delete Song: Personal Jesus"
run_cli music-song delete "${SONG_IDS[4]}" --force
pause

step 44 "Delete Album: Violator"
run_cli music-album delete "${ALBUM_IDS[3]}" --force
pause

step 45 "List Remaining Albums"
run_cli music-album list
pause

# ============================================================================
# SECTION 8: Final State
# ============================================================================

section "SECTION 8: Final State"

step 46 "Display Final Statistics"
echo "Fetching final statistics..."
ARTIST_RESPONSE=$(run_cli_json music-artist --json list --size 1000)
SONG_RESPONSE=$(run_cli_json music-song --json list --size 1000)
ALBUM_RESPONSE=$(run_cli_json music-album --json list --size 1000)

TOTAL_ARTISTS=$(echo "$ARTIST_RESPONSE" | jq -r '.totalElements')
TOTAL_SONGS=$(echo "$SONG_RESPONSE" | jq -r '.totalElements')
TOTAL_ALBUMS=$(echo "$ALBUM_RESPONSE" | jq -r '.totalElements')

cat << EOF

╔════════════════════════════════════════╗
║      FINAL LIBRARY STATISTICS          ║
╠════════════════════════════════════════╣
║  Total Artists:  $TOTAL_ARTISTS                      ║
║  Total Songs:    $TOTAL_SONGS                      ║
║  Total Albums:   $TOTAL_ALBUMS                      ║
╚════════════════════════════════════════╝

EOF
pause

step 47 "List All Artists with Content"
run_cli music-artist list
pause

step 48 "Display Success Message"
cat << 'EOF'

╔═══════════════════════════════════════════════════════════════╗
║                                                               ║
║                    DEMO COMPLETED SUCCESSFULLY!               ║
║                                                               ║
║  Summary of Operations:                                       ║
║  ✓ Created 3 artists                                          ║
║  ✓ Created 6 songs                                            ║
║  ✓ Created 5 albums                                           ║
║  ✓ Updated 1 artist, 1 song, 1 album                          ║
║  ✓ Deleted 1 song, 1 album                                    ║
║  ✓ Demonstrated search, filtering, pagination, sorting        ║
║                                                               ║
║  The Music Library API is fully functional!                   ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝

EOF

echo -e "${GREEN}All CLI commands executed successfully!${NC}"
echo ""
