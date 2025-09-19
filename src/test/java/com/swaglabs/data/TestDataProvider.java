package com.swaglabs.data;

import org.testng.annotations.DataProvider;

/**
 * Test Data Provider class containing all test data for parameterized tests
 */
public class TestDataProvider {

    /**
     * Login test data provider with various user credentials
     */
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][]{
                {"standard_user", "secret_sauce", "valid", true, "Standard User"},
                {"locked_out_user", "secret_sauce", "locked", false, "Locked User"},
                {"problem_user", "secret_sauce", "valid", true, "Problem User"},
                {"performance_glitch_user", "secret_sauce", "valid", true, "Performance Glitch User"},
                {"error_user", "secret_sauce", "valid", true, "Error User"},
                {"visual_user", "secret_sauce", "valid", true, "Visual User"}
        };
    }

    /**
     * Invalid login credentials data provider
     */
    @DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        return new Object[][]{
                {"invalid_user", "secret_sauce", "Username and password do not match any user in this service"},
                {"standard_user", "invalid_password", "Username and password do not match any user in this service"},
                {"", "secret_sauce", "Username is required"},
                {"standard_user", "", "Password is required"},
                {"", "", "Username is required"}
        };
    }

    /**
     * Product data provider for testing different products
     */
    @DataProvider(name = "productData")
    public Object[][] getProductData() {
        return new Object[][]{
                {"Sauce Labs Backpack", "$29.99", "carry.allTheThings() with the sleek, streamlined Sly Pack"},
                {"Sauce Labs Bike Light", "$9.99", "A red light isn't the desired state in testing"},
                {"Sauce Labs Bolt T-Shirt", "$15.99", "Get your testing superhero on with the Sauce Labs bolt T-shirt"},
                {"Sauce Labs Fleece Jacket", "$49.99", "It's not every day that you come across a midweight quarter-zip fleece jacket"},
                {"Sauce Labs Onesie", "$7.99", "Rib snap infant onesie for the junior automation engineer"},
                {"Test.allTheThings() T-Shirt (Red)", "$15.99", "This classic Sauce Labs t-shirt is perfect to wear when cozying"}
        };
    }

    /**
     * Checkout information data provider
     */
    @DataProvider(name = "checkoutData")
    public Object[][] getCheckoutData() {
        return new Object[][]{
                {"Emmanuel", "Arhu", "233", "Ghana"},
                {"John", "Doe", "12345", "USA"},
                {"Jane", "Smith", "SW1A 1AA", "UK"},
                {"Ahmed", "Hassan", "10001", "Egypt"},
                {"Maria", "Garcia", "28001", "Spain"}
        };
    }

    /**
     * Invalid checkout data provider for validation testing
     */
    @DataProvider(name = "invalidCheckoutData")
    public Object[][] getInvalidCheckoutData() {
        return new Object[][]{
                {"", "Doe", "12345", "Error: First Name is required"},
                {"John", "", "12345", "Error: Last Name is required"},
                {"John", "Doe", "", "Error: Postal Code is required"},
                {"", "", "", "Error: First Name is required"},
                {"", "", "12345", "Error: First Name is required"}
        };
    }

    /**
     * Product sorting data provider
     */
    @DataProvider(name = "sortingData")
    public Object[][] getSortingData() {
        return new Object[][]{
                {"az", "Name (A to Z)"},
                {"za", "Name (Z to A)"},
                {"lohi", "Price (low to high)"},
                {"hilo", "Price (high to low)"}
        };
    }

    /**
     * Multiple products for cart testing
     */
    @DataProvider(name = "multipleProductsData")
    public Object[][] getMultipleProductsData() {
        return new Object[][]{
                {new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light"}, 2},
                {new String[]{"Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket", "Sauce Labs Onesie"}, 3},
                {new String[]{"Test.allTheThings() T-Shirt (Red)"}, 1},
                {new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"}, 4}
        };
    }

    /**
     * Browser data provider for cross-browser testing
     */
    @DataProvider(name = "browserData")
    public Object[][] getBrowserData() {
        return new Object[][]{
                {"chrome"},
                {"firefox"}
        };
    }

    /**
     * Complete end-to-end test data combining user, products, and checkout info
     */
    @DataProvider(name = "e2eTestData")
    public Object[][] getE2ETestData() {
        return new Object[][]{
                {
                        "standard_user", "secret_sauce",
                        new String[]{"Sauce Labs Backpack", "Sauce Labs Bike Light"},
                        "Emmanuel", "Arhu", "233"
                },
                {
                        "standard_user", "secret_sauce",
                        new String[]{"Sauce Labs Bolt T-Shirt"},
                        "John", "Doe", "12345"
                },
                {
                        "performance_glitch_user", "secret_sauce",
                        new String[]{"Sauce Labs Fleece Jacket", "Sauce Labs Onesie"},
                        "Jane", "Smith", "SW1A 1AA"
                }
        };
    }

    /**
     * Error scenarios data provider
     */
    @DataProvider(name = "errorScenarios")
    public Object[][] getErrorScenarios() {
        return new Object[][]{
                {"network_failure", "Network connection issue"},
                {"server_error", "Internal server error"},
                {"timeout", "Request timeout"},
                {"invalid_response", "Invalid server response"}
        };
    }

    /**
     * Performance test data provider
     */
    @DataProvider(name = "performanceData")
    public Object[][] getPerformanceData() {
        return new Object[][]{
                {"standard_user", 5000}, // Expected max load time in ms
                {"performance_glitch_user", 10000}, // Slower user with higher threshold
                {"problem_user", 5000}
        };
    }
}