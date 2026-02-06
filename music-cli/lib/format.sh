#!/usr/bin/env bash
#
# format.sh - Output formatting functions
#

# Format duration (seconds) to MM:SS or HH:MM:SS
function format_duration() {
    local seconds="$1"

    if [[ -z "$seconds" ]] || [[ ! "$seconds" =~ ^[0-9]+$ ]]; then
        echo "0:00"
        return
    fi

    local hours=$((seconds / 3600))
    local minutes=$(( (seconds % 3600) / 60 ))
    local secs=$((seconds % 60))

    if [[ $hours -gt 0 ]]; then
        printf "%d:%02d:%02d" "$hours" "$minutes" "$secs"
    else
        printf "%d:%02d" "$minutes" "$secs"
    fi
}

# Format date (already in YYYY-MM-DD format, just return it)
function format_date() {
    local date="$1"
    echo "$date"
}

# Format pagination info
function format_pagination() {
    local json="$1"

    local page_number
    local total_pages
    local total_elements
    local number_of_elements

    page_number=$(echo "$json" | jq -r '.number // 0')
    total_pages=$(echo "$json" | jq -r '.totalPages // 0')
    total_elements=$(echo "$json" | jq -r '.totalElements // 0')
    number_of_elements=$(echo "$json" | jq -r '.numberOfElements // 0')

    # Page numbers are 0-indexed in API, show as 1-indexed to user
    local current_page=$((page_number + 1))

    if [[ "$total_pages" -eq 0 ]]; then
        echo "No results"
    else
        echo "Page $current_page of $total_pages ($total_elements total)"
    fi
}

# Pretty-print JSON
function format_json() {
    local json="$1"
    echo "$json" | jq '.'
}

# Format table header
function format_table_header() {
    local -a headers=("$@")
    printf "%s\n" "${headers[*]}"
    printf "%s\n" "$(echo "${headers[*]}" | sed 's/[^ ]/-/g')"
}

# Format artist list as table
function format_artist_list() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    # Check if there are any results
    local count
    count=$(echo "$json" | jq '.content | length')

    if [[ "$count" -eq 0 ]]; then
        echo "No artists found"
        return
    fi

    # Print header
    printf "%-6s  %-30s  %-6s  %-6s\n" "ID" "Name" "Songs" "Albums"
    printf "%-6s  %-30s  %-6s  %-6s\n" "------" "------------------------------" "------" "------"

    # Print artists
    echo "$json" | jq -r '.content[] | [.id, .name, .songCount, .albumCount] | @tsv' | \
    while IFS=$'\t' read -r id name song_count album_count; do
        printf "%-6s  %-30s  %-6s  %-6s\n" "$id" "$name" "$song_count" "$album_count"
    done

    echo ""
    format_pagination "$json"
}

# Format song list as table
function format_song_list() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    # Check if there are any results
    local count
    count=$(echo "$json" | jq '.content | length')

    if [[ "$count" -eq 0 ]]; then
        echo "No songs found"
        return
    fi

    # Print header
    printf "%-6s  %-30s  %-20s  %-8s  %-12s  %-6s\n" "ID" "Title" "Artist" "Length" "Release Date" "Albums"
    printf "%-6s  %-30s  %-20s  %-8s  %-12s  %-6s\n" "------" "------------------------------" "--------------------" "--------" "------------" "------"

    # Print songs
    echo "$json" | jq -r '.content[] | [.id, .title, .artist.name, .lengthInSeconds, .releaseDate, (.albums | length)] | @tsv' | \
    while IFS=$'\t' read -r id title artist length release_date album_count; do
        local formatted_length
        formatted_length=$(format_duration "$length")
        printf "%-6s  %-30s  %-20s  %-8s  %-12s  %-6s\n" "$id" "$title" "$artist" "$formatted_length" "$release_date" "$album_count"
    done

    echo ""
    format_pagination "$json"
}

# Format album list as table
function format_album_list() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    # Check if there are any results
    local count
    count=$(echo "$json" | jq '.content | length')

    if [[ "$count" -eq 0 ]]; then
        echo "No albums found"
        return
    fi

    # Print header
    printf "%-6s  %-30s  %-20s  %-12s  %-6s\n" "ID" "Title" "Artist" "Release Date" "Songs"
    printf "%-6s  %-30s  %-20s  %-12s  %-6s\n" "------" "------------------------------" "--------------------" "------------" "------"

    # Print albums
    echo "$json" | jq -r '.content[] | [.id, .title, (.artist.name // "Unknown"), .releaseDate, (.songs | length)] | @tsv' | \
    while IFS=$'\t' read -r id title artist release_date song_count; do
        printf "%-6s  %-30s  %-20s  %-12s  %-6s\n" "$id" "$title" "$artist" "$release_date" "$song_count"
    done

    echo ""
    format_pagination "$json"
}

# Format artist details
function format_artist_details() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    local id name
    id=$(echo "$json" | jq -r '.id')
    name=$(echo "$json" | jq -r '.name')

    echo "$(color_bold "Artist #$id")"
    echo "Name: $name"
}

