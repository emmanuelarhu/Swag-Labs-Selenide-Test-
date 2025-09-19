# Simple Dockerfile for SwagLabs Test Automation
FROM maven:3.9.6-eclipse-temurin-21-alpine

# Install browsers and dependencies
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    firefox \
    xvfb \
    && rm -rf /var/cache/apk/*

# Set environment variables
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_DRIVER=/usr/bin/chromedriver
ENV DISPLAY=:99

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml ./
COPY src/ ./src/

# Download dependencies
RUN mvn dependency:go-offline -B

# Create directories
RUN mkdir -p target/screenshots target/allure-results logs

# Create test runner script
RUN echo '#!/bin/sh' > run-tests.sh && \
    echo 'set -e' >> run-tests.sh && \
    echo 'Xvfb :99 -ac -screen 0 1920x1080x24 > /dev/null 2>&1 &' >> run-tests.sh && \
    echo 'sleep 3' >> run-tests.sh && \
    echo 'TEST_TYPE=${TEST_TYPE:-smoke}' >> run-tests.sh && \
    echo 'mvn clean test -Dtest=${TEST_TYPE^}Tests -Dbrowser=${BROWSER:-chrome} -Dheadless=true' >> run-tests.sh && \
    echo 'mvn allure:report || true' >> run-tests.sh && \
    chmod +x run-tests.sh

# Run tests
ENTRYPOINT ["./run-tests.sh"]
