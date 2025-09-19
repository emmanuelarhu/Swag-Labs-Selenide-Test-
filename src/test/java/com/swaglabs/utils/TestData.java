package com.swaglabs.utils;

import org.testng.annotations.DataProvider;

public class TestData {
    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return new Object[][] {
            {"standard_user", "secret_sauce", "Swag Labs"}
        };
    }

    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        return new Object[][] {
            {"invalid_user", "wrong_password", "Epic sadface: Username and password do not match any user in this service"},
            {"locked_out_user", "secret_sauce", "Epic sadface: Sorry, this user has been locked out."},
            {"", "", "Epic sadface: Username is required"}
        };
    }

    @DataProvider(name = "emptyCheckoutData")
    public static Object[][] emptyCheckoutData() {
        return new Object[][] {
            {"", "", "", "Error: First Name is required"}
        };
    }

    @DataProvider(name = "partialCheckoutData")
    public static Object[][] partialCheckoutData() {
        return new Object[][] {
            {"Emmanuel", "", "", "Error: Last Name is required"}
        };
    }

    @DataProvider(name = "validCheckoutData")
    public static Object[][] validCheckoutData() {
        return new Object[][] {
            {"Emmanuel", "Arhu", "0233",
             new String[] {"Checkout: Overview", "Payment Information", "Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"},
             new String[] {"Checkout: Complete!", "Thank you for your order!"}
            }
        };
    }

    @DataProvider(name = "cartItems")
    public static Object[][] cartItems() {
        return new Object[][] {
            { new String[] {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"} }
        };
    }
}
