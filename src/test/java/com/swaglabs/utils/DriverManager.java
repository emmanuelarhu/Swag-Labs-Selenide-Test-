package com.swaglabs.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebDriver Manager for handling browser instances
 */
public class DriverManager {
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Creates and returns WebDriver instance based on browser type
     */
    public static WebDriver getDriver(String browser, boolean headless) {
        WebDriver driver = null;

        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();

                    if (headless) {
                        chromeOptions.addArguments("--headless");
                    }

                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.addArguments("--disable-extensions");
                    chromeOptions.addArguments("--disable-web-security");
                    chromeOptions.addArguments("--allow-running-insecure-content");

                    driver = new ChromeDriver(chromeOptions);
                    logger.info("Chrome driver initialized successfully");
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();

                    if (headless) {
                        firefoxOptions.addArguments("--headless");
                    }

                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");

                    driver = new FirefoxDriver(firefoxOptions);
                    logger.info("Firefox driver initialized successfully");
                    break;

                default:
                    logger.error("Browser '{}' is not supported. Using Chrome as default.", browser);
                    return getDriver("chrome", headless);
            }

            if (driver != null) {
                driver.manage().window().maximize();
                driverThreadLocal.set(driver);
            }

        } catch (Exception e) {
            logger.error("Failed to initialize {} driver: {}", browser, e.getMessage());
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }

        return driver;
    }

    /**
     * Gets the current WebDriver instance for the thread
     */
    public static WebDriver getCurrentDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Closes and quits the current WebDriver instance
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error closing WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Closes the current WebDriver instance without quitting
     */
    public static void closeDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.close();
                logger.info("WebDriver window closed successfully");
            } catch (Exception e) {
                logger.error("Error closing WebDriver window: {}", e.getMessage());
            }
        }
    }
}