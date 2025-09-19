package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestDataProvider;
import com.swaglabs.pages.*;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Shopping Cart Tests with parameterized scenarios
 */
@Epic("Swag Labs Application")
@Feature("Shopping Cart Management")
public class CartTest extends BaseTest {

    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeMethod(groups = {"smoke", "regression"})
    public void setupCartTest() {
        LoginPage loginPage = new LoginPage();
        productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );
        productsPage.verifyProductsPageDisplayed();
    }

    @Test(priority = 1, groups = {"smoke", "cart", "critical"})
    @Story("Basic Cart Operations")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test basic add to cart and remove from cart functionality")
    public void testBasicCartOperations() {

        String testProduct = "Sauce Labs Backpack";

        // Verify cart is initially empty
        assertThat(productsPage.getCartItemsCount()).isEqualTo(0);

        // Add product to cart
        productsPage.addProductToCart(testProduct);

        // Verify cart count updated
        assertThat(productsPage.getCartItemsCount()).isEqualTo(1);
        productsPage.verifyRemoveButtonDisplayed(testProduct);

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartContainsItem(testProduct);

        // Verify cart item details
        assertThat(cartPage.getCartItemsCount()).isEqualTo(1);
        assertThat(cartPage.getItemQuantity(testProduct)).isEqualTo(1);

        String itemPrice = cartPage.getItemPrice(testProduct);
        assertThat(itemPrice).isEqualTo("$29.99");

        // Remove item from cart
        cartPage.removeItemFromCart(testProduct);

        // Verify cart is empty
        cartPage.verifyCartIsEmpty();
        assertThat(cartPage.getCartItemsCount()).isEqualTo(0);
    }

    @Test(priority = 2, dataProvider = "multipleProductsData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "cart", "management"})
    @Story("Multiple Products Cart Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test adding, removing, and managing multiple products in cart")
    public void testMultipleProductsCartManagement(String[] productNames, int expectedCount) {

        // Verify cart starts empty
        assertThat(productsPage.getCartItemsCount()).isEqualTo(0);

        // Add all products to cart
        for (String productName : productNames) {
            productsPage.addProductToCart(productName);
        }

        // Verify cart count
        assertThat(productsPage.getCartItemsCount()).isEqualTo(expectedCount);

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartHasItems();

        SoftAssert softAssert = new SoftAssert();

        // Verify all items are in cart
        List<String> cartItems = cartPage.getAllCartItemNames();
        softAssert.assertEquals(cartItems.size(), expectedCount,
                "Cart should contain expected number of items");

        for (String productName : productNames) {
            softAssert.assertTrue(cartItems.contains(productName),
                    String.format("Cart should contain: %s", productName));
            softAssert.assertEquals(cartPage.getItemQuantity(productName), 1,
                    String.format("Quantity should be 1 for: %s", productName));
        }

        // Test removing items one by one
        for (int i = 0; i < productNames.length; i++) {
            String productToRemove = productNames[i];
            cartPage.removeItemFromCart(productToRemove);

            int expectedRemainingItems = expectedCount - (i + 1);
            softAssert.assertEquals(cartPage.getCartItemsCount(), expectedRemainingItems,
                    String.format("After removing %s, cart should have %d items",
                            productToRemove, expectedRemainingItems));
        }

        // Verify cart is empty
        cartPage.verifyCartIsEmpty();
        softAssert.assertEquals(cartPage.getCartItemsCount(), 0,
                "Cart should be empty after removing all items");

        softAssert.assertAll();
    }

    @Test(priority = 3, groups = {"regression", "cart", "persistence"})
    @Story("Cart State Persistence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify cart contents persist when navigating between pages")
    public void testCartPersistenceAcrossNavigation() {

        String[] testProducts = {"Sauce Labs Backpack", "Sauce Labs Bike Light", "Sauce Labs Bolt T-Shirt"};

        // Add products to cart
        for (String product : testProducts) {
            productsPage.addProductToCart(product);
        }

        int initialCartCount = productsPage.getCartItemsCount();
        assertThat(initialCartCount).isEqualTo(testProducts.length);

        // Navigate to product details and back
        ProductDetailsPage detailsPage = productsPage.clickOnProduct(testProducts[0]);
        assertThat(detailsPage.getCartItemsCount()).isEqualTo(initialCartCount);

        ProductsPage returnedProductsPage = detailsPage.goBackToProducts();
        assertThat(returnedProductsPage.getCartItemsCount()).isEqualTo(initialCartCount);

        // Navigate to cart
        cartPage = returnedProductsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        SoftAssert softAssert = new SoftAssert();

        // Verify all items still present
        for (String product : testProducts) {
            softAssert.assertTrue(cartPage.isItemInCart(product),
                    String.format("Cart should still contain: %s", product));
        }

        // Navigate back to products and verify cart persists
        ProductsPage finalProductsPage = cartPage.continueShopping();
        softAssert.assertEquals(finalProductsPage.getCartItemsCount(), initialCartCount,
                "Cart count should persist after navigation");

        softAssert.assertAll();
    }

    @Test(priority = 4, dataProvider = "productData", dataProviderClass = TestDataProvider.class,
            groups = {"regression", "cart", "validation"})
    @Story("Cart Item Information")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify cart displays correct item information including price and description")
    public void testCartItemInformationValidation(String productName, String expectedPrice,
                                                  String expectedDescriptionPart) {

        // Add product to cart
        productsPage.addProductToCart(productName);

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartContainsItem(productName);

        // Verify item details in cart
        String cartItemPrice = cartPage.getItemPrice(productName);
        String cartItemDescription = cartPage.getItemDescription(productName);
        int itemQuantity = cartPage.getItemQuantity(productName);

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(cartItemPrice, expectedPrice,
                String.format("Cart price should match expected for: %s", productName));
        softAssert.assertTrue(cartItemDescription.contains(expectedDescriptionPart),
                String.format("Cart description should contain expected text for: %s", productName));
        softAssert.assertEquals(itemQuantity, 1,
                String.format("Item quantity should be 1 for: %s", productName));

        softAssert.assertAll();

        // Clean up
        cartPage.removeItemFromCart(productName);
    }

    @Test(priority = 5, groups = {"regression", "cart", "checkout"})
    @Story("Cart Checkout Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test navigation from cart to checkout process")
    public void testCartCheckoutNavigation() {

        // Add items to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartHasItems()
                .verifyCheckoutButtonEnabled();

        // Proceed to checkout
        CheckoutInformationPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.verifyCheckoutInformationPageDisplayed();

        // Verify we can navigate back to cart
        CartPage returnedCartPage = checkoutPage.clickCancel();
        returnedCartPage.verifyCartPageDisplayed()
                .verifyCartHasItems();

        // Verify items are still in cart
        assertThat(returnedCartPage.getCartItemsCount()).isEqualTo(2);
    }

    @Test(priority = 6, groups = {"regression", "cart", "edge-cases"})
    @Story("Cart Edge Cases")
    @Severity(SeverityLevel.MINOR)
    @Description("Test cart behavior in edge case scenarios like empty cart")
    public void testCartEdgeCaseScenarios() {

        // Navigate to cart when empty
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartIsEmpty();

        SoftAssert softAssert = new SoftAssert();

        // Verify empty cart state
        softAssert.assertEquals(cartPage.getCartItemsCount(), 0,
                "Empty cart should show 0 items");
        softAssert.assertTrue(cartPage.isCartEmpty(),
                "Cart should be identified as empty");
        softAssert.assertTrue(cartPage.isContinueShoppingButtonDisplayed(),
                "Continue shopping button should be displayed");

        // Test continue shopping from empty cart
        ProductsPage returnedProductsPage = cartPage.continueShopping();
        returnedProductsPage.verifyProductsPageDisplayed();

        // Add item, then remove it in cart
        returnedProductsPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartWithItem = returnedProductsPage.navigateToCart();
        cartWithItem.removeItemFromCart("Sauce Labs Backpack");

        // Verify cart returns to empty state
        softAssert.assertTrue(cartWithItem.isCartEmpty(),
                "Cart should be empty after removing last item");

        softAssert.assertAll();
    }

    @Test(priority = 7, groups = {"regression", "cart", "ui"})
    @Story("Cart User Interface")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify cart UI elements function correctly")
    public void testCartUIElementsValidation() {

        // Add multiple items to test UI
        String[] testProducts = {"Sauce Labs Backpack", "Sauce Labs Fleece Jacket"};

        for (String product : testProducts) {
            productsPage.addProductToCart(product);
        }

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        SoftAssert softAssert = new SoftAssert();

        // Verify UI elements are present
        softAssert.assertTrue(cartPage.isContinueShoppingButtonDisplayed(),
                "Continue shopping button should be displayed");
        softAssert.assertTrue(cartPage.isCheckoutButtonDisplayed(),
                "Checkout button should be displayed");

        // Verify cart item layout
        List<String> cartItems = cartPage.getAllCartItemNames();
        List<String> cartPrices = cartPage.getAllCartItemPrices();

        softAssert.assertEquals(cartItems.size(), testProducts.length,
                "Should display all cart items");
        softAssert.assertEquals(cartPrices.size(), testProducts.length,
                "Should display all cart item prices");

        // Test remove buttons for each item
        for (String product : testProducts) {
            softAssert.assertTrue(cartPage.isItemInCart(product),
                    String.format("Remove functionality should be available for: %s", product));
        }

        softAssert.assertAll();
    }

    @Test(priority = 8, groups = {"regression", "cart", "performance"})
    @Story("Cart Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify cart operations complete within acceptable time limits")
    public void testCartOperationPerformance() {

        long startTime = System.currentTimeMillis();

        // Add multiple items
        String[] products = {"Sauce Labs Backpack", "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt", "Sauce Labs Fleece Jacket"};

        for (String product : products) {
            productsPage.addProductToCart(product);
        }

        // Navigate to cart
        cartPage = productsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed();

        // Perform cart operations
        List<String> items = cartPage.getAllCartItemNames();
        List<String> prices = cartPage.getAllCartItemPrices();

        // Remove all items
        for (String product : products) {
            cartPage.removeItemFromCart(product);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performance assertions
        assertThat(items).hasSize(products.length);
        assertThat(prices).hasSize(products.length);
        assertThat(duration).isLessThan(10000); // Should complete within 10 seconds

        System.out.println(String.format("Cart operations completed in: %d ms", duration));
    }

    @Test(priority = 9, groups = {"smoke", "cart", "integration"})
    @Story("Cart Product Details Integration")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test cart operations from product details page")
    public void testCartProductDetailsPageIntegration() {

        String testProduct = "Sauce Labs Backpack";

        // Navigate to product details
        ProductDetailsPage detailsPage = productsPage.clickOnProduct(testProduct);
        detailsPage.verifyProductDetailsPageDisplayed();

        // Add to cart from details page
        detailsPage.addToCart();
        assertThat(detailsPage.getCartItemsCount()).isEqualTo(1);

        // Navigate to cart from details page
        cartPage = detailsPage.navigateToCart();
        cartPage.verifyCartPageDisplayed()
                .verifyCartContainsItem(testProduct);

        // Verify item details match
        String cartPrice = cartPage.getItemPrice(testProduct);
        assertThat(cartPrice).isNotEmpty();

        // Navigate back to products
        ProductsPage finalProductsPage = cartPage.continueShopping();
        finalProductsPage.verifyProductsPageDisplayed();

        // Verify cart count persists
        assertThat(finalProductsPage.getCartItemsCount()).isEqualTo(1);

        // Verify remove button is shown on products page
        assertThat(finalProductsPage.isRemoveButtonDisplayed(testProduct)).isTrue();
    }
}