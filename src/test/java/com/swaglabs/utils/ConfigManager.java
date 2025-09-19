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
    private static ConfigManager instance;
    private Properties properties;

    private static final String CONFIG_FILE = "config.properties";

    private ConfigManager() {
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
        properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
            } else {
                logger.error("Configuration file {} not found", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", e.getMessage());
            throw new RuntimeException("Error loading configuration file", e);
        }
    }

    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    // Application Configuration
    public String getAppUrl() {
        return getProperty("app.url");
    }

    public long getTimeout() {
        return Long.parseLong(getProperty("app.timeout", "10000"));
    }

    // Browser Configuration
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public boolean isBrowserHeadless() {
        return Boolean.parseBoolean(getProperty("browser.headless", "false"));
    }

    public String getBrowserSize() {
        return getProperty("browser.size", "1920x1080");
    }

    // User Configuration
    public String getStandardUsername() {
        return getProperty("user.standard.username");
    }

    public String getStandardPassword() {
        return getProperty("user.standard.password");
    }

    public String getLockedUsername() {
        return getProperty("user.locked.username");
    }

    public String getLockedPassword() {
        return getProperty("user.locked.password");
    }

    public String getProblemUsername() {
        return getProperty("user.problem.username");
    }

    public String getProblemPassword() {
        return getProperty("user.problem.password");
    }

    // Test Data
    public String getTestFirstName() {
        return getProperty("test.firstName");
    }

    public String getTestLastName() {
        return getProperty("test.lastName");
    }

    public String getTestPostalCode() {
        return getProperty("test.postalCode");
    }

    // Screenshot Configuration
    public boolean isScreenshotsEnabled() {
        return Boolean.parseBoolean(getProperty("screenshots.enabled", "true"));
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
}