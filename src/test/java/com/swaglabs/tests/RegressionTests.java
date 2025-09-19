package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestDataProvider;
import com.swaglabs.pages.*;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression Tests for comprehensive application validation
 */
@Epic("Swag Labs Application")
@Feature("Regression Tests")
public class RegressionTests extends BaseTest {

    @Test(priority = 1, dataProvider = "loginData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "login"})
    @Story("User Authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with different user types")
    public void testLoginWithDifferentUsers(String username, String password, String userType,
                                            boolean shouldSucceed, String description) {
        LoginPage loginPage = new LoginPage();

        if (shouldSucceed && !userType.equals("locked")) {
            ProductsPage productsPage = loginPage.login(username, password);
            productsPage.verifyProductsPageDisplayed();
            assertThat(productsPage.getAllProductNames()).isNotEmpty();
        } else if (userType.equals("locked")) {
            loginPage.login(username, password);
            loginPage.verifyErrorMessageDisplayed();
            String errorMessage = loginPage.getErrorMessageText();
            assertThat(errorMessage).contains("locked out");
        }
    }

    @Test(priority = 2, dataProvider = "productData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "products"})
    @Story("Product Information")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test product information accuracy")
    public void testProductInformation(String productName, String expectedPrice,
                                       String expectedDescriptionPart) {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        String actualPrice = productsPage.getProductPrice(productName);
        String actualDescription = productsPage.getProductDescription(productName);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actualPrice, expectedPrice);
        softAssert.assertTrue(actualDescription.contains(expectedDescriptionPart));
        softAssert.assertAll();
    }

    @Test(priority = 3, dataProvider = "multipleProductsData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "cart"})
    @Story("Cart Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test cart operations with multiple products")
    public void testCartWithMultipleProducts(String[] productNames, int expectedCount) {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Add all products
        for (String productName : productNames) {
            productsPage.addProductToCart(productName);
        }

        assertThat(productsPage.getCartItemsCount()).isEqualTo(expectedCount);

        // Verify in cart
        CartPage cartPage = productsPage.navigateToCart();
        for (String productName : productNames) {
            cartPage.verifyCartContainsItem(productName);
        }
    }

    @Test(priority = 4, dataProvider = "sortingData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "sorting"})
    @Story("Product Sorting")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test product sorting functionality")
    public void testProductSorting(String sortValue, String sortDescription) {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage.sortProducts(sortValue);

        java.util.List<String> sortedProducts = productsPage.getAllProductNames();
        assertThat(sortedProducts).hasSize(6);

        // Basic sorting validation
        if ("az".equals(sortValue)) {
            for (int i = 1; i < sortedProducts.size(); i++) {
                assertThat(sortedProducts.get(i - 1).compareToIgnoreCase(sortedProducts.get(i)))
                        .isLessThanOrEqualTo(0);
            }
        }
    }

    @Test(priority = 5, dataProvider = "checkoutData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "checkout"})
    @Story("Checkout Process")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test checkout process with different user data")
    public void testCheckoutProcess(String firstName, String lastName, String postalCode, String country) {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Add products
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        // Navigate through checkout
        CartPage cartPage = productsPage.navigateToCart();
        CheckoutInformationPage checkoutInfo = cartPage.proceedToCheckout();
        checkoutInfo.fillCheckoutInformation(firstName, lastName, postalCode);

        CheckoutOverviewPage overview = checkoutInfo.clickContinue();
        overview.verifyCheckoutOverviewPageDisplayed();

        CheckoutCompletePage complete = overview.finishCheckout();
        complete.verifyCheckoutCompletePageDisplayed();
        complete.verifySuccessMessage();
    }

    @Test(priority = 6, groups = {"regression", "navigation"})
    @Story("Page Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test navigation between different pages")
    public void testPageNavigation() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Navigate to product details
        ProductDetailsPage detailsPage = productsPage.clickOnProduct("Sauce Labs Backpack");
        detailsPage.verifyProductDetailsPageDisplayed();

        // Navigate back
        ProductsPage backToProducts = detailsPage.goBackToProducts();
        backToProducts.verifyProductsPageDisplayed();

        // Navigate to cart
        CartPage cartPage = backToProducts.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        // Continue shopping
        ProductsPage continueProducts = cartPage.continueShopping();
        continueProducts.verifyProductsPageDisplayed();
    }

    @Test(priority = 7, groups = {"regression", "validation"})
    @Story("Form Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test form validation on checkout page")
    public void testFormValidation() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = productsPage.navigateToCart();
        CheckoutInformationPage checkoutInfo = cartPage.proceedToCheckout();

        // Test empty form
        checkoutInfo.submitIncompleteForm();
        checkoutInfo.verifyErrorMessageDisplayed();
        String errorMessage = checkoutInfo.getErrorMessageText();
        assertThat(errorMessage).contains("First Name is required");

        // Test partial form
        checkoutInfo.enterFirstName("Test");
        checkoutInfo.submitIncompleteForm();
        String errorMessage2 = checkoutInfo.getErrorMessageText();
        assertThat(errorMessage2).contains("Last Name is required");
    }

    @Test(priority = 8, groups = {"regression", "session"})
    @Story("Session Management")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test session state management")
    public void testSessionManagement() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Add items to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        assertThat(productsPage.getCartItemsCount()).isEqualTo(2);

        // Logout
        LoginPage logoutPage = productsPage.logout();
        logoutPage.verifyLoginPageDisplayed();

        // Login again
        ProductsPage newSession = logoutPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Verify cart is empty in new session
        assertThat(newSession.getCartItemsCount()).isEqualTo(0);
    }
}