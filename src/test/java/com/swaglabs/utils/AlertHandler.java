package com.swaglabs.utils;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Utility class for handling JavaScript alerts, confirmations, and prompts
 */
public class AlertHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlertHandler.class);
    private static final int DEFAULT_TIMEOUT = 10;

    /**
     * Checks if an alert is present
     * @return true if alert is present, false otherwise
     */
    public static boolean isAlertPresent() {
        try {
            Selenide.switchTo().alert();
            logger.debug("Alert is present");
            return true;
        } catch (NoAlertPresentException e) {
            logger.debug("No alert present");
            return false;
        }
    }

    /**
     * Accepts the alert if present
     * @return true if alert was accepted, false if no alert was present
     */
    public static boolean acceptAlert() {
        try {
            if (isAlertPresent()) {
                Alert alert = Selenide.switchTo().alert();
                String alertText = alert.getText();
                alert.accept();
                logger.info("Alert accepted with text: '{}'", alertText);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error accepting alert: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Dismisses the alert if present
     * @return true if alert was dismissed, false if no alert was present
     */
    public static boolean dismissAlert() {
        try {
            if (isAlertPresent()) {
                Alert alert = Selenide.switchTo().alert();
                String alertText = alert.getText();
                alert.dismiss();
                logger.info("Alert dismissed with text: '{}'", alertText);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error dismissing alert: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Gets the text from the alert if present
     * @return alert text if present, null otherwise
     */
    public static String getAlertText() {
        try {
            if (isAlertPresent()) {
                Alert alert = Selenide.switchTo().alert();
                String alertText = alert.getText();
                logger.info("Alert text retrieved: '{}'", alertText);
                return alertText;
            }
        } catch (Exception e) {
            logger.error("Error getting alert text: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Sends text to a prompt dialog and accepts it
     * @param text text to send to the prompt
     * @return true if successful, false otherwise
     */
    public static boolean sendTextToPrompt(String text) {
        try {
            if (isAlertPresent()) {
                Alert alert = Selenide.switchTo().alert();
                alert.sendKeys(text);
                alert.accept();
                logger.info("Text '{}' sent to prompt and accepted", text);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error sending text to prompt: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Waits for an alert to be present and returns it
     * @param timeoutSeconds maximum time to wait in seconds
     * @return Alert object if present within timeout, null otherwise
     */
    public static Alert waitForAlert(int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(Selenide.webdriver().object(), Duration.ofSeconds(timeoutSeconds));
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            logger.info("Alert appeared within {} seconds", timeoutSeconds);
            return alert;
        } catch (Exception e) {
            logger.warn("No alert appeared within {} seconds", timeoutSeconds);
            return null;
        }
    }

    /**
     * Waits for an alert to be present with default timeout
     * @return Alert object if present within timeout, null otherwise
     */
    public static Alert waitForAlert() {
        return waitForAlert(DEFAULT_TIMEOUT);
    }

    /**
     * Handles alert by accepting it and returning its text
     * @return alert text if present and accepted, null otherwise
     */
    public static String handleAlertAndGetText() {
        try {
            if (isAlertPresent()) {
                Alert alert = Selenide.switchTo().alert();
                String alertText = alert.getText();
                alert.accept();
                logger.info("Alert handled and text retrieved: '{}'", alertText);
                return alertText;
            }
        } catch (Exception e) {
            logger.error("Error handling alert: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Waits for alert, gets its text, and accepts it
     * @param timeoutSeconds maximum time to wait
     * @return alert text if found and handled, null otherwise
     */
    public static String waitForAlertAndHandle(int timeoutSeconds) {
        try {
            Alert alert = waitForAlert(timeoutSeconds);
            if (alert != null) {
                String alertText = alert.getText();
                alert.accept();
                logger.info("Alert waited for, text retrieved and accepted: '{}'", alertText);
                return alertText;
            }
        } catch (Exception e) {
            logger.error("Error waiting for and handling alert: {}", e.getMessage());
        }
        return null;
    }
}