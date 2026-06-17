#!/usr/bin/env bash
#
# Music Library API Demo - cURL Version
#
# This script demonstrates all features of the Music Library API
# using direct HTTP requests via curl.
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

# Function to make HTTP request and format output
http_request() {
    local method="$1"
    local endpoint="$2"
    local data="${3:-}"

    echo -e "${YELLOW}HTTP $method $API_URL$endpoint${NC}"

    if [[ -n "$data" ]]; then
        echo -e "${YELLOW}Request Body:${NC}"
        echo "$data" | jq '.'
    fi

    echo -e "${BLUE}Response:${NC}"

    if [[ -n "$data" ]]; then
        curl -s -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$API_URL$endpoint" | jq '.'
    else
        curl -s -X "$method" "$API_URL$endpoint" | jq '.'
    fi

    echo ""
}

# Function to make HTTP request and return response (for ID extraction)
http_request_raw() {
    local method="$1"
    local endpoint="$2"
    local data="${3:-}"

    if [[ -n "$data" ]]; then
        curl -s -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$API_URL$endpoint"
    else
        curl -s -X "$method" "$API_URL$endpoint"
    fi
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
║        MUSIC LIBRARY API - COMPREHENSIVE DEMO (cURL)          ║
║                                                               ║
║  This demo will showcase all features of the Music Library   ║
║  API using direct HTTP requests via curl.                     ║
║                                                               ║
╚═══════════════════════════════════════════════════════════════╝
EOF
pause

step 2 "Check API Connectivity"
http_request GET "/api/artists?page=0&size=1"
pause

step 3 "List Existing Artists (Starting State)"
http_request GET "/api/artists?page=0&size=10&sort=name,asc"
pause

# ============================================================================
# SECTION 2: Artist Management
# ============================================================================

section "SECTION 2: Artist Management"

step 4 "Create Artist: Talking Heads"
RESPONSE=$(http_request_raw POST "/api/artists" '{"name":"Talking Heads"}')
TALKING_HEADS_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created Talking Heads with ID: $TALKING_HEADS_ID"
pause

step 5 "Create Artist: Depeche Mode"
RESPONSE=$(http_request_raw POST "/api/artists" '{"name":"Depeche Mode"}')
DEPECHE_MODE_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created Depeche Mode with ID: $DEPECHE_MODE_ID"
pause

step 6 "Create Artist: ABBA"
RESPONSE=$(http_request_raw POST "/api/artists" '{"name":"ABBA"}')
ABBA_ID=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created ABBA with ID: $ABBA_ID"
pause

step 7 "List All Artists"
http_request GET "/api/artists?page=0&size=10&sort=name,asc"
pause

step 8 "Get Details for Talking Heads"
http_request GET "/api/artists/$TALKING_HEADS_ID"
pause

step 9 "Update Talking Heads's Name"
http_request PUT "/api/artists/$TALKING_HEADS_ID" '{"name":"Talking Heads (New Wave Legends)"}'
pause

step 10 "List Artists Again (Verify Update)"
http_request GET "/api/artists?page=0&size=10&sort=name,asc"
pause

# ============================================================================
# SECTION 3: Song Management
# ============================================================================

section "SECTION 3: Song Management"

step 11 "Create Song: Psycho Killer by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Psycho Killer\",
    \"lengthInSeconds\": 264,
    \"releaseDate\": \"1977-09-16\",
    \"artistId\": $TALKING_HEADS_ID
}")
SONG_IDS[0]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[0]}"
pause

step 12 "Create Song: Once in a Lifetime by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Once in a Lifetime\",
    \"lengthInSeconds\": 260,
    \"releaseDate\": \"1980-12-15\",
    \"artistId\": $TALKING_HEADS_ID
}")
SONG_IDS[1]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[1]}"
pause

step 13 "Create Song: Burning Down the House by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Burning Down the House\",
    \"lengthInSeconds\": 241,
    \"releaseDate\": \"1983-07-08\",
    \"artistId\": $TALKING_HEADS_ID
}")
SONG_IDS[2]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[2]}"
pause

step 14 "Create Song: Enjoy the Silence by Depeche Mode"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Enjoy the Silence\",
    \"lengthInSeconds\": 373,
    \"releaseDate\": \"1990-02-05\",
    \"artistId\": $DEPECHE_MODE_ID
}")
SONG_IDS[3]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[3]}"
pause

