#!/usr/bin/env bash
#
# colors.sh - Color definitions and functions for CLI output
#

# Check if terminal supports colors
function supports_colors() {
    if [[ -t 1 ]] && command -v tput >/dev/null 2>&1; then
        local colors
        colors=$(tput colors 2>/dev/null)
        if [[ -n "$colors" ]] && [[ "$colors" -ge 8 ]]; then
            return 0
        fi
    fi
    return 1
}

# Initialize color variables
if supports_colors && [[ "${NO_COLOR:-false}" != "true" ]]; then
    # ANSI color codes
    RED=$(tput setaf 1 2>/dev/null)
    GREEN=$(tput setaf 2 2>/dev/null)
    YELLOW=$(tput setaf 3 2>/dev/null)
    BLUE=$(tput setaf 4 2>/dev/null)
    MAGENTA=$(tput setaf 5 2>/dev/null)
    CYAN=$(tput setaf 6 2>/dev/null)
    WHITE=$(tput setaf 7 2>/dev/null)
    BOLD=$(tput bold 2>/dev/null)
    RESET=$(tput sgr0 2>/dev/null)
else
    # No colors
    RED=""
    GREEN=""
    YELLOW=""
    BLUE=""
    MAGENTA=""
    CYAN=""
    WHITE=""
    BOLD=""
    RESET=""
fi

# Colored output functions
function color_error() {
    echo "${RED}${1}${RESET}"
}

function color_success() {
    echo "${GREEN}${1}${RESET}"
}

function color_warning() {
    echo "${YELLOW}${1}${RESET}"
}

function color_info() {
    echo "${BLUE}${1}${RESET}"
}

function color_highlight() {
    echo "${CYAN}${1}${RESET}"
}

function color_bold() {
    echo "${BOLD}${1}${RESET}"
}
