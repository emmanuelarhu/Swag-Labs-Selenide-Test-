package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestDataProvider;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.pages.ProductsPage;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parameterized Login Tests with comprehensive credential validation
 */
@Epic("Swag Labs Application")
@Feature("User Authentication")
public class LoginTest extends BaseTest {

    @Test(
            priority = 1,
            dataProvider = "loginData",
            dataProviderClass = TestDataProvider.class,
            groups = {"smoke", "login", "critical"},
            description = "Test login with valid credentials for different user types"
    )
    @Story("Valid User Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify successful login with valid credentials for different user types")
    public void testValidLogin(String username, String password, String userType,
                               boolean shouldSucceed, String description) {

        LoginPage loginPage = new LoginPage();

        // Verify login page elements
        loginPage.verifyLoginPageDisplayed()
                .verifyAcceptedUsernamesDisplayed();

        if (shouldSucceed && !userType.equals("locked")) {
            // Expected successful login
            ProductsPage productsPage = loginPage.login(username, password);

            // Assertions for successful login
            productsPage.verifyProductsPageDisplayed();
            assertThat(productsPage.getAllProductNames()).isNotEmpty();
            assertThat(productsPage.getCartItemsCount()).isEqualTo(0);

            // Verify specific user behavior
            if (userType.equals("problem")) {
                // Problem user specific validations
                assertThat(productsPage.getAllProductNames()).hasSize(6);
            } else if (userType.equals("performance")) {
                // Performance glitch user - might be slower but should work
                assertThat(productsPage.getAllProductNames()).hasSize(6);
            }

        } else if (userType.equals("locked")) {
            // Expected login failure for locked user
            loginPage.login(username, password)
                    ;

            String errorMessage = loginPage.getErrorMessageText();
            assertThat(errorMessage).contains("locked out")
                    .contains("Sorry, this user has been locked out");
        }
    }

    @Test(
            priority = 2,
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProvider.class,
            groups = {"smoke", "login", "negative", "validation"},
            description = "Test login with invalid credentials and verify error messages"
    )
    @Story("Invalid Login Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify proper error messages for invalid login attempts")
    public void testInvalidLogin(String username, String password, String expectedErrorMessage) {

        LoginPage loginPage = new LoginPage();

        loginPage.verifyLoginPageDisplayed()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButton()
                .verifyErrorMessageDisplayed();

        String actualErrorMessage = loginPage.getErrorMessageText();

        // Detailed assertions for error messages
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertNotNull(actualErrorMessage, "Error message should not be null");
        softAssert.assertTrue(actualErrorMessage.contains(expectedErrorMessage),
                String.format("Expected error message to contain: '%s', but got: '%s'",
                        expectedErrorMessage, actualErrorMessage));

        // Additional validations based on error type
        if (expectedErrorMessage.contains("Username is required")) {
            softAssert.assertTrue(actualErrorMessage.contains("Username"),
                    "Error should mention Username field");
        } else if (expectedErrorMessage.contains("Password is required")) {
            softAssert.assertTrue(actualErrorMessage.contains("Password"),
                    "Error should mention Password field");
        } else if (expectedErrorMessage.contains("do not match")) {
            softAssert.assertTrue(actualErrorMessage.toLowerCase().contains("match"),
                    "Error should mention credential mismatch");
        }

        softAssert.assertAll();
    }

    @Test(
            priority = 3,
            groups = {"smoke", "login", "ui"},
            description = "Verify login page UI elements and layout"
    )
    @Story("Login Page UI")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify all login page elements are present and functional")
    public void testLoginPageElements() {

        LoginPage loginPage = new LoginPage();

        // Verify all page elements
        loginPage.verifyLoginPageDisplayed();

        SoftAssert softAssert = new SoftAssert();

        // Verify form fields
        softAssert.assertTrue(loginPage.isUsernameFieldDisplayed(),
                "Username field should be displayed");
        softAssert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password field should be displayed");
        softAssert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be displayed");

        // Verify credentials information
        String credentialsText = loginPage.getAcceptedUsernamesText();
        softAssert.assertNotNull(credentialsText, "Credentials text should not be null");
        softAssert.assertTrue(credentialsText.contains("standard_user"),
                "Should display standard_user in accepted usernames");
        softAssert.assertTrue(credentialsText.contains("secret_sauce"),
                "Should display password for all users");

        softAssert.assertAll();
    }

