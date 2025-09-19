package com.swaglabs.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.swaglabs.utils.ConfigManager;
import com.swaglabs.utils.ScreenshotUtils;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base test class that provides common setup and teardown functionality
 * for all test classes in the automation framework.
 */
public abstract class BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        logger.info("Starting test suite setup...");

        // Load configuration
        config = ConfigManager.getInstance();

        // Configure Selenide
        setupSelenideConfiguration();

        // Add Allure listener for better reporting
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false)
                        .includeSelenideSteps(true));

        logger.info("Test suite setup completed");
    }

    @BeforeMethod(alwaysRun = true)
    public void testSetup() {
        logger.info("Setting up test method...");

        // Open application URL
        Selenide.open(config.getAppUrl());
        logger.info("Opened application URL: {}", config.getAppUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        logger.info("Tearing down test method...");

        // Take screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            String testMethodName = result.getMethod().getMethodName();
            logger.error("Test failed: {}", result.getThrowable().getMessage());
//
//            // Take failure screenshot
//            ScreenshotUtils.takeScreenshot(testMethodName);

            // Also save screenshot to file system
            ScreenshotUtils.takeScreenshot(testMethodName);
        }

        // Clear browser data
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        logger.info("Test method teardown completed");
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        logger.info("Starting test suite teardown...");

        // Close all browser windows
        Selenide.closeWebDriver();

        logger.info("Test suite teardown completed");
    }

    /**
     * Configure Selenide settings based on configuration properties
     */
    private void setupSelenideConfiguration() {
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
    }

    /**
     * Get the current test method name for logging purposes
     */
    protected String getCurrentTestMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}