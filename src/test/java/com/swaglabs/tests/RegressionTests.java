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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
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
        // First session - add items to cart
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
        );

        // Add items to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        assertThat(productsPage.getCartItemsCount()).isEqualTo(2);

        // Logout to end session
        LoginPage logoutPage = productsPage.logout();
        logoutPage.verifyLoginPageDisplayed();

        // Start new session - login again
        ProductsPage newSession = logoutPage.login(
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
        );

        // Verify cart is empty in new session (this is expected behavior for this app)
        // Note: SauceDemo doesn't persist cart across sessions, so cart should be empty
        int cartCount = newSession.getCartItemsCount();

        // For SauceDemo, cart doesn't persist across sessions, so we expect 0
        // If your application should persist cart, change this assertion accordingly
        assertThat(cartCount).describedAs("Cart should be empty after new login session").isEqualTo(0);
    }

    @Test(priority = 9, groups = {"regression", "ui"})
    @Story("UI Elements")
    @Severity(SeverityLevel.MINOR)
    @Description("Test UI elements are displayed correctly")
    public void testUIElements() {
        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
        );

        // Verify UI elements
        assertThat(productsPage.getAllProductNames()).hasSize(6);
        assertThat(productsPage.getAllProductPrices()).hasSize(6);

        // Test product interactions
        productsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(productsPage.getCartItemsCount()).isEqualTo(1);

        // Navigate to cart and verify UI
        CartPage cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed();
        assertThat(cartPage.getCartItemsCount()).isEqualTo(1);
    }

    @Test(priority = 10, groups = {"regression", "error-handling"})
    @Story("Error Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test application error handling")
    public void testErrorHandling() {
        LoginPage loginPage = new LoginPage();

        // Test invalid login
        loginPage.login("invalid_user", "invalid_password");
        loginPage.verifyErrorMessageDisplayed();

        String errorMessage = loginPage.getErrorMessageText();
        assertThat(errorMessage).contains("do not match");

        // Clear and try valid login
        loginPage.clearForm();
        ProductsPage productsPage = loginPage.login(
                getConfig().getStandardUsername(),
                getConfig().getStandardPassword()
        );

        productsPage.verifyProductsPageDisplayed();
    }
}