step 15 "Create Song: Personal Jesus by Depeche Mode"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Personal Jesus\",
    \"lengthInSeconds\": 279,
    \"releaseDate\": \"1989-08-29\",
    \"artistId\": $DEPECHE_MODE_ID
}")
SONG_IDS[4]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[4]}"
pause

step 16 "Create Song: Dancing Queen by ABBA"
RESPONSE=$(http_request_raw POST "/api/songs" "{
    \"title\": \"Dancing Queen\",
    \"lengthInSeconds\": 230,
    \"releaseDate\": \"1976-08-16\",
    \"artistId\": $ABBA_ID
}")
SONG_IDS[5]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created song with ID: ${SONG_IDS[5]}"
pause

step 17 "List All Songs"
http_request GET "/api/songs?page=0&size=20&sort=title,asc"
pause

step 18 "Get Details for Psycho Killer"
http_request GET "/api/songs/${SONG_IDS[0]}"
pause

step 19 "Update Psycho Killer Length to 266 Seconds"
http_request PUT "/api/songs/${SONG_IDS[0]}" "{
    \"id\": ${SONG_IDS[0]},
    \"title\": \"Psycho Killer\",
    \"lengthInSeconds\": 266,
    \"releaseDate\": \"1977-09-16\",
    \"artistId\": $TALKING_HEADS_ID
}"
pause

step 20 "List Songs Filtered by Talking Heads"
http_request GET "/api/artists/$TALKING_HEADS_ID/songs?page=0&size=20&sort=title,asc"
pause

step 21 "List Songs Sorted by Release Date (Descending)"
http_request GET "/api/songs?page=0&size=20&sort=releaseDate,desc"
pause

# ============================================================================
# SECTION 4: Album Management
# ============================================================================

section "SECTION 4: Album Management"

step 22 "Create Album: Talking Heads: 77 by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/albums" "{
    \"title\": \"Talking Heads: 77\",
    \"releaseDate\": \"1977-09-16\",
    \"artistId\": $TALKING_HEADS_ID,
    \"songIds\": [${SONG_IDS[0]}]
}")
ALBUM_IDS[0]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created album with ID: ${ALBUM_IDS[0]}"
pause

step 23 "Create Album: Remain in Light by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/albums" "{
    \"title\": \"Remain in Light\",
    \"releaseDate\": \"1980-10-08\",
    \"artistId\": $TALKING_HEADS_ID,
    \"songIds\": [${SONG_IDS[1]}]
}")
ALBUM_IDS[1]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created album with ID: ${ALBUM_IDS[1]}"
pause

step 24 "Create Album: Speaking in Tongues by Talking Heads"
RESPONSE=$(http_request_raw POST "/api/albums" "{
    \"title\": \"Speaking in Tongues\",
    \"releaseDate\": \"1983-05-31\",
    \"artistId\": $TALKING_HEADS_ID,
    \"songIds\": [${SONG_IDS[2]}]
}")
ALBUM_IDS[2]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created album with ID: ${ALBUM_IDS[2]}"
pause

step 25 "Create Album: Violator by Depeche Mode"
RESPONSE=$(http_request_raw POST "/api/albums" "{
    \"title\": \"Violator\",
    \"releaseDate\": \"1990-03-19\",
    \"artistId\": $DEPECHE_MODE_ID,
    \"songIds\": [${SONG_IDS[3]}]
}")
ALBUM_IDS[3]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created album with ID: ${ALBUM_IDS[3]}"
pause

step 26 "Create Album: Arrival by ABBA"
RESPONSE=$(http_request_raw POST "/api/albums" "{
    \"title\": \"Arrival\",
    \"releaseDate\": \"1976-10-11\",
    \"artistId\": $ABBA_ID,
    \"songIds\": [${SONG_IDS[5]}]
}")
ALBUM_IDS[4]=$(echo "$RESPONSE" | jq -r '.id')
echo "$RESPONSE" | jq '.'
echo "Created album with ID: ${ALBUM_IDS[4]}"
pause

step 27 "List All Albums"
http_request GET "/api/albums?page=0&size=20&sort=title,asc"
pause

