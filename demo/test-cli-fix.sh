#!/usr/bin/env bash
CLI_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../music-cli/bin" && pwd)"

# Test the fixed run_cli_json function
run_cli_json() {
    local cmd="$1"
    shift
    echo "Command: $cmd $@" >&2
    "$CLI_DIR/$cmd" "$@"
}

# Test that it works
echo "Testing JSON capture..."
RESPONSE=$(run_cli_json music-config show --json)
echo "Response captured successfully"
echo "$RESPONSE" | jq '.' > /dev/null 2>&1 && echo "✓ Valid JSON" || echo "✗ Invalid JSON"
