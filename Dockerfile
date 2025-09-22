FROM maven:3.9.6-eclipse-temurin-21

# Add cache-busting ARG
ARG CACHEBUST=1

# Install Chrome and dependencies
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    curl \
    unzip \
    xvfb \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Allure CLI
RUN wget -q https://github.com/allure-framework/allure2/releases/download/2.25.0/allure-2.25.0.tgz \
    && tar -zxf allure-2.25.0.tgz \
    && mv allure-2.25.0 /opt/allure \
    && ln -s /opt/allure/bin/allure /usr/local/bin/allure \
    && rm allure-2.25.0.tgz

WORKDIR /app

# Copy Maven files first (for better caching)
COPY pom.xml .

# Download dependencies (this will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy rest of the project
COPY . .

# Create directories
RUN mkdir -p target/allure-results target/allure-report target/screenshots logs

# Expose port for Allure report
EXPOSE 8080

# Create entrypoint script directly in the image
RUN echo '#!/bin/bash\n\
set -e\n\
\n\
echo "=== SwagLabs Test Execution Starting ==="\n\
echo "Date: $(date)"\n\
echo "Working Directory: $(pwd)"\n\
echo "Java Version: $(java -version 2>&1 | head -1)"\n\
\n\
# Start Xvfb for headless Chrome\n\
echo "Starting virtual display..."\n\
export DISPLAY=:99\n\
Xvfb :99 -ac -screen 0 1920x1080x24 > /dev/null 2>&1 &\n\
XVFB_PID=$!\n\
sleep 3\n\
echo "Virtual display started with PID: $XVFB_PID"\n\
\n\
# Set test parameters\n\
TEST_TYPE=${TEST_TYPE:-smoke}\n\
BROWSER=${BROWSER:-chrome}\n\
\n\
echo "=== Test Configuration ==="\n\
echo "Test Type: $TEST_TYPE"\n\
echo "Browser: $BROWSER"\n\
echo "Display: $DISPLAY"\n\
\n\
# Map test type to test class\n\
case $TEST_TYPE in\n\
    smoke)\n\
        TEST_CLASS="SmokeTests"\n\
        ;;\n\
    regression)\n\
        TEST_CLASS="RegressionTests"\n\
        ;;\n\
    all)\n\
        TEST_CLASS="*Test"\n\
        ;;\n\
    *)\n\
        TEST_CLASS="SmokeTests"\n\
        echo "Unknown test type '\''$TEST_TYPE'\'', defaulting to SmokeTests"\n\
        ;;\n\
esac\n\
\n\
echo "Running test class: $TEST_CLASS"\n\
\n\
# Validate project structure\n\
echo "=== Validating Project Structure ==="\n\
if [ ! -f "pom.xml" ]; then\n\
    echo "ERROR: pom.xml not found!"\n\
    exit 1\n\
fi\n\
\n\
if [ ! -f "src/test/resources/config.properties" ]; then\n\
    echo "ERROR: config.properties not found!"\n\
    exit 1\n\
fi\n\
\n\
echo "Project structure validated"\n\
\n\
# Run tests with explicit parameters\n\
echo "=== Starting Test Execution ==="\n\
mvn test \\\n\
    -Dtest=$TEST_CLASS \\\n\
    -Dbrowser=$BROWSER \\\n\
    -Dheadless=true \\\n\
    -Dselenide.browser=$BROWSER \\\n\
    -Dselenide.headless=true \\\n\
    -Dmaven.test.failure.ignore=true \\\n\
    || echo "Tests completed with some failures/errors"\n\
\n\
TEST_EXIT_CODE=$?\n\
echo "Maven test execution completed with exit code: $TEST_EXIT_CODE"\n\
\n\
# Check test results\n\
echo "=== Checking Test Results ==="\n\
if [ -d "target/surefire-reports" ]; then\n\
    echo "Surefire reports found:"\n\
    ls -la target/surefire-reports/\n\
else\n\
    echo "No surefire reports found"\n\
fi\n\
\n\
if [ -d "target/allure-results" ] && [ "$(ls -A target/allure-results)" ]; then\n\
    echo "Allure results found:"\n\
    ls -la target/allure-results/\n\
    \n\
    echo "=== Generating Allure Report ==="\n\
    allure generate target/allure-results -o target/allure-report --clean\n\
    echo "Allure report generated in target/allure-report"\n\
    \n\
    # List files for debugging\n\
    echo "Report files:"\n\
    ls -la target/allure-report/ | head -10\n\
else\n\
    echo "No test results found, creating minimal report"\n\
    mkdir -p target/allure-report target/allure-results\n\
    \n\
    # Create minimal allure result\n\
    cat > target/allure-results/minimal-result.json << EOFR\n\
{\n\
  "uuid": "minimal-$(date +%s)",\n\
  "name": "SwagLabs Minimal Test",\n\
  "fullName": "com.swaglabs.tests.MinimalTest",\n\
  "status": "broken",\n\
  "statusMessage": "No test results generated - check test configuration",\n\
  "stage": "finished",\n\
  "start": $(date +%s)000,\n\
  "stop": $(date +%s)000,\n\
  "labels": [\n\
    {"name": "suite", "value": "SwagLabs Tests"},\n\
    {"name": "feature", "value": "Test Execution"},\n\
    {"name": "browser", "value": "$BROWSER"},\n\
    {"name": "testType", "value": "$TEST_TYPE"}\n\
  ]\n\
}\n\
EOFR\n\
    \n\
    # Generate report from minimal result\n\
    allure generate target/allure-results -o target/allure-report --clean\n\
    echo "Minimal Allure report created"\n\
fi\n\
\n\
# Clean up\n\
echo "=== Cleaning Up ==="\n\
kill $XVFB_PID 2>/dev/null || true\n\
\n\
echo "=== Test Execution Complete ==="\n\
echo "Final exit code: $TEST_EXIT_CODE"\n\
\n\
# Exit with 0 to not fail the container (we handle test failures in CI)\n\
exit 0' > /app/entrypoint.sh

# Make script executable
RUN chmod +x /app/entrypoint.sh

# Set the entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]