package com.swaglabs.utils;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling screenshots with improved error handling
 */
public class ScreenshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    /**
     * Takes a screenshot and saves it with the given test name
     * @param testName Name of the test for which screenshot is taken
     * @return Path to the screenshot file
     */
    public static String takeScreenshot(String testName) {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            logger.warn("WebDriver not started, cannot take screenshot for test: {}", testName);
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = String.format("%s_%s", cleanFileName(testName), timestamp);

            File screenshot = new File(Selenide.screenshot(fileName));
            if (screenshot != null && screenshot.exists()) {
                logger.info("Screenshot saved: {}", screenshot.getAbsolutePath());

                // Attach to Allure report
                attachScreenshotToAllure(screenshot);

                return screenshot.getAbsolutePath();
            } else {
                logger.warn("Screenshot file not created for test: {}", testName);
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot for test: {}", testName, e);
        }
        return null;
    }

    /**
     * Takes a screenshot and returns the file
     * @return Screenshot file
     */
    public static File takeScreenshotAsFile() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            logger.warn("WebDriver not started, cannot take screenshot");
            return null;
        }

        try {
            String fileName = "screenshot_" + LocalDateTime.now().format(TIMESTAMP_FORMAT);
            File screenshot = new File(Selenide.screenshot(fileName));
            if (screenshot != null && screenshot.exists()) {
                logger.info("Screenshot file created: {}", screenshot.getAbsolutePath());
                return screenshot;
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot as file", e);
        }
        return null;
    }

    /**
     * Attaches screenshot to Allure report
     * @param screenshotFile Screenshot file to attach
     */
    @Attachment(value = "Screenshot", type = "image/png")
    private static byte[] attachScreenshotToAllure(File screenshotFile) {
        try {
            if (screenshotFile != null && screenshotFile.exists()) {
                byte[] screenshotBytes = Files.readAllBytes(screenshotFile.toPath());
                logger.debug("Screenshot attached to Allure report: {} bytes", screenshotBytes.length);
                return screenshotBytes;
            }
        } catch (IOException e) {
            logger.error("Failed to attach screenshot to Allure report", e);
        }
        return new byte[0];
    }

    /**
     * Attaches screenshot to Allure report directly
     * @return Screenshot bytes
     */
    @Attachment(value = "Page Screenshot", type = "image/png")
    public static byte[] attachScreenshotToAllure() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            logger.warn("WebDriver not started, cannot attach screenshot to Allure");
            return new byte[0];
        }

        try {
            File screenshot = new File(Selenide.screenshot("allure_screenshot"));
            if (screenshot != null && screenshot.exists()) {
                byte[] screenshotBytes = Files.readAllBytes(screenshot.toPath());
                logger.info("Screenshot attached to Allure report");
                return screenshotBytes;
            }
        } catch (Exception e) {
            logger.error("Failed to take and attach screenshot to Allure", e);
        }
        return new byte[0];
    }

    /**
     * Takes screenshot on test failure
     * @param testMethodName Name of the failed test method
     */
    @Attachment(value = "Test Failure Screenshot", type = "image/png")
    public static byte[] takeFailureScreenshot(String testMethodName) {
        logger.info("Taking failure screenshot for test: {}", testMethodName);
        return attachScreenshotToAllure();
    }

    /**
     * Safe method to take screenshot with error handling
     * @param testName Name of the test
     * @return Screenshot file path or null if failed
     */
    public static String safeScreenshot(String testName) {
        try {
            return takeScreenshot(testName);
        } catch (Exception e) {
            logger.error("Safe screenshot failed for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Check if screenshots can be taken (WebDriver available)
     * @return true if WebDriver is available for screenshots
     */
    public static boolean canTakeScreenshot() {
        return WebDriverRunner.hasWebDriverStarted();
    }

    /**
     * Clean file name for cross-platform compatibility
     * @param fileName original file name
     * @return cleaned file name
     */
    private static String cleanFileName(String fileName) {
        if (fileName == null) {
            return "unknown_test";
        }
        // Remove or replace invalid characters for file names
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Create screenshots directory if it doesn't exist
     * @param path directory path
     */
    public static void createScreenshotsDirectory(String path) {
        try {
            File directory = new File(path);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    logger.info("Screenshots directory created: {}", path);
                } else {
                    logger.warn("Failed to create screenshots directory: {}", path);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating screenshots directory: {}", path, e);
        }
    }
}