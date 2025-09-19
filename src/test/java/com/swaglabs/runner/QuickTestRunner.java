package com.swaglabs.runner;

import com.swaglabs.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick Test Runner for debugging and single test execution
 */
public class QuickTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(QuickTestRunner.class);

    public static void main(String[] args) {
        try {
            // Initialize configuration first
            logger.info("Initializing configuration...");
            ConfigManager config = ConfigManager.getInstance();
            config.validateConfiguration();
            logger.info("Configuration validated successfully");

            // Determine which tests to run
            String testType = System.getProperty("test.type", "smoke");

            logger.info("Running {} tests", testType);

            switch (testType.toLowerCase()) {
                case "smoke":
                    runSmokeTests();
                    break;
                case "login":
                    runLoginTests();
                    break;
                case "single":
                    runSingleTest();
                    break;
                default:
                    runSmokeTests();
            }

        } catch (Exception e) {
            logger.error("Failed to run tests: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void runSmokeTests() {
        logger.info("Running Smoke Tests via TestNG programmatically");

        TestNG testng = new TestNG();
        List<String> testClasses = new ArrayList<>();
        testClasses.add("com.swaglabs.tests.SmokeTests");

        testng.setTestClasses((Class[]) testClasses.toArray());
        testng.setVerbose(2);

        testng.run();

        if (testng.hasFailure()) {
            logger.error("Smoke tests failed");
            System.exit(1);
        } else {
            logger.info("Smoke tests completed successfully");
        }
    }

    private static void runLoginTests() {
        logger.info("Running Login Tests via TestNG programmatically");

        TestNG testng = new TestNG();
        List<String> testClasses = new ArrayList<>();
        testClasses.add("com.swaglabs.tests.LoginTest");

        testng.setTestClasses((Class[]) testClasses.toArray());
        testng.setVerbose(2);

        testng.run();

        if (testng.hasFailure()) {
            logger.error("Login tests failed");
            System.exit(1);
        } else {
            logger.info("Login tests completed successfully");
        }
    }

    private static void runSingleTest() {
        String testClass = System.getProperty("test.class", "com.swaglabs.tests.SmokeTests");
        String testMethod = System.getProperty("test.method", "testLogin");

        logger.info("Running single test: {}.{}", testClass, testMethod);

        TestNG testng = new TestNG();

        // Set up test classes
        List<String> testClasses = new ArrayList<>();
        testClasses.add(testClass);
        testng.setTestClasses((Class[]) testClasses.toArray());

        // Set specific method if provided
        if (testMethod != null && !testMethod.isEmpty()) {
            testng.setTestNames(List.of(testMethod));
        }

        testng.setVerbose(2);
        testng.run();

        if (testng.hasFailure()) {
            logger.error("Single test failed: {}.{}", testClass, testMethod);
            System.exit(1);
        } else {
            logger.info("Single test completed successfully: {}.{}", testClass, testMethod);
        }
    }

    /**
     * Validate environment before running tests
     */
    private static void validateEnvironment() {
        logger.info("Validating test environment...");

        try {
            // Check Java version
            String javaVersion = System.getProperty("java.version");
            logger.info("Java version: {}", javaVersion);

            // Check if config file exists
            ConfigManager config = ConfigManager.getInstance();
            config.validateConfiguration();

            // Check browser availability (basic check)
            String browser = config.getBrowser();
            logger.info("Configured browser: {}", browser);

            logger.info("Environment validation completed successfully");

        } catch (Exception e) {
            logger.error("Environment validation failed: {}", e.getMessage());
            throw new RuntimeException("Environment validation failed", e);
        }
    }

    /**
     * Print usage information
     */
    private static void printUsage() {
        System.out.println("QuickTestRunner Usage:");
        System.out.println("======================");
        System.out.println();
        System.out.println("Run smoke tests:");
        System.out.println("  java -Dtest.type=smoke -cp ... com.swaglabs.runner.QuickTestRunner");
        System.out.println();
        System.out.println("Run login tests:");
        System.out.println("  java -Dtest.type=login -cp ... com.swaglabs.runner.QuickTestRunner");
        System.out.println();
        System.out.println("Run single test:");
        System.out.println("  java -Dtest.type=single -Dtest.class=com.swaglabs.tests.LoginTest -Dtest.method=testLogin -cp ... com.swaglabs.runner.QuickTestRunner");
        System.out.println();
        System.out.println("Additional options:");
        System.out.println("  -Dbrowser=chrome|firefox");
        System.out.println("  -Dheadless=true|false");
        System.out.println();
    }
}