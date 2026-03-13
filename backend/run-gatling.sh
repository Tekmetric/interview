#!/bin/bash

# Gatling Load Test Execution Script
# This script starts the Spring Boot application, runs Gatling tests, and optionally stops the app

set -e

# Color codes for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
APP_PORT=8080
STARTUP_TIMEOUT=60
STOP_APP_AFTER_TEST=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --stop-after)
            STOP_APP_AFTER_TEST=true
            shift
            ;;
        --port)
            APP_PORT="$2"
            shift 2
            ;;
        --timeout)
            STARTUP_TIMEOUT="$2"
            shift 2
            ;;
        --help)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --stop-after    Stop the application after Gatling tests complete"
            echo "  --port PORT     Application port (default: 8080)"
            echo "  --timeout SEC   Startup timeout in seconds (default: 60)"
            echo "  --help          Show this help message"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}  Gatling Load Test Execution${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""

# Function to check if the application is running
check_app_running() {
    curl -s -o /dev/null -w "%{http_code}" http://localhost:${APP_PORT}/actuator/health || echo "000"
}

# Function to wait for the application to start
wait_for_app() {
    echo -e "${YELLOW}Waiting for application to start on port ${APP_PORT}...${NC}"
    local counter=0
    while [ $counter -lt $STARTUP_TIMEOUT ]; do
        local status=$(check_app_running)
        if [ "$status" = "200" ]; then
            echo -e "${GREEN}Application is ready!${NC}"
            return 0
        fi
        echo -n "."
        sleep 1
        ((counter++))
    done
    echo -e "\n${RED}Application failed to start within ${STARTUP_TIMEOUT} seconds${NC}"
    return 1
}

# Check if app is already running
APP_ALREADY_RUNNING=false
if [ "$(check_app_running)" = "200" ]; then
    echo -e "${YELLOW}Application is already running on port ${APP_PORT}${NC}"
    APP_ALREADY_RUNNING=true
else
    echo -e "${YELLOW}Starting Spring Boot application...${NC}"

    # Start the application in the background
    mvn spring-boot:run > app.log 2>&1 &
    APP_PID=$!
    echo "Application PID: $APP_PID"

    # Wait for the application to be ready
    if ! wait_for_app; then
        echo -e "${RED}Failed to start application. Check app.log for details.${NC}"
        kill $APP_PID 2>/dev/null || true
        exit 1
    fi
fi

echo ""
echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}  Running Gatling Simulation${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""

# Run Gatling tests
if mvn gatling:test; then
    echo ""
    echo -e "${GREEN}==================================================${NC}"
    echo -e "${GREEN}  Gatling Tests Completed Successfully!${NC}"
    echo -e "${GREEN}==================================================${NC}"
    echo ""

    # Find and display the location of the Gatling report
    LATEST_REPORT=$(find target/gatling -name "index.html" -type f -printf '%T@ %p\n' 2>/dev/null | sort -n | tail -1 | cut -f2- -d" ")
    if [ -n "$LATEST_REPORT" ]; then
        echo -e "${GREEN}Gatling report available at:${NC}"
        echo -e "${YELLOW}file://$(pwd)/${LATEST_REPORT}${NC}"
        echo ""
    fi

    TEST_EXIT_CODE=0
else
    echo ""
    echo -e "${RED}==================================================${NC}"
    echo -e "${RED}  Gatling Tests Failed${NC}"
    echo -e "${RED}==================================================${NC}"
    echo ""
    TEST_EXIT_CODE=1
fi

# Stop the application if requested and we started it
if [ "$STOP_APP_AFTER_TEST" = true ] && [ "$APP_ALREADY_RUNNING" = false ]; then
    echo -e "${YELLOW}Stopping application (PID: $APP_PID)...${NC}"
    kill $APP_PID 2>/dev/null || true
    wait $APP_PID 2>/dev/null || true
    echo -e "${GREEN}Application stopped${NC}"
elif [ "$APP_ALREADY_RUNNING" = false ]; then
    echo -e "${YELLOW}Application is still running (PID: $APP_PID)${NC}"
    echo -e "${YELLOW}You can view metrics at: http://localhost:${APP_PORT}/actuator/prometheus${NC}"
    echo -e "${YELLOW}Stop it manually with: kill $APP_PID${NC}"
fi

echo ""
echo -e "${GREEN}Done!${NC}"

exit $TEST_EXIT_CODE