# Format song details (full format with artist object or artistId)
function format_song_detail() {
    local json="$1"
    local with_albums="${2:-false}"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    local id title artist_name artist_id length release_date
    id=$(echo "$json" | jq -r '.id')
    title=$(echo "$json" | jq -r '.title')

    # Try to get artist info from nested object first, then fall back to artistId
    artist_name=$(echo "$json" | jq -r '.artist.name // empty')
    if [[ -z "$artist_name" ]]; then
        artist_id=$(echo "$json" | jq -r '.artistId // 0')
        artist_name="Artist ID: $artist_id"
    else
        artist_id=$(echo "$json" | jq -r '.artist.id // 0')
    fi

    length=$(echo "$json" | jq -r '.lengthInSeconds')
    release_date=$(echo "$json" | jq -r '.releaseDate')

    local formatted_length
    formatted_length=$(format_duration "$length")

    echo "$(color_bold "Song #$id")"
    echo "Title: $title"
    if [[ "$artist_name" != "Artist ID:"* ]]; then
        echo "Artist: $artist_name (ID: $artist_id)"
    else
        echo "Artist ID: $artist_id"
    fi
    echo "Length: $formatted_length ($length seconds)"
    echo "Release Date: $release_date"

    # Show albums if with_albums flag is set and albums are present
    if [[ "$with_albums" == "true" ]]; then
        local album_count
        album_count=$(echo "$json" | jq -r '.albums // [] | length')

        if [[ "$album_count" -gt 0 ]]; then
            echo ""
            echo "Albums ($album_count):"
            echo "$json" | jq -r '.albums[] | "  - \(.title) (ID: \(.id))"'
        fi
    fi
}

# Legacy format song details (for backward compatibility)
function format_song_details() {
    format_song_detail "$@"
}

# Format album details
function format_album_detail() {
    local json="$1"
    local with_songs="${2:-false}"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    local id title artist_id artist_name release_date
    id=$(echo "$json" | jq -r '.id')
    title=$(echo "$json" | jq -r '.title')
    artist_id=$(echo "$json" | jq -r '.artistId')
    artist_name=$(echo "$json" | jq -r '.artistName // empty')
    release_date=$(echo "$json" | jq -r '.releaseDate')

    echo "$(color_bold "Album #$id")"
    echo "Title: $title"
    if [[ -n "$artist_name" ]]; then
        echo "Artist: $artist_name (ID: $artist_id)"
    else
        echo "Artist ID: $artist_id"
    fi
    echo "Release Date: $release_date"

    # Show songs if present
    local song_count
    song_count=$(echo "$json" | jq '.songIds | length')

    if [[ "$song_count" -gt 0 ]]; then
        echo ""
        if [[ "$with_songs" == "true" ]]; then
            echo "Songs ($song_count):"
            # Fetch full song details for each song ID
            echo "$json" | jq -r '.songIds[]' | while read -r song_id; do
                local song_response
                song_response=$(api_get_song "$song_id" 2>/dev/null)
                if [[ $? -eq 0 ]]; then
                    local song_title song_length
                    song_title=$(echo "$song_response" | jq -r '.title')
                    song_length=$(echo "$song_response" | jq -r '.lengthInSeconds')
                    local formatted_length
                    formatted_length=$(format_duration "$song_length")
                    echo "  - $song_title ($formatted_length) [ID: $song_id]"
                else
                    echo "  - Song ID: $song_id"
                fi
            done
        else
            echo "Song IDs ($song_count): $(echo "$json" | jq -r '.songIds | join(", ")')"
        fi
    fi
}

# Legacy format album details (for backward compatibility)
function format_album_details() {
    format_album_detail "$@"
}

# Format search results
function format_search_results() {
    local json="$1"

    if [[ "$OUTPUT_FORMAT" == "json" ]]; then
        format_json "$json"
        return
    fi

    # Check if there are any results
    local count
    count=$(echo "$json" | jq '.content | length')

    if [[ "$count" -eq 0 ]]; then
        echo "No results found"
        return
    fi

    # Group by entity type
    local artists songs albums

    artists=$(echo "$json" | jq -r '.content[] | select(.entityType == "ARTIST")')
    songs=$(echo "$json" | jq -r '.content[] | select(.entityType == "SONG")')
    albums=$(echo "$json" | jq -r '.content[] | select(.entityType == "ALBUM")')

    # Show artists
    if [[ -n "$artists" ]]; then
        echo "$(color_bold "Artists:")"
        echo "$artists" | jq -r '[.id, .name] | @tsv' | \
        while IFS=$'\t' read -r id name; do
            printf "  #%-6s  %s\n" "$id" "$name"
        done
        echo ""
    fi

    # Show songs
    if [[ -n "$songs" ]]; then
        echo "$(color_bold "Songs:")"
        echo "$songs" | jq -r '[.id, .name, .artistName, .releaseDate] | @tsv' | \
        while IFS=$'\t' read -r id title artist date; do
            printf "  #%-6s  %-30s  %-20s  %s\n" "$id" "$title" "$artist" "$date"
        done
        echo ""
    fi

    # Show albums
    if [[ -n "$albums" ]]; then
        echo "$(color_bold "Albums:")"
        echo "$albums" | jq -r '[.id, .name, .artistName, .releaseDate] | @tsv' | \
        while IFS=$'\t' read -r id title artist date; do
            printf "  #%-6s  %-30s  %-20s  %s\n" "$id" "$title" "$artist" "$date"
        done
        echo ""
    fi

    format_pagination "$json"
}
