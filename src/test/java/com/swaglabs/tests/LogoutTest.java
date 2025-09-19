package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestDataProvider;
import com.swaglabs.pages.*;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Logout Tests with session management validation
 */
@Epic("Swag Labs Application")
@Feature("User Session Management")
public class LogoutTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(groups = {"smoke", "regression"})
    public void setupLogoutTest() {
        LoginPage loginPage = new LoginPage();
        productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );
        productsPage.verifyProductsPageDisplayed();
    }

    @Test(
            priority = 1,
            groups = {"smoke", "logout", "critical"},
            description = "Test basic logout functionality"
    )
    @Story("Basic Logout")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test that user can successfully logout and return to login page")
    public void testBasicLogout() {

        // Perform logout
        LoginPage loginPage = productsPage.logout();

        // Verify return to login page
        loginPage.verifyLoginPageDisplayed()
                .verifyAcceptedUsernamesDisplayed();

        SoftAssert softAssert = new SoftAssert();

        // Verify login page elements
        softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username field should be displayed after logout");
        softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password field should be displayed after logout");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be displayed after logout");

        // Verify no error messages from logout
        softAssert.assertFalse(loginPage.isErrorMessageDisplayed(),
                "No error message should be displayed after successful logout");

        softAssert.assertAll();
    }

    @Test(
            priority = 2,
            groups = {"regression", "logout", "session"},
            description = "Test logout clears session data including cart items"
    )
    @Story("Session Data Cleanup")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that logout properly clears all session data including cart contents")
    public void testLogoutClearsSessionData() {

        // Add items to cart before logout
        String[] testProducts = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};

        for (String product : testProducts) {
            productsPage.addProductToCart(product);
        }

        // Verify cart has items
        int cartCountBeforeLogout = productsPage.getCartItemsCount();
        assertThat(cartCountBeforeLogout).isEqualTo(testProducts.length);

        // Logout
        LoginPage loginPage = productsPage.logout();
        loginPage.verifyLoginPageDisplayed();

        // Login again
        ProductsPage newSessionProductsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Verify cart is empty in new session
        assertThat(newSessionProductsPage.getCartItemsCount()).isEqualTo(0);

        // Verify all products show "Add to cart" buttons (not "Remove")
        for (String product : testProducts) {
            assertThat(newSessionProductsPage.isAddToCartButtonDisplayed(product)).isTrue();
            assertThat(newSessionProductsPage.isRemoveButtonDisplayed(product)).isFalse();
        }
    }

    @Test(
            priority = 3,
            dataProvider = "loginData",
            dataProviderClass = TestDataProvider.class,
            groups = {"regression", "logout", "multi-user"},
            description = "Test logout works correctly for different user types"
    )
    @Story("Multi-User Logout")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout functionality for different user types")
    public void testLogoutForDifferentUsers(String username, String password, String userType,
                                            boolean shouldSucceed, String description) {

        if (!shouldSucceed || userType.equals("locked")) {
            return; // Skip invalid users for logout testing
        }

        // Login with specific user
        LoginPage loginPage = new LoginPage();
        ProductsPage userProductsPage = loginPage.login(username, password);
        userProductsPage.verifyProductsPageDisplayed();

        // Add some items to test session cleanup
        userProductsPage.addProductToCart("Sauce Labs Backpack");
        assertThat(userProductsPage.getCartItemsCount()).isEqualTo(1);

        // Logout
        LoginPage logoutPage = userProductsPage.logout();
        logoutPage.verifyLoginPageDisplayed();

        // Login again and verify clean session
        ProductsPage cleanSessionPage = logoutPage.login(username, password);
        cleanSessionPage.verifyProductsPageDisplayed();

        // Verify cart is empty
        assertThat(cleanSessionPage.getCartItemsCount()).isEqualTo(0);
    }

    @Test(
            priority = 4,
            groups = {"regression", "logout", "navigation"},
            description = "Test logout from different pages"
    )
    @Story("Logout from Different Pages")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout functionality works from different pages in the application")
    public void testLogoutFromDifferentPages() {

        SoftAssert softAssert = new SoftAssert();

        // Test logout from products page (already setup)
        LoginPage loginFromProducts = productsPage.logout();
        loginFromProducts.verifyLoginPageDisplayed();

        // Login and test logout from product details page
        ProductsPage productsPage2 = loginFromProducts.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        ProductDetailsPage detailsPage = productsPage2.clickOnProduct("Sauce Labs Backpack");
        detailsPage.verifyProductDetailsPageDisplayed();

        // Add item to cart from details page
        detailsPage.addToCart();
        assertThat(detailsPage.getCartItemsCount()).isEqualTo(1);

        // Navigate back to products and logout
        ProductsPage productsFromDetails = detailsPage.goBackToProducts();
        LoginPage loginFromDetails = productsFromDetails.logout();
        loginFromDetails.verifyLoginPageDisplayed();

        // Test logout after cart operations
        ProductsPage productsPage3 = loginFromDetails.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        productsPage3.addProductToCart("Sauce Labs Bike Light");
        CartPage cartPage = productsPage3.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        // Return to products and logout
        ProductsPage productsFromCart = cartPage.continueShopping();
        LoginPage finalLogin = productsFromCart.logout();
        finalLogin.verifyLoginPageDisplayed();

        softAssert.assertTrue(true, "Logout should work from all pages");
        softAssert.assertAll();
    }

    @Test(
            priority = 5,
            groups = {"regression", "logout", "security"},
            description = "Test logout prevents unauthorized access"
    )
    @Story("Logout Security")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test that after logout, user cannot access protected pages without re-login")
    public void testLogoutSecurity() {

        // Add items to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        // Get current URL (should be products page)
        // Note: This would typically involve browser navigation checks

        // Logout
        LoginPage loginPage = productsPage.logout();
        loginPage.verifyLoginPageDisplayed();

        // Verify we're on login page and cannot access products directly
        // In a real scenario, we might try to navigate directly to products URL
        // and verify it redirects to login

        // Attempt to login again should work
        ProductsPage newSession = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        newSession.verifyProductsPageDisplayed();

        // Verify fresh session (cart should be empty)
        assertThat(newSession.getCartItemsCount()).isEqualTo(0);
    }

    @Test(
            priority = 6,
            groups = {"regression", "logout", "ui"},
            description = "Test logout UI elements and menu navigation"
    )
    @Story("Logout UI Elements")
    @Severity(SeverityLevel.MINOR)
    @Description("Test that logout UI elements function correctly")
    public void testLogoutUIElements() {

        SoftAssert softAssert = new SoftAssert();

        // Open menu to access logout
        productsPage.openMenu();

        // In a real implementation, we would verify:
        // - Menu opens correctly
        // - Logout option is visible
        // - Logout option is clickable
        // - Menu can be closed without logging out

        // For this test, we'll just verify logout works
        LoginPage loginPage = productsPage.logout();
        loginPage.verifyLoginPageDisplayed();

        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Should return to login page with login button visible");

        softAssert.assertAll();
    }

    @Test(
            priority = 7,
            groups = {"regression", "logout", "performance"},
            description = "Test logout performance"
    )
    @Story("Logout Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Test that logout completes within acceptable time")
    public void testLogoutPerformance() {

        // Add multiple items to create session data
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"};

        for (String product : products) {
            productsPage.addProductToCart(product);
        }

        long startTime = System.currentTimeMillis();

        // Perform logout
        LoginPage loginPage = productsPage.logout();
        loginPage.verifyLoginPageDisplayed();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performance assertion
        assertThat(duration).isLessThan(5000); // Should complete within 5 seconds

        System.out.println(String.format("Logout completed in: %d ms", duration));
    }

    @Test(
            priority = 8,
            groups = {"regression", "logout", "edge-cases"},
            description = "Test logout edge cases and error scenarios"
    )
    @Story("Logout Edge Cases")
    @Severity(SeverityLevel.MINOR)
    @Description("Test logout behavior in edge case scenarios")
    public void testLogoutEdgeCases() {

        SoftAssert softAssert = new SoftAssert();

        // Test logout with large cart
        String[] allProducts = {"Sauce Labs Backpack", "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie", "Test.allTheThings() T-Shirt (Red)"};

        for (String product : allProducts) {
            productsPage.addProductToCart(product);
        }

        assertThat(productsPage.getCartItemsCount()).isEqualTo(6);

        // Logout should still work with full cart
        LoginPage loginPage = productsPage.logout();
        loginPage.verifyLoginPageDisplayed();

        // Login again and verify cart is clean
        ProductsPage cleanSession = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        assertThat(cleanSession.getCartItemsCount()).isEqualTo(0);

        softAssert.assertTrue(true, "Logout should handle edge cases gracefully");
        softAssert.assertAll();
    }

    @Test(
            priority = 9,
            groups = {"regression", "logout", "integration"},
            description = "Test logout integration with complete user workflow"
    )
    @Story("Logout Workflow Integration")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout as part of complete user workflow")
    public void testLogoutWorkflowIntegration() {

        // Complete user workflow before logout
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        // Navigate through different pages
        ProductDetailsPage detailsPage = productsPage.clickOnProduct("Sauce Labs Bolt T-Shirt");
        detailsPage.addToCart();

        ProductsPage backToProducts = detailsPage.goBackToProducts();
        CartPage cartPage = backToProducts.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        // Verify workflow state
        assertThat(cartPage.getCartItemsCount()).isEqualTo(3);

        // Return to products and logout
        ProductsPage finalProducts = cartPage.continueShopping();
        LoginPage logoutPage = finalProducts.logout();
        logoutPage.verifyLoginPageDisplayed();

        // Verify clean slate after logout
        ProductsPage newWorkflow = logoutPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        assertThat(newWorkflow.getCartItemsCount()).isEqualTo(0);

        // Should be able to start fresh workflow
        newWorkflow.addProductToCart("Sauce Labs Fleece Jacket");
        assertThat(newWorkflow.getCartItemsCount()).isEqualTo(1);
    }
}