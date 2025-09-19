package com.swaglabs.tests;

import com.swaglabs.base.BaseTest;
import com.swaglabs.data.TestDataProvider;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.pages.ProductsPage;
import com.swaglabs.pages.ProductDetailsPage;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Products Page Tests with parameterized product validation
 */
@Epic("Swag Labs Application")
@Feature("Product Catalog Management")
public class ProductsTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(groups = {"smoke", "regression"})
    public void loginToApplication() {
        LoginPage loginPage = new LoginPage();
        productsPage = loginPage.login(
                config.getStandardUsername(),
                config.getStandardPassword()
        );
        productsPage.verifyProductsPageDisplayed();
    }

    @Test(
            priority = 1,
            groups = {"smoke", "products", "critical"},
            description = "Verify products page displays all required elements"
    )
    @Story("Product Page Display")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test verifies that products page displays all products with complete information")
    public void testProductsPageDisplay() {

        // Verify page title and layout
        productsPage.verifyProductsPageDisplayed();

        // Get all product information
        List<String> productNames = productsPage.getAllProductNames();
        List<String> productPrices = productsPage.getAllProductPrices();

        SoftAssert softAssert = new SoftAssert();

        // Verify product count
        softAssert.assertEquals(productNames.size(), 6,
                "Should display exactly 6 products");
        softAssert.assertEquals(productPrices.size(), 6,
                "Should display 6 product prices");

        // Verify specific products are present
        softAssert.assertTrue(productNames.contains("Sauce Labs Backpack"),
                "Should contain Sauce Labs Backpack");
        softAssert.assertTrue(productNames.contains("Sauce Labs Bike Light"),
                "Should contain Sauce Labs Bike Light");
        softAssert.assertTrue(productNames.contains("Sauce Labs Bolt T-Shirt"),
                "Should contain Sauce Labs Bolt T-Shirt");
        softAssert.assertTrue(productNames.contains("Sauce Labs Fleece Jacket"),
                "Should contain Sauce Labs Fleece Jacket");
        softAssert.assertTrue(productNames.contains("Sauce Labs Onesie"),
                "Should contain Sauce Labs Onesie");
        softAssert.assertTrue(productNames.contains("Test.allTheThings() T-Shirt (Red)"),
                "Should contain Test.allTheThings() T-Shirt");

        // Verify all products have valid prices
        for (String price : productPrices) {
            softAssert.assertTrue(price.startsWith("$"),
                    String.format("Price '%s' should start with $", price));
            softAssert.assertTrue(price.matches("\\$\\d+\\.\\d{2}"),
                    String.format("Price '%s' should be in format $XX.XX", price));
        }

        // Verify cart is initially empty
        softAssert.assertEquals(productsPage.getCartItemsCount(), 0,
                "Cart should be initially empty");

        softAssert.assertAll();
    }

    @Test(
            priority = 2,
            dataProvider = "productData",
            dataProviderClass = TestDataProvider.class,
            groups = {"smoke", "products", "validation"},
            description = "Verify individual product information and functionality"
    )
    @Story("Product Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test verifies each product displays correct information and add-to-cart functionality")
    public void testIndividualProductValidation(String productName, String expectedPrice,
                                                String expectedDescriptionPart) {

        // Verify product is displayed
        productsPage.verifyProductDisplayed(productName);

        // Get actual product information
        String actualPrice = productsPage.getProductPrice(productName);
        String actualDescription = productsPage.getProductDescription(productName);

        SoftAssert softAssert = new SoftAssert();

        // Verify product details
        softAssert.assertEquals(actualPrice, expectedPrice,
                String.format("Price mismatch for product: %s", productName));
        softAssert.assertTrue(actualDescription.contains(expectedDescriptionPart),
                String.format("Description for %s should contain: '%s', but was: '%s'",
                        productName, expectedDescriptionPart, actualDescription));

        // Verify add to cart button functionality
        softAssert.assertTrue(productsPage.isAddToCartButtonDisplayed(productName),
                String.format("Add to cart button should be displayed for: %s", productName));

        // Test add to cart functionality
        int initialCartCount = productsPage.getCartItemsCount();
        productsPage.addProductToCart(productName);

        int newCartCount = productsPage.getCartItemsCount();
        softAssert.assertEquals(newCartCount, initialCartCount + 1,
                "Cart count should increase by 1 after adding product");

        // Verify remove button appears
        softAssert.assertTrue(productsPage.isRemoveButtonDisplayed(productName),
                String.format("Remove button should be displayed after adding: %s", productName));

        // Test remove functionality
        productsPage.removeProductFromCart(productName);
        int finalCartCount = productsPage.getCartItemsCount();
        softAssert.assertEquals(finalCartCount, initialCartCount,
                "Cart count should return to original after removing product");

        // Verify add button is back
        softAssert.assertTrue(productsPage.isAddToCartButtonDisplayed(productName),
                String.format("Add to cart button should be displayed again for: %s", productName));

        softAssert.assertAll();
    }

    @Test(
            priority = 3,
            dataProvider = "sortingData",
            dataProviderClass = TestDataProvider.class,
            groups = {"smoke", "products", "sorting"},
            description = "Test product sorting functionality"
    )
    @Story("Product Sorting")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify products can be sorted correctly by different criteria")
    public void testProductSorting(String sortValue, String sortDescription) {

        // Get initial product list
        List<String> initialProducts = productsPage.getAllProductNames();
        List<String> initialPrices = productsPage.getAllProductPrices();

        // Apply sorting
        productsPage.sortProducts(sortValue);

        // Get sorted lists
        List<String> sortedProducts = productsPage.getAllProductNames();
        List<String> sortedPrices = productsPage.getAllProductPrices();

        SoftAssert softAssert = new SoftAssert();

        // Verify same number of products
        softAssert.assertEquals(sortedProducts.size(), initialProducts.size(),
                "Product count should remain same after sorting");
        softAssert.assertEquals(sortedPrices.size(), initialPrices.size(),
                "Price count should remain same after sorting");

        // Verify all products still present
        softAssert.assertTrue(sortedProducts.containsAll(initialProducts),
                "All original products should still be present after sorting");

        // Verify sorting logic based on sort type
        switch (sortValue) {
            case "az":
                // Name A to Z - should be alphabetically sorted
                for (int i = 1; i < sortedProducts.size(); i++) {
                    softAssert.assertTrue(
                            sortedProducts.get(i - 1).compareToIgnoreCase(sortedProducts.get(i)) <= 0,
                            String.format("Products should be in alphabetical order: %s should come before %s",
                                    sortedProducts.get(i - 1), sortedProducts.get(i)));
                }
                break;

            case "za":
                // Name Z to A - should be reverse alphabetically sorted
                for (int i = 1; i < sortedProducts.size(); i++) {
                    softAssert.assertTrue(
                            sortedProducts.get(i - 1).compareToIgnoreCase(sortedProducts.get(i)) >= 0,
                            String.format("Products should be in reverse alphabetical order: %s should come after %s",
                                    sortedProducts.get(i - 1), sortedProducts.get(i)));
                }
                break;

            case "lohi":
            case "hilo":
                // Price sorting - verify prices are in correct order
                for (int i = 1; i < sortedPrices.size(); i++) {
                    double price1 = Double.parseDouble(sortedPrices.get(i - 1).replace("$", ""));
                    double price2 = Double.parseDouble(sortedPrices.get(i).replace("$", ""));

                    if (sortValue.equals("lohi")) {
                        softAssert.assertTrue(price1 <= price2,
                                String.format("Prices should be low to high: $%.2f should be <= $%.2f", price1, price2));
                    } else {
                        softAssert.assertTrue(price1 >= price2,
                                String.format("Prices should be high to low: $%.2f should be >= $%.2f", price1, price2));
                    }
                }
                break;
        }

        softAssert.assertAll();
    }

    @Test(
            priority = 4,
            dataProvider = "productData",
            dataProviderClass = TestDataProvider.class,
            groups = {"regression", "products", "navigation"},
            description = "Test navigation to product details page"
    )
    @Story("Product Details Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify clicking on products navigates to correct details page")
    public void testProductDetailsNavigation(String productName, String expectedPrice,
                                             String expectedDescriptionPart) {

        // Navigate to product details
        ProductDetailsPage detailsPage = productsPage.clickOnProduct(productName);

        // Verify details page
        detailsPage.verifyProductDetailsPageDisplayed()
                .verifyProductName(productName);

        // Verify product information consistency
        String detailsPrice = detailsPage.getProductPrice();
        String detailsDescription = detailsPage.getProductDescription();
        String detailsName = detailsPage.getProductName();

        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals(detailsName, productName,
                "Product name should match on details page");
        softAssert.assertEquals(detailsPrice, expectedPrice,
                "Product price should match on details page");
        softAssert.assertTrue(detailsDescription.contains(expectedDescriptionPart),
                "Product description should contain expected text on details page");

        // Test navigation back to products
        ProductsPage returnedProductsPage = detailsPage.goBackToProducts();
        returnedProductsPage.verifyProductsPageDisplayed();

        // Verify we're back on the same page
        assertThat(returnedProductsPage.getAllProductNames()).hasSize(6);

        softAssert.assertAll();
    }

    @Test(
            priority = 5,
            dataProvider = "multipleProductsData",
            dataProviderClass = TestDataProvider.class,
            groups = {"regression", "products", "cart"},
            description = "Test adding multiple products to cart"
    )
    @Story("Multiple Products Cart Operations")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify multiple products can be added to cart and cart count updates correctly")
    public void testMultipleProductsAddToCart(String[] productNames, int expectedCount) {

        // Verify cart is initially empty
        assertThat(productsPage.getCartItemsCount()).isEqualTo(0);

        // Add all products
        for (String productName : productNames) {
            productsPage.addProductToCart(productName);
            productsPage.verifyRemoveButtonDisplayed(productName);
        }

        // Verify final cart count
        assertThat(productsPage.getCartItemsCount()).isEqualTo(expectedCount);

        // Verify all products show remove buttons
        for (String productName : productNames) {
            assertThat(productsPage.isRemoveButtonDisplayed(productName)).isTrue();
        }

        // Remove all products
        for (String productName : productNames) {
            productsPage.removeProductFromCart(productName);
        }

        // Verify cart is empty again
        assertThat(productsPage.getCartItemsCount()).isEqualTo(0);
    }

    @Test(
            priority = 6,
            groups = {"regression", "products", "ui"},
            description = "Test products page UI elements and interactions"
    )
    @Story("Products Page UI")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify all UI elements on products page function correctly")
    public void testProductsPageUIElements() {

        SoftAssert softAssert = new SoftAssert();

        // Verify cart badge behavior
        softAssert.assertEquals(productsPage.getCartItemsCount(), 0,
                "Cart should be initially empty");
        softAssert.assertFalse(productsPage.isCartBadgeDisplayed(),
                "Cart badge should not be displayed when empty");

        // Add item and verify badge appears
        productsPage.addProductToCart("Sauce Labs Backpack");
        softAssert.assertEquals(productsPage.getCartItemsCount(), 1,
                "Cart count should be 1 after adding item");
        softAssert.assertTrue(productsPage.isCartBadgeDisplayed(),
                "Cart badge should be displayed when items are present");

        // Test menu functionality
        productsPage.openMenu();
        // Note: Menu verification would depend on implementation

        // Clean up
        productsPage.removeProductFromCart("Sauce Labs Backpack");
        softAssert.assertEquals(productsPage.getCartItemsCount(), 0,
                "Cart should be empty after removing item");

        softAssert.assertAll();
    }

    @Test(
            priority = 7,
            groups = {"regression", "products", "performance"},
            description = "Test products page load performance"
    )
    @Story("Products Page Performance")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify products page loads within acceptable time limits")
    public void testProductsPagePerformance() {

        long startTime = System.currentTimeMillis();

        // Perform various operations
        List<String> products = productsPage.getAllProductNames();
        List<String> prices = productsPage.getAllProductPrices();

        // Test sorting performance
        productsPage.sortProducts("za");
        productsPage.sortProducts("az");

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Performance assertions
        assertThat(products).hasSize(6);
        assertThat(prices).hasSize(6);
        assertThat(duration).isLessThan(5000); // Should complete within 5 seconds

        System.out.println(String.format("Products page operations completed in: %d ms", duration));
    }

    @Test(
            priority = 8,
            groups = {"regression", "products", "accessibility"},
            description = "Test products page accessibility features"
    )
    @Story("Products Page Accessibility")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify basic accessibility features on products page")
    public void testProductsPageAccessibility() {

        SoftAssert softAssert = new SoftAssert();

        // Verify products are displayed
        List<String> productNames = productsPage.getAllProductNames();
        softAssert.assertTrue(productNames.size() > 0,
                "Products should be displayed for accessibility");

        // Verify interactive elements are present
        for (String productName : productNames) {
            softAssert.assertTrue(productsPage.isAddToCartButtonDisplayed(productName),
                    String.format("Add to cart button should be accessible for: %s", productName));
        }

        // Basic navigation accessibility
        softAssert.assertTrue(productsPage.getAllProductNames().size() > 0,
                "Should be able to access product information");

        softAssert.assertAll();
    }
}