package com.swaglabs.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object class for Products page
 */
public class ProductsPage {
    private static final Logger logger = LoggerFactory.getLogger(ProductsPage.class);

    private final SelenideElement pageTitle = $(".title");
    private final SelenideElement shoppingCartLink = $(".shopping_cart_link");
    private final SelenideElement cartBadge = $(".shopping_cart_badge");
    private final SelenideElement menuButton = $("#react-burger-menu-btn");
    private final SelenideElement sortDropdown = $(".product_sort_container");
    private final ElementsCollection productItems = $$(".inventory_item");
    private final ElementsCollection productNames = $$(".inventory_item_name");
    private final ElementsCollection productPrices = $$(".inventory_item_price");
    private final ElementsCollection addToCartButtons = $$("button[data-test^='add-to-cart']");
    private final ElementsCollection removeButtons = $$("button[data-test^='remove']");

    private final SelenideElement logoutLink = $("#logout_sidebar_link");

    @Step("Verify products page is displayed")
    public ProductsPage verifyProductsPageDisplayed() {
        logger.info("Verifying products page is displayed");
        pageTitle.shouldBe(Condition.visible);
        pageTitle.shouldHave(Condition.text("Products"));
        productItems.shouldHave(CollectionCondition.sizeGreaterThan(0));
        return this;
    }

    @Step("Add product to cart by name: {productName}")
    public ProductsPage addProductToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);

        SelenideElement product = findProductByName(productName);
        SelenideElement addButton = product.$("button[data-test^='add-to-cart']");
        addButton.click();

        logger.info("Successfully added product to cart: {}", productName);
        return this;
    }

    @Step("Remove product from cart by name: {productName}")
    public ProductsPage removeProductFromCart(String productName) {
        logger.info("Removing product from cart: {}", productName);

        SelenideElement product = findProductByName(productName);
        SelenideElement removeButton = product.$("button[data-test^='remove']");
        removeButton.click();

        logger.info("Successfully removed product from cart: {}", productName);
        return this;
    }

    @Step("Click on product: {productName}")
    public ProductDetailsPage clickOnProduct(String productName) {
        logger.info("Clicking on product: {}", productName);

        SelenideElement product = findProductByName(productName);
        SelenideElement productLink = product.$(".inventory_item_name");
        productLink.click();

        logger.info("Navigated to product details for: {}", productName);
        return new ProductDetailsPage();
    }

    @Step("Get cart items count")
    public int getCartItemsCount() {
        if (cartBadge.isDisplayed()) {
            String badgeText = cartBadge.getText();
            int count = Integer.parseInt(badgeText);
            logger.info("Cart items count: {}", count);
            return count;
        }
        logger.info("Cart is empty (no badge displayed)");
        return 0;
    }

    @Step("Navigate to cart")
    public CartPage navigateToCart() {
        logger.info("Navigating to cart");
        shoppingCartLink.click();
        return new CartPage();
    }

    @Step("Sort products by: {sortOption}")
    public ProductsPage sortProducts(String sortOption) {
        logger.info("Sorting products by: {}", sortOption);
        sortDropdown.selectOptionByValue(sortOption);
        return this;
    }

    @Step("Get all product names")
    public List<String> getAllProductNames() {
        List<String> names = productNames.texts();
        logger.info("Retrieved {} product names", names.size());
        return names;
    }

    @Step("Get all product prices")
    public List<String> getAllProductPrices() {
        List<String> prices = productPrices.texts();
        logger.info("Retrieved {} product prices", prices.size());
        return prices;
    }

    @Step("Verify product is displayed: {productName}")
    public ProductsPage verifyProductDisplayed(String productName) {
        logger.info("Verifying product is displayed: {}", productName);
        findProductByName(productName).shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify add to cart button is displayed for product: {productName}")
    public ProductsPage verifyAddToCartButtonDisplayed(String productName) {
        logger.info("Verifying add to cart button for product: {}", productName);
        SelenideElement product = findProductByName(productName);
        product.$("button[data-test^='add-to-cart']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify remove button is displayed for product: {productName}")
    public ProductsPage verifyRemoveButtonDisplayed(String productName) {
        logger.info("Verifying remove button for product: {}", productName);
        SelenideElement product = findProductByName(productName);
        product.$("button[data-test^='remove']").shouldBe(Condition.visible);
        return this;
    }

    @Step("Open menu")
    public ProductsPage openMenu() {
        logger.info("Opening navigation menu");
        menuButton.click();
        return this;
    }

    @Step("Logout")
    public LoginPage logout() {
        logger.info("Logging out");
        openMenu();
        logoutLink.shouldBe(Condition.visible).click();
        return new LoginPage();
    }

    @Step("Get product price by name: {productName}")
    public String getProductPrice(String productName) {
        logger.info("Getting price for product: {}", productName);
        SelenideElement product = findProductByName(productName);
        String price = product.$(".inventory_item_price").getText();
        logger.info("Product {} price: {}", productName, price);
        return price;
    }

    @Step("Get product description by name: {productName}")
    public String getProductDescription(String productName) {
        logger.info("Getting description for product: {}", productName);
        SelenideElement product = findProductByName(productName);
        String description = product.$(".inventory_item_desc").getText();
        logger.info("Product {} description: {}", productName, description);
        return description;
    }

    private SelenideElement findProductByName(String productName) {
        logger.debug("Finding product by name: {}", productName);
        return productItems.findBy(Condition.text(productName));
    }

    public boolean isProductDisplayed(String productName) {
        return findProductByName(productName).isDisplayed();
    }

    public boolean isCartBadgeDisplayed() {
        return cartBadge.isDisplayed();
    }

    public boolean isAddToCartButtonDisplayed(String productName) {
        SelenideElement product = findProductByName(productName);
        return product.$("button[data-test^='add-to-cart']").isDisplayed();
    }

    public boolean isRemoveButtonDisplayed(String productName) {
        SelenideElement product = findProductByName(productName);
        return product.$("button[data-test^='remove']").isDisplayed();
    }
}