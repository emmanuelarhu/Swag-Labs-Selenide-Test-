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
 * Comprehensive Checkout Tests with validation scenarios and error handling
 */
@Epic("Swag Labs Application")
@Feature("Checkout Process")
public class CheckoutTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutInformationPage checkoutInfoPage;

    @BeforeMethod(groups = {"smoke", "regression"})
    public void setupCheckoutTest() {
        LoginPage loginPage = new LoginPage();
        productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );

        // Add items to cart for checkout testing
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        // Navigate to cart and then to checkout
        cartPage = productsPage.navigateToCart();
        checkoutInfoPage = cartPage.proceedToCheckout();
    }

    @Test(priority = 1, groups = {"smoke", "checkout", "validation", "critical"})
    @Story("Checkout Form Validation - Empty Form")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test checkout validation when submitting form without any input")
    public void testCheckoutValidationEmptyForm() {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed();

        // Try to submit empty form
        checkoutInfoPage.submitIncompleteForm()
                .verifyErrorMessageDisplayed();

        String errorMessage = checkoutInfoPage.getErrorMessageText();

        // Verify appropriate error message for empty form
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(errorMessage, "Error message should not be null");
        softAssert.assertTrue(errorMessage.contains("First Name is required"),
                String.format("Expected 'First Name is required' but got: '%s'", errorMessage));

        // Verify we're still on checkout information page
        softAssert.assertTrue(checkoutInfoPage.isFirstNameFieldDisplayed(),
                "Should remain on checkout information page after validation error");
        softAssert.assertTrue(checkoutInfoPage.isContinueButtonDisplayed(),
                "Continue button should still be displayed");

        softAssert.assertAll();
    }

    @Test(priority = 2, groups = {"regression", "checkout", "validation"})
    @Story("Checkout Form Validation - Partial Form")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test checkout validation when only first name is provided")
    public void testCheckoutValidationFirstNameOnly() {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .enterFirstName("TestFirstName")
                .submitIncompleteForm()
                .verifyErrorMessageDisplayed();

        String errorMessage = checkoutInfoPage.getErrorMessageText();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(errorMessage.contains("Last Name is required"),
                String.format("Expected 'Last Name is required' but got: '%s'", errorMessage));

        // Verify first name is retained
        String retainedFirstName = checkoutInfoPage.getFirstNameValue();
        softAssert.assertEquals(retainedFirstName, "TestFirstName",
                "First name should be retained after validation error");

        softAssert.assertAll();
    }

    @Test(priority = 3, groups = {"regression", "checkout", "validation"})
    @Story("Checkout Form Validation - Missing Postal Code")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test checkout validation when postal code is missing")
    public void testCheckoutValidationMissingPostalCode() {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .enterFirstName("TestFirst")
                .enterLastName("TestLast")
                .submitIncompleteForm()
                .verifyErrorMessageDisplayed();

        String errorMessage = checkoutInfoPage.getErrorMessageText();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(errorMessage.contains("Postal Code is required"),
                String.format("Expected 'Postal Code is required' but got: '%s'", errorMessage));

        // Verify both names are retained
        softAssert.assertEquals(checkoutInfoPage.getFirstNameValue(), "TestFirst",
                "First name should be retained");
        softAssert.assertEquals(checkoutInfoPage.getLastNameValue(), "TestLast",
                "Last name should be retained");

        softAssert.assertAll();
    }

    @Test(priority = 4, dataProvider = "invalidCheckoutData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "checkout", "validation"})
    @Story("Checkout Form Validation - Invalid Data")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test checkout validation with different invalid data scenarios")
    public void testCheckoutValidationInvalidData(String firstName, String lastName,
                                                  String postalCode, String expectedError) {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .fillCheckoutInformation(firstName, lastName, postalCode)
                .submitIncompleteForm()
                .verifyErrorMessageDisplayed();

        String actualError = checkoutInfoPage.getErrorMessageText();

        assertThat(actualError).contains(expectedError);
    }

    @Test(priority = 5, dataProvider = "checkoutData", dataProviderClass = TestDataProvider.class,
            groups = {"smoke", "checkout", "success"})
    @Story("Successful Checkout Process")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test complete checkout flow with valid checkout information")
    public void testSuccessfulCheckout(String firstName, String lastName,
                                       String postalCode, String country) {

        // Fill valid checkout information
        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .fillCheckoutInformation(firstName, lastName, postalCode);

        // Continue to overview
        CheckoutOverviewPage overviewPage = checkoutInfoPage.clickContinue();
        overviewPage.verifyCheckoutOverviewPageDisplayed();

        // Verify items in overview
        overviewPage.verifyItemInOverview("Sauce Labs Backpack")
                .verifyItemInOverview("Sauce Labs Bike Light");

        // Verify pricing information
        overviewPage.verifyPriceTotalDisplayed()
                .verifyPaymentInformationDisplayed()
                .verifyShippingInformationDisplayed();

        String itemTotal = overviewPage.getItemTotalPrice();
        String tax = overviewPage.getTaxAmount();
        String total = overviewPage.getTotalPrice();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(itemTotal, "Item total should not be null");
        softAssert.assertNotNull(tax, "Tax should not be null");
        softAssert.assertNotNull(total, "Total should not be null");

        // Complete checkout
        CheckoutCompletePage completePage = overviewPage.finishCheckout();
        completePage.verifyCheckoutCompletePageDisplayed()
                .verifySuccessMessage()
                .verifyOrderCompletionConfirmation();

        // Verify success message
        String successMessage = completePage.getSuccessHeaderText();
        softAssert.assertTrue(successMessage.contains("Thank you for your order!"),
                "Success message should contain thank you text");

        softAssert.assertAll();
    }

    @Test(priority = 6, groups = {"regression", "checkout", "cancellation"})
    @Story("Checkout Cancellation - Information Stage")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test cancelling checkout process at information entry stage")
    public void testCheckoutCancellationAtInformation() {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .enterFirstName("Test")
                .enterLastName("User");

        // Cancel checkout
        CartPage returnedCartPage = checkoutInfoPage.clickCancel();
        returnedCartPage.verifyCartPageDisplayed()
                .verifyCartHasItems();

        // Verify items are still in cart
        assertThat(returnedCartPage.getCartItemsCount()).isEqualTo(2);
        returnedCartPage.verifyCartContainsItem("Sauce Labs Backpack")
                .verifyCartContainsItem("Sauce Labs Bike Light");
    }

    @Test(priority = 7, groups = {"regression", "checkout", "cancellation"})
    @Story("Checkout Cancellation - Overview Stage")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test cancelling checkout process at overview stage")
    public void testCheckoutCancellationAtOverview() {

        // Complete information stage
        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .fillCheckoutInformation("Test", "User", "12345");

        CheckoutOverviewPage overviewPage = checkoutInfoPage.clickContinue();
        overviewPage.verifyCheckoutOverviewPageDisplayed();

        // Cancel at overview stage
        ProductsPage cancelledProductsPage = overviewPage.cancelCheckout();
        cancelledProductsPage.verifyProductsPageDisplayed();

        // Verify cart still contains items
        assertThat(cancelledProductsPage.getCartItemsCount()).isEqualTo(2);
    }

    @Test(priority = 8, dataProvider = "e2eTestData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "checkout", "e2e"})
    @Story("Complete E2E Checkout Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test complete checkout process from product selection to order completion")
    public void testCompleteE2ECheckoutFlow(String username, String password,
                                            String[] productNames, String firstName,
                                            String lastName, String postalCode) {

        // Start fresh for E2E test
        LoginPage loginPage = new LoginPage();
        ProductsPage freshProductsPage = loginPage.login(username, password);

        // Add specified products
        for (String productName : productNames) {
            freshProductsPage.addProductToCart(productName);
        }

        // Navigate through checkout process
        CartPage freshCartPage = freshProductsPage.navigateToCart();
        freshCartPage.verifyCartPageDisplayed();

        // Verify all products in cart
        for (String productName : productNames) {
            freshCartPage.verifyCartContainsItem(productName);
        }

        // Proceed to checkout
        CheckoutInformationPage freshCheckoutPage = freshCartPage.proceedToCheckout();
        freshCheckoutPage.verifyCheckoutInformationPageDisplayed()
                .fillCheckoutInformation(firstName, lastName, postalCode);

        // Review order
        CheckoutOverviewPage freshOverviewPage = freshCheckoutPage.clickContinue();
        freshOverviewPage.verifyCheckoutOverviewPageDisplayed();

        // Verify all products in overview
        for (String productName : productNames) {
            freshOverviewPage.verifyItemInOverview(productName);
        }

        // Complete order
        CheckoutCompletePage freshCompletePage = freshOverviewPage.finishCheckout();
        freshCompletePage.verifyCheckoutCompletePageDisplayed()
                .verifyOrderCompletionConfirmation();

        // Return to home and verify cart is empty
        ProductsPage finalProductsPage = freshCompletePage.goBackHome();
        finalProductsPage.verifyProductsPageDisplayed();

        assertThat(finalProductsPage.getCartItemsCount()).isEqualTo(0);
    }

    @Test(priority = 9, groups = {"regression", "checkout", "pricing"})
    @Story("Checkout Price Calculations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test accuracy of price calculations during checkout process")
    public void testCheckoutPriceCalculations() {

        checkoutInfoPage.verifyCheckoutInformationPageDisplayed()
                .fillCheckoutInformation("Test", "User", "12345");

        CheckoutOverviewPage overviewPage = checkoutInfoPage.clickContinue();
        overviewPage.verifyCheckoutOverviewPageDisplayed()
                .verifyPriceTotalDisplayed();

        // Get pricing information
        String itemTotalStr = overviewPage.getItemTotalPrice();
        String taxStr = overviewPage.getTaxAmount();
        String totalStr = overviewPage.getTotalPrice();

        SoftAssert softAssert = new SoftAssert();

        // Verify format of pricing strings
        softAssert.assertTrue(itemTotalStr.contains("$"), "Item total should contain $");
        softAssert.assertTrue(taxStr.contains("$"), "Tax should contain $");
        softAssert.assertTrue(totalStr.contains("$"), "Total should contain $");

        // Extract numeric values for calculation verification
        try {
            double itemTotal = Double.parseDouble(itemTotalStr.replaceAll("[^0-9.]", ""));
            double tax = Double.parseDouble(taxStr.replaceAll("[^0-9.]", ""));
            double total = Double.parseDouble(totalStr.replaceAll("[^0-9.]", ""));

            // Verify tax calculation (should be positive)
            softAssert.assertTrue(tax > 0, "Tax should be greater than 0");
            softAssert.assertTrue(itemTotal > 0, "Item total should be greater than 0");

            // Verify total calculation
            double calculatedTotal = itemTotal + tax;
            softAssert.assertEquals(total, calculatedTotal, 0.01,
                    String.format("Total should equal item total plus tax: %.2f + %.2f = %.2f",
                            itemTotal, tax, calculatedTotal));

        } catch (NumberFormatException e) {
            softAssert.fail("Could not parse pricing information: " + e.getMessage());
        }

        softAssert.assertAll();
    }
}