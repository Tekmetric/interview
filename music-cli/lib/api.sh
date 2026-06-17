#!/usr/bin/env bash
#
# api.sh - API communication functions
#

# Make HTTP request with error handling
# Usage: http_request <method> <url> [body]
# Returns JSON response on success, exits on error
function http_request() {
    local method="$1"
    local url="$2"
    local body="${3:-}"

    local response http_code

    log_debug "HTTP $method $url"

    if [[ -n "$body" ]]; then
        log_debug "Request body: $body"
        response=$(curl -s -w "\n%{http_code}" \
            -X "$method" \
            -H "Content-Type: application/json" \
            -d "$body" \
            "$url" 2>&1)
    else
        response=$(curl -s -w "\n%{http_code}" \
            -X "$method" \
            "$url" 2>&1)
    fi

    # Extract HTTP code from last line
    http_code=$(echo "$response" | tail -n1)
    response=$(echo "$response" | sed '$d')

    log_debug "HTTP $http_code"

    case "$http_code" in
        200|201|204)
            # Success
            if [[ "$http_code" == "204" ]]; then
                echo "{}"  # No content
            else
                echo "$response"
            fi
            return 0
            ;;
        400)
            log_error "Bad Request: Invalid input"
            if [[ -n "$response" ]]; then
                log_debug "Error details: $response"
                # Try to extract error message from JSON
                local error_msg
                error_msg=$(echo "$response" | jq -r '.message // .error // empty' 2>/dev/null)
                if [[ -n "$error_msg" ]]; then
                    echo "$error_msg" >&2
                fi
            fi
            return $EXIT_ERROR
            ;;
        404)
            log_error "Not Found: The requested resource does not exist"
            return $EXIT_ERROR
            ;;
        500)
            log_error "Server Error: The server encountered an error"
            if [[ -n "$response" ]]; then
                log_debug "Error details: $response"
            fi
            return $EXIT_SERVER
            ;;
        000)
            log_error "Cannot connect to API at $API_URL"
            echo "Please check that the Music Library API server is running." >&2
            return $EXIT_SERVER
            ;;
        *)
            log_error "HTTP $http_code: Unexpected error"
            if [[ -n "$response" ]]; then
                log_debug "Response: $response"
            fi
            return $EXIT_ERROR
            ;;
    esac
}

