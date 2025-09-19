FROM maven:3.9.4-eclipse-temurin-21

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src src/

# Install Chrome
RUN apt-get update && \
    apt-get install -y wget gnupg2 && \
    wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    apt-get clean

# Run tests
CMD ["mvn", "clean", "test"]
