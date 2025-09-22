#!/bin/bash

# Docker Testing Script for SwagLabs Test Automation
# This script tests the Docker container locally before CI/CD

echo "🧪 Testing SwagLabs Docker Container"
echo "===================================="

# Clean up any existing containers
echo "🧹 Cleaning up existing containers..."
docker stop swaglabs-test 2>/dev/null || true
docker rm swaglabs-test 2>/dev/null || true

# Build the Docker image
echo "🔨 Building Docker image..."
if docker build -t swaglabs-tests .; then
    echo "✅ Docker image built successfully"
else
    echo "❌ Docker build failed"
    exit 1
fi

# Create local directories for testing
echo "📁 Creating local test directories..."
mkdir -p ./target/allure-results
mkdir -p ./target/allure-report
mkdir -p ./target/screenshots
mkdir -p ./logs
chmod -R 777 ./target ./logs

# Test 1: Smoke tests
echo ""
echo "🔥 Test 1: Running Smoke Tests..."
echo "================================"

docker run --rm \
    --name swaglabs-test-smoke \
    -e TEST_TYPE=smoke \
    -e BROWSER=chrome \
    -v "$(pwd | tr '[:upper:]' '[:lower:]' | sed 's/ideaprojects/ideaprojects/')/target":/app/target \
    --security-opt seccomp=unconfined \
    --shm-size=2g \
    swaglabs-tests

SMOKE_EXIT_CODE=$?

# Check results
echo ""
echo "📊 Checking Smoke Test Results..."
if [ -d "./target/allure-results" ] && [ "$(ls -A ./target/allure-results 2>/dev/null)" ]; then
    echo "✅ Allure results found:"
    ls -la ./target/allure-results/
else
    echo "⚠️ No Allure results found"
fi

if [ -d "./target/allure-report" ] && [ "$(ls -A ./target/allure-report 2>/dev/null)" ]; then
    echo "✅ Allure report found:"
    ls -la ./target/allure-report/ | head -5
else
    echo "⚠️ No Allure report found"
fi

# Test 2: Regression tests (optional)
read -p "🔄 Run Regression Tests? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🔄 Test 2: Running Regression Tests..."
    echo "====================================="

    # Clear previous results
    rm -rf ./target/allure-results/*
    rm -rf ./target/allure-report/*

    docker run --rm \
        --name swaglabs-test-regression \
        -e TEST_TYPE=regression \
        -e BROWSER=chrome \
        -v $(pwd)/target:/app/target \
        -v $(pwd)/logs:/app/logs \
        --security-opt seccomp=unconfined \
        --shm-size=2g \
        swaglabs-tests

    REGRESSION_EXIT_CODE=$?

    echo ""
    echo "📊 Checking Regression Test Results..."
    if [ -d "./target/allure-results" ] && [ "$(ls -A ./target/allure-results 2>/dev/null)" ]; then
        echo "✅ Regression results found:"
        ls -la ./target/allure-results/
    else
        echo "⚠️ No regression results found"
    fi
fi

# Test 3: Interactive container (for debugging)
read -p "🐚 Open interactive container for debugging? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🐚 Opening interactive container..."
    echo "=================================="
    echo "You can now run commands inside the container:"
    echo "  - mvn test -Dtest=SmokeTests"
    echo "  - ls -la target/"
    echo "  - exit (to leave)"
    echo ""

    docker run -it --rm \
        --name swaglabs-test-interactive \
        -v $(pwd)/target:/app/target \
        -v $(pwd)/logs:/app/logs \
        --security-opt seccomp=unconfined \
        --shm-size=2g \
        --run-tests /bin/bash \
        swaglabs-tests
fi

# Summary
echo ""
echo "📋 Test Summary"
echo "==============="
echo "Smoke Test Exit Code: $SMOKE_EXIT_CODE"
if [ -n "$REGRESSION_EXIT_CODE" ]; then
    echo "Regression Test Exit Code: $REGRESSION_EXIT_CODE"
fi

echo ""
echo "📁 Generated Files:"
find ./target -type f -name "*.json" -o -name "*.html" -o -name "*.xml" | head -10

echo ""
if [ -f "./target/allure-report/index.html" ]; then
    echo "✅ Local report available at: file://$(pwd)/target/allure-report/index.html"
    echo "🌐 To serve locally: python3 -m http.server 8080 --directory target/allure-report"
else
    echo "❌ No local report generated"
fi

echo ""
echo "🎯 Next Steps:"
echo "1. If tests pass locally, commit and push to trigger GitHub Actions"
echo "2. Check GitHub Actions workflow in the Actions tab"
echo "3. View deployed report at: https://yourusername.github.io/yourrepo/"
echo ""
echo "🏁 Docker testing completed!"