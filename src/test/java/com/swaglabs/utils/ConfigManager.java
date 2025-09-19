package com.swaglabs.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager to handle application properties
 */
public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static volatile ConfigManager instance;
    private final Properties properties;

    private static final String CONFIG_FILE = "config.properties";

    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration loaded successfully from {}", CONFIG_FILE);

                // Log loaded properties for debugging (without sensitive data)
                logger.debug("Loaded {} properties", properties.size());
                properties.stringPropertyNames().stream()
                        .filter(key -> !key.toLowerCase().contains("password"))
                        .forEach(key -> logger.debug("Property: {} = {}", key, properties.getProperty(key)));

            } else {
                logger.error("Configuration file {} not found in classpath", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", e.getMessage());
            throw new RuntimeException("Error loading configuration file", e);
        }
    }

    public String getProperty(String key) {
        // First check system properties (command line overrides)
        String value = System.getProperty(key);
        if (value != null) {
            logger.debug("Using system property for {}: {}", key, value);
            return value;
        }

        // Then check loaded properties file
        value = properties.getProperty(key);
        if (value != null) {
            logger.debug("Using config file property for {}: {}", key, value);
            return value;
        }

        logger.warn("Property '{}' not found in system properties or config file", key);
        return null;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            logger.debug("Using default value for {}: {}", key, defaultValue);
            return defaultValue;
        }
        return value;
    }

    // Application Configuration
    public String getAppUrl() {
        String url = getProperty("app.url");
        if (url == null) {
            logger.error("app.url not configured!");
            throw new RuntimeException("Application URL not configured. Please set app.url in config.properties");
        }
        return url;
    }

    public long getTimeout() {
        String timeout = getProperty("app.timeout", "10000");
        try {
            return Long.parseLong(timeout);
        } catch (NumberFormatException e) {
            logger.warn("Invalid timeout value '{}', using default 10000", timeout);
            return 10000;
        }
    }

    // Browser Configuration
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public boolean isBrowserHeadless() {
        String headless = getProperty("browser.headless", "false");
        return Boolean.parseBoolean(headless);
    }

    public String getBrowserSize() {
        return getProperty("browser.size", "1920x1080");
    }

    // User Configuration
    public String getStandardUsername() {
        String username = getProperty("user.standard.username");
        if (username == null) {
            logger.error("user.standard.username not configured!");
            throw new RuntimeException("Standard username not configured");
        }
        return username;
    }

    public String getStandardPassword() {
        String password = getProperty("user.standard.password");
        if (password == null) {
            logger.error("user.standard.password not configured!");
            throw new RuntimeException("Standard password not configured");
        }
        return password;
    }

    public String getLockedUsername() {
        return getProperty("user.locked.username", "locked_out_user");
    }

    public String getLockedPassword() {
        return getProperty("user.locked.password", "secret_sauce");
    }

    public String getProblemUsername() {
        return getProperty("user.problem.username", "problem_user");
    }

    public String getProblemPassword() {
        return getProperty("user.problem.password", "secret_sauce");
    }

    public String getPerformanceUsername() {
        return getProperty("user.performance.username", "performance_glitch_user");
    }

    public String getPerformancePassword() {
        return getProperty("user.performance.password", "secret_sauce");
    }

    // Test Data
    public String getTestFirstName() {
        return getProperty("test.firstName", "Test");
    }

    public String getTestLastName() {
        return getProperty("test.lastName", "User");
    }

    public String getTestPostalCode() {
        return getProperty("test.postalCode", "12345");
    }

    // Screenshot Configuration
    public boolean isScreenshotsEnabled() {
        String enabled = getProperty("screenshots.enabled", "true");
        return Boolean.parseBoolean(enabled);
    }

    public String getScreenshotsPath() {
        return getProperty("screenshots.path", "target/screenshots");
    }

    // Reports Configuration
    public String getReportsPath() {
        return getProperty("reports.path", "target/reports");
    }

    public String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "target/allure-results");
    }

    // Environment Configuration
    public String getEnvironment() {
        return getProperty("environment", "test");
    }

    /**
     * Validates that all required properties are present
     */
    public void validateConfiguration() {
        logger.info("Validating configuration...");

        try {
            getAppUrl(); // Will throw exception if missing
            getStandardUsername(); // Will throw exception if missing
            getStandardPassword(); // Will throw exception if missing

            logger.info("Configuration validation passed");
        } catch (Exception e) {
            logger.error("Configuration validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get all properties as a Properties object (for debugging)
     */
    public Properties getAllProperties() {
        return new Properties(properties);
    }

    /**
     * Check if a property exists
     */
    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }
}