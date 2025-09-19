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
 * Page Object class for Cart page
 */
public class CartPage {
    private static final Logger logger = LoggerFactory.getLogger(CartPage.class);

    // Page elements
    private final SelenideElement pageTitle = $(".title");
    private final SelenideElement continueShoppingButton = $("#continue-shopping");
    private final SelenideElement checkoutButton = $("#checkout");
    private final ElementsCollection cartItems = $$(".cart_item");
    private final ElementsCollection cartItemNames = $$(".inventory_item_name");
    private final ElementsCollection cartItemPrices = $$(".inventory_item_price");
    private final ElementsCollection cartItemQuantities = $$(".cart_quantity");
    private final ElementsCollection removeButtons = $$("button[data-test^='remove']");

    @Step("Verify cart page is displayed")
    public CartPage verifyCartPageDisplayed() {
        logger.info("Verifying cart page is displayed");
        pageTitle.shouldBe(Condition.visible);
        pageTitle.shouldHave(Condition.text("Your Cart"));
        return this;
    }

    @Step("Get cart items count")
    public int getCartItemsCount() {
        int count = cartItems.size();
        logger.info("Cart contains {} items", count);
        return count;
    }

    @Step("Verify cart contains item: {itemName}")
    public CartPage verifyCartContainsItem(String itemName) {
        logger.info("Verifying cart contains item: {}", itemName);
        cartItemNames.findBy(Condition.text(itemName)).shouldBe(Condition.visible);
        return this;
    }

    @Step("Remove item from cart: {itemName}")
    public CartPage removeItemFromCart(String itemName) {
        logger.info("Removing item from cart: {}", itemName);

        SelenideElement item = findCartItemByName(itemName);
        SelenideElement removeButton = item.$("button[data-test^='remove']");
        removeButton.click();

        logger.info("Successfully removed item from cart: {}", itemName);
        return this;
    }

    @Step("Get all cart item names")
    public List<String> getAllCartItemNames() {
        List<String> names = cartItemNames.texts();
        logger.info("Cart item names: {}", names);
        return names;
    }

    @Step("Get all cart item prices")
    public List<String> getAllCartItemPrices() {
        List<String> prices = cartItemPrices.texts();
        logger.info("Cart item prices: {}", prices);
        return prices;
    }

    @Step("Get item quantity: {itemName}")
    public int getItemQuantity(String itemName) {
        logger.info("Getting quantity for item: {}", itemName);
        SelenideElement item = findCartItemByName(itemName);
        String quantityText = item.$(".cart_quantity").getText();
        int quantity = Integer.parseInt(quantityText);
        logger.info("Item {} quantity: {}", itemName, quantity);
        return quantity;
    }

    @Step("Continue shopping")
    public ProductsPage continueShopping() {
        logger.info("Continuing shopping");
        continueShoppingButton.click();
        return new ProductsPage();
    }

    @Step("Proceed to checkout")
    public CheckoutInformationPage proceedToCheckout() {
        logger.info("Proceeding to checkout");
        checkoutButton.click();
        return new CheckoutInformationPage();
    }

    @Step("Verify checkout button is enabled")
    public CartPage verifyCheckoutButtonEnabled() {
        logger.info("Verifying checkout button is enabled");
        checkoutButton.shouldBe(Condition.enabled);
        return this;
    }

    @Step("Verify checkout button is disabled")
    public CartPage verifyCheckoutButtonDisabled() {
        logger.info("Verifying checkout button is disabled");
        checkoutButton.shouldBe(Condition.disabled);
        return this;
    }

    @Step("Verify cart is empty")
    public CartPage verifyCartIsEmpty() {
        logger.info("Verifying cart is empty");
        cartItems.shouldHave(CollectionCondition.size(0));
        return this;
    }

    @Step("Verify cart has items")
    public CartPage verifyCartHasItems() {
        logger.info("Verifying cart has items");
        cartItems.shouldHave(CollectionCondition.sizeGreaterThan(0));
        return this;
    }

    @Step("Get item price by name: {itemName}")
    public String getItemPrice(String itemName) {
        logger.info("Getting price for item: {}", itemName);
        SelenideElement item = findCartItemByName(itemName);
        String price = item.$(".inventory_item_price").getText();
        logger.info("Item {} price: {}", itemName, price);
        return price;
    }

    @Step("Get item description by name: {itemName}")
    public String getItemDescription(String itemName) {
        logger.info("Getting description for item: {}", itemName);
        SelenideElement item = findCartItemByName(itemName);
        String description = item.$(".inventory_item_desc").getText();
        logger.info("Item {} description: {}", itemName, description);
        return description;
    }

    // Helper methods
    private SelenideElement findCartItemByName(String itemName) {
        logger.debug("Finding cart item by name: {}", itemName);
        return cartItems.findBy(Condition.text(itemName));
    }

    // Validation methods
    public boolean isItemInCart(String itemName) {
        return cartItemNames.findBy(Condition.text(itemName)).isDisplayed();
    }

    public boolean isContinueShoppingButtonDisplayed() {
        return continueShoppingButton.isDisplayed();
    }

    public boolean isCheckoutButtonDisplayed() {
        return checkoutButton.isDisplayed();
    }

    public boolean isCartEmpty() {
        return cartItems.size() == 0;
    }
}