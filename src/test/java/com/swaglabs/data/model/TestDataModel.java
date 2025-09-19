package com.swaglabs.data.model;

/**
 * Test data model classes for structured test data management
 */
public class TestDataModel {

    /**
     * User data model for login tests
     */
    public static class User {
        private String username;
        private String password;
        private String userType;
        private boolean shouldLoginSucceed;
        private String description;

        public User(String username, String password, String userType, boolean shouldLoginSucceed, String description) {
            this.username = username;
            this.password = password;
            this.userType = userType;
            this.shouldLoginSucceed = shouldLoginSucceed;
            this.description = description;
        }

        // Getters
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getUserType() { return userType; }
        public boolean shouldLoginSucceed() { return shouldLoginSucceed; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return String.format("User{username='%s', type='%s', description='%s'}",
                    username, userType, description);
        }
    }

    /**
     * Product data model
     */
    public static class Product {
        private String name;
        private String price;
        private String description;
        private String imageAlt;

        public Product(String name, String price, String description) {
            this.name = name;
            this.price = price;
            this.description = description;
            this.imageAlt = name.toLowerCase().replace(" ", "-") + ".jpg";
        }

        // Getters
        public String getName() { return name; }
        public String getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImageAlt() { return imageAlt; }

        @Override
        public String toString() {
            return String.format("Product{name='%s', price='%s'}", name, price);
        }
    }

    /**
     * Checkout information data model
     */
    public static class CheckoutInfo {
        private String firstName;
        private String lastName;
        private String postalCode;
        private String country;

        public CheckoutInfo(String firstName, String lastName, String postalCode, String country) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.postalCode = postalCode;
            this.country = country;
        }

        // Getters
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPostalCode() { return postalCode; }
        public String getCountry() { return country; }

        public String getFullName() {
            return firstName + " " + lastName;
        }

        @Override
        public String toString() {
            return String.format("CheckoutInfo{name='%s %s', postalCode='%s', country='%s'}",
                    firstName, lastName, postalCode, country);
        }
    }

    /**
     * Cart item data model
     */
    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        // Getters
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }

        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getTotalPrice() {
            // Extract numeric value from price string (e.g., "$29.99" -> 29.99)
            String priceStr = product.getPrice().replace("$", "");
            double price = Double.parseDouble(priceStr);
            return price * quantity;
        }

        @Override
        public String toString() {
            return String.format("CartItem{product='%s', quantity=%d, total=%.2f}",
                    product.getName(), quantity, getTotalPrice());
        }
    }

    /**
     * Test execution context model
     */
    public static class TestContext {
        private String testName;
        private String browser;
        private boolean headless;
        private String environment;
        private long startTime;
        private long endTime;

        public TestContext(String testName, String browser, boolean headless, String environment) {
            this.testName = testName;
            this.browser = browser;
            this.headless = headless;
            this.environment = environment;
            this.startTime = System.currentTimeMillis();
        }

        // Getters and setters
        public String getTestName() { return testName; }
        public String getBrowser() { return browser; }
        public boolean isHeadless() { return headless; }
        public String getEnvironment() { return environment; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getDuration() {
            return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime;
        }

        @Override
        public String toString() {
            return String.format("TestContext{test='%s', browser='%s', headless=%s, duration=%dms}",
                    testName, browser, headless, getDuration());
        }
    }

    /**
     * Error data model for negative testing
     */
    public static class ErrorScenario {
        private String errorType;
        private String expectedMessage;
        private String description;

        public ErrorScenario(String errorType, String expectedMessage, String description) {
            this.errorType = errorType;
            this.expectedMessage = expectedMessage;
            this.description = description;
        }

        // Getters
        public String getErrorType() { return errorType; }
        public String getExpectedMessage() { return expectedMessage; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return String.format("ErrorScenario{type='%s', message='%s'}", errorType, expectedMessage);
        }
    }
}