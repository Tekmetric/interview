# Music Library API - Demo Scripts

This directory contains comprehensive demonstration scripts that showcase all features of the Music Library API.

## Available Demos

### 1. CLI Demo (`demo-cli.sh`)
Uses the Music Library CLI commands to interact with the API.

**Features:**
- User-friendly command-line interface
- Formatted table output
- Built-in help and error messages
- Configuration management

**To run:**
```bash
./demo-cli.sh
```

### 2. cURL Demo (`demo-curl.sh`)
Uses direct HTTP requests via curl to interact with the API.

**Features:**
- Raw HTTP request/response visibility
- JSON request bodies
- Direct API endpoint access
- Lower-level API interaction

**To run:**
```bash
./demo-curl.sh
```

## Prerequisites

### For Both Demos
1. **Music Library API must be running:**
   ```bash
   cd ../backend
   mvn spring-boot:run
   ```
   Or verify it's running:
   ```bash
   curl http://localhost:8080/api/artists
   ```

2. **jq** - JSON processor
   ```bash
   # macOS
   brew install jq

   # Ubuntu/Debian
   sudo apt-get install jq

   # RHEL/CentOS
   sudo yum install jq
   ```

### Additional for CLI Demo
3. **Music CLI** - Must be built/available
   - Scripts expect CLI to be at `../music-cli/bin/`

### Additional for cURL Demo
3. **curl** - Usually pre-installed on most systems

## What the Demos Demonstrate

Both scripts execute the same sequence of operations, showcasing:

### Section 1: Initial Setup
- API connectivity check
- Display starting state

### Section 2: Artist Management (3 artists)
- Create: Pink Floyd, Led Zeppelin, AC/DC
- List artists
- Get individual artist
- Update artist name
- Verify update

### Section 3: Song Management (6 songs)
- Create songs for each artist:
  - Pink Floyd: "Comfortably Numb", "Wish You Were Here", "Shine On You Crazy Diamond"
  - Led Zeppelin: "Stairway to Heaven", "Kashmir"
  - AC/DC: "Back in Black"
- List all songs
- Get individual song
- Update song (partial update - length only)
- Filter songs by artist
- Sort songs by release date

### Section 4: Album Management (5 albums)
- Create albums with song associations:
  - Pink Floyd: "The Wall", "Wish You Were Here"
  - Led Zeppelin: "Led Zeppelin IV", "Physical Graffiti"
  - AC/DC: "Back in Black"
- List all albums
- Get album with song details
- Update album title
- Filter albums by artist
- Get artist with all albums
- Get artist with all songs

### Section 5: Search Functionality
- Search across all entities
- Search for specific terms ("Floyd", "heaven", "black")
- Search filtered by entity type:
  - Artists only
  - Songs only
  - Albums only

### Section 6: Pagination and Sorting
- Demonstrate pagination (page 0 and 1 with size 2)
- Sort albums by release date (ascending and descending)

### Section 7: Deletion and Cleanup
- Delete a song ("Kashmir")
- Delete an album ("Physical Graffiti")
- Verify deletion

### Section 8: Final State
- Display final statistics (total counts)
- List all artists with their content
- Success message

## Script Behavior

### Timing
- Each operation pauses for **3 seconds** before proceeding
- Total runtime: ~2-3 minutes per script
- Allows time to observe each operation's output

### Output
- **CLI Demo:** Formatted tables and user-friendly messages
- **cURL Demo:** HTTP requests, JSON bodies, and responses

### Error Handling
- Scripts use `set -e` to exit on first error
- Any API failure will stop the demo
- Check API is running if errors occur

## Customization

### Change API URL
Both scripts respect the `MUSIC_API_URL` environment variable:

```bash
export MUSIC_API_URL="http://localhost:9090"
./demo-cli.sh
```

Or edit the scripts directly to change the default.

### Change Pause Duration
Edit the `PAUSE_DURATION` variable in either script:

```bash
PAUSE_DURATION=1  # Change from 3 to 1 second
```

### Modify Operations
Both scripts are well-commented. Feel free to:
- Add more entities
- Change entity names
- Add additional test scenarios
- Remove sections you don't need

## Interpreting Results

### Success Indicators
- CLI Demo: Green checkmarks (✓) and success messages
- cURL Demo: HTTP 200/201 responses, valid JSON

### Expected Final State
After running either demo:
- **Artists:** Initial count + 3 new artists
- **Songs:** Initial count + 5 new songs (6 created, 1 deleted)
- **Albums:** Initial count + 4 new albums (5 created, 1 deleted)

### Common Issues

**"Cannot connect to API"**
- Ensure the API is running: `curl http://localhost:8080/api/artists`
- Check the API URL configuration

**"command not found: jq"**
- Install jq (see Prerequisites)

**CLI Demo: "music-artist: command not found"**
- Verify CLI path is correct
- Scripts expect: `../music-cli/bin/music-artist`

**Duplicate entities**
- Running the demo multiple times creates duplicate data
- This is expected and demonstrates POST operations work correctly

## Demo Plan Document

See `DEMO_PLAN.txt` for a detailed plain-text description of all operations performed by the demo scripts.

## Use Cases

### Development
- Verify API is working after code changes
- Test all endpoints quickly
- Demonstrate features to team members

### Testing
- Smoke test after deployment
- Integration test for full workflow
- Performance baseline (with timing adjustments)

### Documentation
- Show API capabilities
- Provide usage examples
- Training new developers

### Presentations
- Live API demonstration
- Feature showcase
- API walkthrough

## Notes

- Scripts are idempotent-safe (can be run multiple times)
- Each run creates new entities (IDs will differ)
- Scripts don't clean up data created (by design)
- To reset database, restart the API with fresh data.sql

## Troubleshooting

### Script Fails Partway Through
1. Note which step failed
2. Check API logs for errors
3. Verify the entity was/wasn't created
4. You can manually continue from that point

### Want to Reset Database
Restart the Spring Boot application:
```bash
cd ../backend
# Stop the app (Ctrl+C)
mvn spring-boot:run
```

### Want to See Raw Responses
- CLI Demo: Add `--json` flag to commands in the script
- cURL Demo: Remove `| jq '.'` to see unformatted JSON

## License

These demo scripts are part of the Music Library API interview project.
