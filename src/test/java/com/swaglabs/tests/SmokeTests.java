package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke Tests for critical application functionality
 */
@Epic("Swag Labs Application")
@Feature("Smoke Tests")
public class SmokeTests extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "critical"})
    @Story("Login Functionality")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test basic login functionality with valid credentials")
    public void testLogin() {
        LoginPage loginPage = new LoginPage();

        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage.verifyProductsPageDisplayed();
        assertThat(productsPage.getAllProductNames()).isNotEmpty();
    }

    @Test(priority = 2, groups = {"smoke", "critical"})
    @Story("Product Display")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test that products are displayed correctly")
    public void testProductsDisplay() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage.verifyProductsPageDisplayed();
        assertThat(productsPage.getAllProductNames()).hasSize(6);
        assertThat(productsPage.getAllProductPrices()).hasSize(6);
    }

    @Test(priority = 3, groups = {"smoke", "critical"})
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test basic add to cart functionality")
    public void testAddToCart() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemsCount()).isEqualTo(1);

        CartPage cartPage = productsPage.navigateToCart();
        cartPage.verifyCartContainsItem("Sauce Labs Backpack");
    }

    @Test(priority = 4, groups = {"smoke", "critical"})
    @Story("Complete Purchase Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test complete purchase flow from login to order completion")
    public void testCompletePurchaseFlow() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Add product to cart
        productsPage.addProductToCart("Sauce Labs Backpack");

        // Navigate to cart
        CartPage cartPage = productsPage.navigateToCart();
        cartPage.verifyCartContainsItem("Sauce Labs Backpack");

        // Proceed to checkout
        CheckoutInformationPage checkoutInfo = cartPage.proceedToCheckout();
        checkoutInfo.fillCheckoutInformation("Test", "User", "12345");

        // Continue to overview
        CheckoutOverviewPage overview = checkoutInfo.clickContinue();
        overview.verifyCheckoutOverviewPageDisplayed();

        // Complete order
        CheckoutCompletePage complete = overview.finishCheckout();
        complete.verifyCheckoutCompletePageDisplayed();
        complete.verifySuccessMessage();
    }

    @Test(priority = 5, groups = {"smoke", "critical"})
    @Story("Logout Functionality")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout functionality")
    public void testLogout() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        LoginPage logoutPage = productsPage.logout();
        logoutPage.verifyLoginPageDisplayed();
    }
}