# Build URL with query parameters
function build_url() {
    local path="$1"
    shift
    local params=("$@")

    local url="${API_URL}${path}"

    if [[ ${#params[@]} -gt 0 ]]; then
        url="${url}?"
        local first=true
        for param in "${params[@]}"; do
            if [[ "$first" == "true" ]]; then
                first=false
            else
                url="${url}&"
            fi
            url="${url}${param}"
        done
    fi

    echo "$url"
}

# URL encode a string
function url_encode() {
    local string="$1"
    local encoded=""
    local pos c o

    for ((pos=0; pos<${#string}; pos++)); do
        c=${string:$pos:1}
        case "$c" in
            [-_.~a-zA-Z0-9])
                o="$c"
                ;;
            *)
                printf -v o '%%%02X' "'$c"
                ;;
        esac
        encoded="${encoded}${o}"
    done

    echo "$encoded"
}

#
# Artist API functions
#

function api_get_artists() {
    local page="${1:-0}"
    local size="${2:-$PAGE_SIZE}"
    local sort="${3:-name,asc}"

    local url
    url=$(build_url "/api/artists" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

function api_get_artist() {
    local id="$1"

    local url
    url=$(build_url "/api/artists/$id")
    http_request "GET" "$url"
}

function api_create_artist() {
    local name="$1"

    local body
    body=$(jq -n --arg name "$name" '{name: $name}')

    local url
    url=$(build_url "/api/artists")
    http_request "POST" "$url" "$body"
}

function api_update_artist() {
    local id="$1"
    local name="$2"

    local body
    body=$(jq -n --arg name "$name" '{name: $name}')

    local url
    url=$(build_url "/api/artists/$id")
    http_request "PUT" "$url" "$body"
}

function api_delete_artist() {
    local id="$1"

    local url
    url=$(build_url "/api/artists/$id")
    http_request "DELETE" "$url"
}

function api_get_artist_songs() {
    local id="$1"
    local page="${2:-0}"
    local size="${3:-$PAGE_SIZE}"
    local sort="${4:-title,asc}"

    local url
    url=$(build_url "/api/artists/$id/songs" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

function api_get_artist_albums() {
    local id="$1"
    local page="${2:-0}"
    local size="${3:-$PAGE_SIZE}"
    local sort="${4:-title,asc}"

    local url
    url=$(build_url "/api/artists/$id/albums" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

#
# Song API functions
#

function api_get_songs() {
    local page="${1:-0}"
    local size="${2:-$PAGE_SIZE}"
    local sort="${3:-title,asc}"

    local url
    url=$(build_url "/api/songs" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

function api_get_song() {
    local id="$1"

    local url
    url=$(build_url "/api/songs/$id")
    http_request "GET" "$url"
}

function api_create_song() {
    local title="$1"
    local artist_id="$2"
    local length="$3"
    local release_date="$4"
    local album_ids="${5:-}"

    local body

    if [[ -n "$album_ids" ]]; then
        # Convert comma-separated IDs to JSON array
        local album_ids_json
        album_ids_json=$(echo "$album_ids" | tr ',' '\n' | jq -R . | jq -s .)

        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg length "$length" \
            --arg releaseDate "$release_date" \
            --argjson albumIds "$album_ids_json" \
            '{title: $title, artistId: ($artistId | tonumber), length: ($length | tonumber), releaseDate: $releaseDate, albumIds: ($albumIds | map(tonumber))}')
    else
        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg length "$length" \
            --arg releaseDate "$release_date" \
            '{title: $title, artistId: ($artistId | tonumber), length: ($length | tonumber), releaseDate: $releaseDate}')
    fi

    local url
    url=$(build_url "/api/songs")
    http_request "POST" "$url" "$body"
}

function api_update_song() {
    local id="$1"
    local title="$2"
    local artist_id="$3"
    local length="$4"
    local release_date="$5"
    local album_ids="${6:-}"

    local body

    if [[ -n "$album_ids" ]]; then
        local album_ids_json
        album_ids_json=$(echo "$album_ids" | tr ',' '\n' | jq -R . | jq -s .)

        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg length "$length" \
            --arg releaseDate "$release_date" \
            --argjson albumIds "$album_ids_json" \
            '{title: $title, artistId: ($artistId | tonumber), length: ($length | tonumber), releaseDate: $releaseDate, albumIds: ($albumIds | map(tonumber))}')
    else
        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg length "$length" \
            --arg releaseDate "$release_date" \
            '{title: $title, artistId: ($artistId | tonumber), length: ($length | tonumber), releaseDate: $releaseDate, albumIds: []}')
    fi

    local url
    url=$(build_url "/api/songs/$id")
    http_request "PUT" "$url" "$body"
}

function api_delete_song() {
    local id="$1"

    local url
    url=$(build_url "/api/songs/$id")
    http_request "DELETE" "$url"
}

# JSON payload versions for create/update
function api_create_song_json() {
    local payload="$1"

    local url
    url=$(build_url "/api/songs")
    http_request "POST" "$url" "$payload"
}

function api_update_song_json() {
    local id="$1"
    local payload="$2"

    local url
    url=$(build_url "/api/songs/$id")
    http_request "PUT" "$url" "$payload"
}

#
# Album API functions
#

function api_get_albums() {
    local page="${1:-0}"
    local size="${2:-$PAGE_SIZE}"
    local sort="${3:-title,asc}"

    local url
    url=$(build_url "/api/albums" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

function api_get_album() {
    local id="$1"

    local url
    url=$(build_url "/api/albums/$id")
    http_request "GET" "$url"
}

function api_create_album() {
    local title="$1"
    local artist_id="$2"
    local release_date="$3"
    local song_ids="${4:-}"

    local body

    if [[ -n "$song_ids" ]]; then
        local song_ids_json
        song_ids_json=$(echo "$song_ids" | tr ',' '\n' | jq -R . | jq -s .)

        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg releaseDate "$release_date" \
            --argjson songIds "$song_ids_json" \
            '{title: $title, artistId: ($artistId | tonumber), releaseDate: $releaseDate, songIds: ($songIds | map(tonumber))}')
    else
        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg releaseDate "$release_date" \
            '{title: $title, artistId: ($artistId | tonumber), releaseDate: $releaseDate}')
    fi

    local url
    url=$(build_url "/api/albums")
    http_request "POST" "$url" "$body"
}

function api_update_album() {
    local id="$1"
    local title="$2"
    local artist_id="$3"
    local release_date="$4"
    local song_ids="${5:-}"

    local body

    if [[ -n "$song_ids" ]]; then
        local song_ids_json
        song_ids_json=$(echo "$song_ids" | tr ',' '\n' | jq -R . | jq -s .)

        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg releaseDate "$release_date" \
            --argjson songIds "$song_ids_json" \
            '{title: $title, artistId: ($artistId | tonumber), releaseDate: $releaseDate, songIds: ($songIds | map(tonumber))}')
    else
        body=$(jq -n \
            --arg title "$title" \
            --arg artistId "$artist_id" \
            --arg releaseDate "$release_date" \
            '{title: $title, artistId: ($artistId | tonumber), releaseDate: $releaseDate, songIds: []}')
    fi

    local url
    url=$(build_url "/api/albums/$id")
    http_request "PUT" "$url" "$body"
}

function api_delete_album() {
    local id="$1"

    local url
    url=$(build_url "/api/albums/$id")
    http_request "DELETE" "$url"
}

function api_get_album_songs() {
    local id="$1"
    local page="${2:-0}"
    local size="${3:-$PAGE_SIZE}"
    local sort="${4:-title,asc}"

    local url
    url=$(build_url "/api/albums/$id/songs" "page=$page" "size=$size" "sort=$sort")
    http_request "GET" "$url"
}

#
# Search API functions
#

function api_search() {
    local query="$1"
    local page="${2:-0}"
    local size="${3:-$PAGE_SIZE}"

    local encoded_query
    encoded_query=$(url_encode "$query")

    local url
    url=$(build_url "/api/search" "q=$encoded_query" "page=$page" "size=$size")
    http_request "GET" "$url"
}

function api_search_by_type() {
    local query="$1"
    local type="$2"
    local page="${3:-0}"
    local size="${4:-$PAGE_SIZE}"

    local encoded_query
    encoded_query=$(url_encode "$query")

    local url
    url=$(build_url "/api/search" "q=$encoded_query" "type=$type" "page=$page" "size=$size")
    http_request "GET" "$url"
}

function api_search_by_artist() {
    local artist="$1"
    local page="${2:-0}"
    local size="${3:-$PAGE_SIZE}"

    local encoded_artist
    encoded_artist=$(url_encode "$artist")

    local url
    url=$(build_url "/api/search" "artist=$encoded_artist" "page=$page" "size=$size")
    http_request "GET" "$url"
}