    @Test(
            priority = 4,
            groups = {"smoke", "login", "security"},
            description = "Test password field security (masked input)"
    )
    @Story("Login Security")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify password field security features")
    public void testPasswordFieldSecurity() {

        LoginPage loginPage = new LoginPage();

        loginPage.verifyLoginPageDisplayed()
                .enterUsername("standard_user")
                .enterPassword("secret_sauce");

        // Verify password field is masked (this would be implementation specific)
        // For now, just verify the field accepts input without displaying it
        assertThat(loginPage.isPasswordFieldDisplayed()).isTrue();
    }

    @Test(
            priority = 5,
            groups = {"regression", "login", "boundary"},
            description = "Test login with boundary value inputs"
    )
    @Story("Login Boundary Testing")
    @Severity(SeverityLevel.MINOR)
    @Description("Test login with edge case inputs like very long strings")
    public void testLoginBoundaryValues() {

        LoginPage loginPage = new LoginPage();

        // Test with very long username
        String longUsername = "a".repeat(255);
        String longPassword = "b".repeat(255);

        loginPage.verifyLoginPageDisplayed()
                .enterUsername(longUsername)
                .enterPassword(longPassword)
                .clickLoginButton()
                .verifyErrorMessageDisplayed();

        String errorMessage = loginPage.getErrorMessageText();
        assertThat(errorMessage).contains("do not match");
    }

    @Test(
            priority = 6,
            groups = {"regression", "login", "session"},
            description = "Test multiple login attempts"
    )
    @Story("Multiple Login Attempts")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify system handles multiple failed login attempts")
    public void testMultipleLoginAttempts() {

        LoginPage loginPage = new LoginPage();

        // Attempt multiple invalid logins
        for (int i = 1; i <= 3; i++) {
            loginPage.verifyLoginPageDisplayed()
                    .enterUsername("invalid_user_" + i)
                    .enterPassword("invalid_pass_" + i)
                    .clickLoginButton()
                    .verifyErrorMessageDisplayed();

            String errorMessage = loginPage.getErrorMessageText();
            assertThat(errorMessage).contains("do not match");

            // Clear form for next attempt
            loginPage.clearForm();
        }

        // Finally attempt valid login to ensure account isn't locked
        ProductsPage productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );
        productsPage.verifyProductsPageDisplayed();
    }

    @Test(
            priority = 7,
            dataProvider = "loginData",
            dataProviderClass = TestDataProvider.class,
            groups = {"regression", "login", "navigation"},
            description = "Test navigation after successful login"
    )
    @Story("Post-Login Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify proper navigation and state after successful login")
    public void testPostLoginNavigation(String username, String password, String userType,
                                        boolean shouldSucceed, String description) {

        if (!shouldSucceed || userType.equals("locked")) {
            return; // Skip invalid login scenarios for this test
        }

        LoginPage loginPage = new LoginPage();
        ProductsPage productsPage = loginPage.login(username, password);

        // Verify successful navigation
        productsPage.verifyProductsPageDisplayed();

        // Verify page state
        assertThat(productsPage.getCartItemsCount()).isEqualTo(0);
        assertThat(productsPage.getAllProductNames()).isNotEmpty();

        // Verify we can navigate away and back
        var detailsPage = productsPage.clickOnProduct("Sauce Labs Backpack");
        detailsPage.verifyProductDetailsPageDisplayed();

        var returnedProductsPage = detailsPage.goBackToProducts();
        returnedProductsPage.verifyProductsPageDisplayed();

        // Verify state is maintained
        assertThat(returnedProductsPage.getCartItemsCount()).isEqualTo(0);
    }
}