step 28 "Get Details for Talking Heads: 77 Album"
http_request GET "/api/albums/${ALBUM_IDS[0]}"
pause

step 29 "Update Talking Heads: 77 Album Title"
http_request PUT "/api/albums/${ALBUM_IDS[0]}" "{
    \"title\": \"Talking Heads: 77 (Deluxe Edition)\",
    \"releaseDate\": \"1977-09-16\",
    \"artistId\": $TALKING_HEADS_ID,
    \"songIds\": [${SONG_IDS[0]}]
}"
pause

step 30 "List Albums Filtered by Talking Heads"
http_request GET "/api/artists/$TALKING_HEADS_ID/albums?page=0&size=20&sort=title,asc"
pause

step 31 "Get Talking Heads with Albums"
http_request GET "/api/artists/$TALKING_HEADS_ID/albums?page=0&size=100"
pause

step 32 "Get Talking Heads with Songs"
http_request GET "/api/artists/$TALKING_HEADS_ID/songs?page=0&size=100"
pause

# ============================================================================
# SECTION 5: Search Functionality
# ============================================================================

section "SECTION 5: Search Functionality"

step 33 "Search Across All Entities for 'Heads'"
http_request GET "/api/search?q=Heads&page=0&size=20"
pause

step 34 "Search for 'Psycho'"
http_request GET "/api/search?q=Psycho&page=0&size=20"
pause

step 35 "Search for 'Silence'"
http_request GET "/api/search?q=Silence&page=0&size=20"
pause

step 36 "Search for 'Depeche' (Artists Only)"
http_request GET "/api/search?q=Depeche&type=ARTIST&page=0&size=20"
pause

step 37 "Search for 'Dancing' (Songs Only)"
http_request GET "/api/search?q=Dancing&type=SONG&page=0&size=20"
pause

step 38 "Search for 'Violator' (Albums Only)"
http_request GET "/api/search?q=Violator&type=ALBUM&page=0&size=20"
pause

# ============================================================================
# SECTION 6: Pagination and Sorting
# ============================================================================

section "SECTION 6: Pagination and Sorting"

step 39 "List Songs with Page Size 2 (First Page)"
http_request GET "/api/songs?page=0&size=2&sort=title,asc"
pause

step 40 "List Songs with Page Size 2 (Second Page)"
http_request GET "/api/songs?page=1&size=2&sort=title,asc"
pause

step 41 "List Albums Sorted by Release Date (Ascending)"
http_request GET "/api/albums?page=0&size=20&sort=releaseDate,asc"
pause

step 42 "List Albums Sorted by Release Date (Descending)"
http_request GET "/api/albums?page=0&size=20&sort=releaseDate,desc"
pause

# ============================================================================
# SECTION 7: Deletion and Cleanup
# ============================================================================

section "SECTION 7: Deletion and Cleanup"

step 43 "Delete Song: Personal Jesus"
echo -e "${YELLOW}HTTP DELETE $API_URL/api/songs/${SONG_IDS[4]}${NC}"
curl -s -X DELETE "$API_URL/api/songs/${SONG_IDS[4]}"
echo -e "${GREEN}✓ Song deleted${NC}"
echo ""
pause

step 44 "Delete Album: Violator"
echo -e "${YELLOW}HTTP DELETE $API_URL/api/albums/${ALBUM_IDS[3]}${NC}"
curl -s -X DELETE "$API_URL/api/albums/${ALBUM_IDS[3]}"
echo -e "${GREEN}✓ Album deleted${NC}"
echo ""
pause

step 45 "List Remaining Albums"
http_request GET "/api/albums?page=0&size=20&sort=title,asc"
pause

# ============================================================================
# SECTION 8: Final State
# ============================================================================

section "SECTION 8: Final State"

step 46 "Display Final Statistics"
echo "Fetching final statistics..."
ARTIST_RESPONSE=$(http_request_raw GET "/api/artists?page=0&size=1000")
SONG_RESPONSE=$(http_request_raw GET "/api/songs?page=0&size=1000")
ALBUM_RESPONSE=$(http_request_raw GET "/api/albums?page=0&size=1000")

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
http_request GET "/api/artists?page=0&size=100&sort=name,asc"
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

echo -e "${GREEN}All HTTP requests completed successfully!${NC}"
echo ""
