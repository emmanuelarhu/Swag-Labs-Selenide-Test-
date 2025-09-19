package com.swaglabs.utils;

import com.codeborne.selenide.Selenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling popups and overlays
 */
public class PopupHandler {
    private static final Logger logger = LoggerFactory.getLogger(PopupHandler.class);

    /**
     * Handles any popup dialogs that might appear during test execution
     */
    public static void handleAnyPopups() {
        try {
            // Handle JavaScript alerts
            if (AlertHandler.isAlertPresent()) {
                String alertText = AlertHandler.handleAlertAndGetText();
                logger.info("Handled JavaScript alert with text: '{}'", alertText);
            }

            // Additional popup handling can be added here
            // For example: cookie banners, notification popups, etc.

        } catch (Exception e) {
            logger.debug("No popups to handle or error handling popups: {}", e.getMessage());
        }
    }

    /**
     * Closes any modal dialogs that might be present
     */
    public static void closeModalDialogs() {
        try {
            // Look for common modal close buttons
            if (Selenide.$(".modal-close").exists()) {
                Selenide.$(".modal-close").click();
                logger.info("Closed modal dialog using .modal-close");
            } else if (Selenide.$("[data-dismiss='modal']").exists()) {
                Selenide.$("[data-dismiss='modal']").click();
                logger.info("Closed modal dialog using data-dismiss attribute");
            } else if (Selenide.$(".close").exists()) {
                Selenide.$(".close").click();
                logger.info("Closed modal dialog using .close");
            }
        } catch (Exception e) {
            logger.debug("No modal dialogs to close or error closing modals: {}", e.getMessage());
        }
    }

    /**
     * Dismisses cookie consent banners
     */
    public static void dismissCookieBanner() {
        try {
            // Common cookie banner selectors
            if (Selenide.$("#cookie-accept").exists()) {
                Selenide.$("#cookie-accept").click();
                logger.info("Accepted cookies using #cookie-accept");
            } else if (Selenide.$(".cookie-accept").exists()) {
                Selenide.$(".cookie-accept").click();
                logger.info("Accepted cookies using .cookie-accept");
            } else if (Selenide.$("[data-testid='cookie-accept']").exists()) {
                Selenide.$("[data-testid='cookie-accept']").click();
                logger.info("Accepted cookies using data-testid");
            }
        } catch (Exception e) {
            logger.debug("No cookie banner to dismiss or error dismissing: {}", e.getMessage());
        }
    }

    /**
     * Handles all common types of popups
     */
    public static void handleAllPopups() {
        handleAnyPopups();
        closeModalDialogs();
        dismissCookieBanner();
    }
}