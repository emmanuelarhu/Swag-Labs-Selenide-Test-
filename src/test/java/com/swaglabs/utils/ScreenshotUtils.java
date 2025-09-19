package com.swaglabs.utils;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling screenshots
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
        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String fileName = String.format("%s_%s", testName, timestamp);

            File screenshot = new File(Selenide.screenshot(fileName));
            if (screenshot != null) {
                logger.info("Screenshot saved: {}", screenshot.getAbsolutePath());

                // Attach to Allure report
                attachScreenshotToAllure(screenshot);

                return screenshot.getAbsolutePath();
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
        try {
            String fileName = "screenshot_" + LocalDateTime.now().format(TIMESTAMP_FORMAT);
            return new File(Selenide.screenshot(fileName));
        } catch (Exception e) {
            logger.error("Failed to take screenshot", e);
            return null;
        }
    }

    /**
     * Attaches screenshot to Allure report
     * @param screenshotFile Screenshot file to attach
     */
    @Attachment(value = "Screenshot", type = "image/png")
    private static byte[] attachScreenshotToAllure(File screenshotFile) {
        try {
            if (screenshotFile != null && screenshotFile.exists()) {
                return Files.readAllBytes(screenshotFile.toPath());
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
        try {
            File screenshot = new File(Selenide.screenshot("allure_screenshot"));
            if (screenshot != null && screenshot.exists()) {
                return Files.readAllBytes(screenshot.toPath());
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
}