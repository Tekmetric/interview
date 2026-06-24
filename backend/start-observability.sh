#!/bin/bash

echo "Starting Observability Stack..."
echo ""

# Check if Docker is running
echo "Checking Docker..."
if ! docker ps > /dev/null 2>&1; then
    echo ""
    echo "ERROR: Docker is not running!"
    echo ""
    echo "Please start Docker Desktop and try again."
    echo "After Docker is running, verify with: docker ps"
    echo ""
    exit 1
fi
echo "✓ Docker is running"
echo ""

echo "Step 1: Compiling test classes..."
mvn test-compile -q

if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed"
    exit 1
fi

echo "Step 2: Starting Prometheus and Grafana containers..."
echo ""
mvn exec:java -Dexec.mainClass="com.interview.ObservabilityStack" -Dexec.classpathScope=test
