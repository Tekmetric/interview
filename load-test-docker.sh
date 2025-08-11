#!/bin/bash

# Load Test Docker Script
# This script runs load tests against the Docker Compose environment

set -e

echo "🚀 Starting load test with Docker environment..."

# Build the load test container
echo "📦 Building load test container..."
docker build -t inventory-load-test -f artillery.Dockerfile .

# Check if inventory app is running
echo "🔍 Checking if inventory application is running..."
if ! docker ps | grep -q inventory-app; then
    echo "❌ Inventory app container is not running. Please start with 'docker-compose up -d'"
    exit 1
fi

# Wait for the application to be ready
echo "⏳ Waiting for application to be ready..."
timeout=60
counter=0

while ! curl -s http://localhost:8080/actuator/health > /dev/null; do
    sleep 2
    counter=$((counter + 2))
    if [ $counter -gt $timeout ]; then
        echo "❌ Timeout waiting for application to be ready"
        exit 1
    fi
done

echo "✅ Application is ready!"

# Create reports directory if it doesn't exist
mkdir -p artillery-reports

# Run the load test
echo "🔥 Running load test..."
docker run --rm \
    --network container:inventory-app \
    -v "$(pwd)/artillery-reports:/app/artillery-reports" \
    inventory-load-test \
    artillery run docker-test.yml --output /app/artillery-reports/load-report.json

echo "📊 Load test completed! Results saved to artillery-reports/"

# Quick analysis
if [ -f "artillery-reports/load-report.json" ]; then
    echo "📈 Quick test summary:"
    echo "   - Check artillery-reports/load-report.json for detailed results"
    echo "   - Open with: artillery report artillery-reports/load-report.json"
fi