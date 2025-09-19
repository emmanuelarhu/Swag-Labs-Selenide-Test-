#!/bin/bash

# SwagLabs Test Runner Script
# Usage: ./run-tests.sh [test-type] [browser] [headless]

# Default values
TEST_TYPE=${1:-"smoke"}
BROWSER=${2:-"chrome"}
HEADLESS=${3:-"false"}

echo "🚀 Starting SwagLabs Test Execution"
echo "=================================="
echo "Test Type: $TEST_TYPE"
echo "Browser: $BROWSER"
echo "Headless: $HEADLESS"
echo "=================================="

# Clean previous results
echo "🧹 Cleaning previous test results..."
mvn clean -q

# Validate configuration
echo "🔍 Validating configuration..."
if [ ! -f "src/test/resources/config.properties" ]; then
    echo "❌ ERROR: config.properties not found!"
    exit 1
fi

# Check if TestNG XML exists
TESTNG_FILE="src/test/resources/testng.xml"
if [ ! -f "$TESTNG_FILE" ]; then
    echo "❌ ERROR: testng.xml not found!"
    exit 1
fi

echo "✅ Configuration validated"

# Set Maven options for better performance
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"

# Execute tests based on type
case $TEST_TYPE in
    "smoke")
        echo "🔥 Running Smoke Tests..."
        mvn test \
            -Dtest=SmokeTests \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS \
            -DsuiteXmlFile=src/test/resources/smoke-suite.xml
        ;;
    "regression")
        echo "🔄 Running Regression Tests..."
        mvn test \
            -Dtest=RegressionTests \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS \
            -DsuiteXmlFile=src/test/resources/regression-suite.xml
        ;;
    "login")
        echo "🔐 Running Login Tests..."
        mvn test \
            -Dtest=LoginTest \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS
        ;;
    "cart")
        echo "🛒 Running Cart Tests..."
        mvn test \
            -Dtest=CartTest \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS
        ;;
    "checkout")
        echo "💳 Running Checkout Tests..."
        mvn test \
            -Dtest=CheckoutTest \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS
        ;;
    "all")
        echo "🎯 Running All Tests..."
        mvn test \
            -Dbrowser=$BROWSER \
            -Dheadless=$HEADLESS \
            -DsuiteXmlFile=src/test/resources/testng.xml
        ;;
    *)
        echo "❌ Unknown test type: $TEST_TYPE"
        echo "Valid options: smoke, regression, login, cart, checkout, all"
        exit 1
        ;;
esac

# Check test execution result
TEST_RESULT=$?

if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ Tests completed successfully!"
else
    echo "❌ Tests failed with exit code: $TEST_RESULT"
fi

# Generate Allure report if tests ran
if [ -d "target/allure-results" ] && [ "$(ls -A target/allure-results)" ]; then
    echo "📊 Generating Allure report..."
    mvn allure:report -q

    if [ $? -eq 0 ]; then
        echo "📈 Allure report generated successfully!"
        echo "🌐 To view report, run: mvn allure:serve"
    else
        echo "⚠️  Failed to generate Allure report"
    fi
else
    echo "⚠️  No test results found for report generation"
fi

echo "=================================="
echo "🏁 Test execution completed"
echo "Exit code: $TEST_RESULT"

exit $TEST_RESULT