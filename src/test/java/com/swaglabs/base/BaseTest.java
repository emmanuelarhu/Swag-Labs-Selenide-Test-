package com.swaglabs.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.swaglabs.utils.ConfigManager;
import com.swaglabs.utils.ScreenshotUtils;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.UUID;

/**
 * Base test class that provides common setup and teardown functionality
 * for all test classes in the automation framework.
 */
public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        logger.info("Starting test suite setup...");

        try {
            // Initialize configuration first
            config = ConfigManager.getInstance();
            config.validateConfiguration();
            logger.info("Configuration loaded and validated successfully");

            // Configure Selenide
            setupSelenideConfiguration();

            // Add Allure listener for better reporting
            SelenideLogger.addListener("AllureSelenide",
                    new AllureSelenide()
                            .screenshots(true)
                            .savePageSource(false)
                            .includeSelenideSteps(true));

            logger.info("Test suite setup completed successfully");
        } catch (Exception e) {
            logger.error("Error during suite setup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to setup test suite", e);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        logger.info("Starting class setup for: {}", this.getClass().getSimpleName());

        // Ensure config is available at class level
        if (config == null) {
            config = ConfigManager.getInstance();
        }

        logger.info("Class setup completed for: {}", this.getClass().getSimpleName());
    }

    @BeforeMethod(alwaysRun = true)
    public void testSetup() {
        logger.info("Setting up test method...");

        try {
            // Ensure config is available
            if (config == null) {
                config = ConfigManager.getInstance();
                logger.warn("Config was null, reinitializing...");
            }

            // Close any existing WebDriver sessions
            if (WebDriverRunner.hasWebDriverStarted()) {
                try {
                    Selenide.closeWebDriver();
                    logger.info("Closed existing WebDriver session");
                } catch (Exception e) {
                    logger.warn("Error closing existing WebDriver: {}", e.getMessage());
                }
            }

            // Add a small delay to ensure cleanup is complete
            Thread.sleep(1000);

            // Configure Chrome options for Docker environment
            setupChromeOptionsForDocker();

            // Open application URL
            String appUrl = config.getAppUrl();
            logger.info("Opening application URL: {}", appUrl);
            Selenide.open(appUrl);
            logger.info("Successfully opened application URL");

        } catch (Exception e) {
            logger.error("Error during test setup: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to setup test", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        logger.info("Starting test teardown...");

        try {
            // Take screenshot on failure
            if (result.getStatus() == ITestResult.FAILURE) {
                String testMethodName = result.getMethod().getMethodName();
                logger.error("Test failed: {}", result.getThrowable().getMessage());

                // Take failure screenshot only if WebDriver is available
                if (WebDriverRunner.hasWebDriverStarted()) {
                    try {
                        ScreenshotUtils.takeScreenshot(testMethodName);
                    } catch (Exception e) {
                        logger.warn("Failed to take screenshot: {}", e.getMessage());
                    }
                }
            }

            // Clear browser data only if WebDriver is available
            if (WebDriverRunner.hasWebDriverStarted()) {
                try {
                    Selenide.clearBrowserCookies();
                    Selenide.clearBrowserLocalStorage();
                } catch (Exception e) {
                    logger.warn("Error clearing browser data: {}", e.getMessage());
                }
            }

            logger.info("Test teardown completed");

        } catch (Exception e) {
            logger.warn("Error during test teardown: {}", e.getMessage());
        }
    }

    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        logger.info("Starting class teardown for: {}", this.getClass().getSimpleName());

        try {
            // Close WebDriver for this class
            if (WebDriverRunner.hasWebDriverStarted()) {
                Selenide.closeWebDriver();
                logger.info("WebDriver closed for class: {}", this.getClass().getSimpleName());
            }
        } catch (Exception e) {
            logger.warn("Error during class teardown: {}", e.getMessage());
        }

        logger.info("Class teardown completed for: {}", this.getClass().getSimpleName());
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        logger.info("Starting test suite teardown...");

        try {
            // Close all browser windows
            Selenide.closeWebDriver();
            logger.info("WebDriver closed successfully");
        } catch (Exception e) {
            logger.warn("Error closing WebDriver: {}", e.getMessage());
        }

        logger.info("Test suite teardown completed");
    }

    /**
     * Configure Selenide settings based on configuration properties
     */
    private void setupSelenideConfiguration() {
        try {
            Configuration.browser = config.getBrowser();
            Configuration.headless = config.isBrowserHeadless();
            Configuration.browserSize = config.getBrowserSize();
            Configuration.timeout = config.getTimeout();
            Configuration.screenshots = config.isScreenshotsEnabled();
            Configuration.reportsFolder = config.getScreenshotsPath();

            // Additional Selenide configurations
            Configuration.clickViaJs = false;
            Configuration.fastSetValue = false;
            Configuration.savePageSource = false;
            Configuration.reopenBrowserOnFail = false;

            logger.info("Selenide configuration completed - Browser: {}, Headless: {}, Size: {}",
                    Configuration.browser, Configuration.headless, Configuration.browserSize);

        } catch (Exception e) {
            logger.error("Error configuring Selenide: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure Selenide", e);
        }
    }

    /**
     * Setup Chrome options specifically for Docker environment
     */
    private void setupChromeOptionsForDocker() {
        // Generate unique user data directory for each test session
        String uniqueUserDataDir = "/tmp/chrome-user-data-" + UUID.randomUUID().toString();

        // Set Chrome options via system properties
        System.setProperty("chromeoptions.args", String.join(",",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-web-security",
                "--allow-running-insecure-content",
                "--disable-background-timer-throttling",
                "--disable-backgrounding-occluded-windows",
                "--disable-renderer-backgrounding",
                "--disable-features=TranslateUI",
                "--disable-ipc-flooding-protection",
                "--user-data-dir=" + uniqueUserDataDir,
                "--remote-debugging-port=0"  // Use random port
        ));

        logger.info("Chrome options configured for Docker with user data dir: {}", uniqueUserDataDir);
    }

    /**
     * Get the current test method name for logging purposes
     */
    protected String getCurrentTestMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Get configuration manager instance
     */
    protected ConfigManager getConfig() {
        if (config == null) {
            config = ConfigManager.getInstance();
        }
        return config;
    }

    /**
     * Safe method to check if WebDriver is available
     */
    protected boolean isWebDriverAvailable() {
        return WebDriverRunner.hasWebDriverStarted();
    }

    /**
     * Safe method to refresh page
     */
    protected void refreshPage() {
        if (isWebDriverAvailable()) {
            try {
                Selenide.refresh();
                logger.info("Page refreshed successfully");
            } catch (Exception e) {
                logger.warn("Error refreshing page: {}", e.getMessage());
            }
        }
    }

    /**
     * Safe method to navigate to URL
     */
    protected void navigateToUrl(String url) {
        try {
            Selenide.open(url);
            logger.info("Navigated to URL: {}", url);
        } catch (Exception e) {
            logger.error("Error navigating to URL {}: {}", url, e.getMessage());
            throw new RuntimeException("Failed to navigate to URL: " + url, e);
        }
    }
}