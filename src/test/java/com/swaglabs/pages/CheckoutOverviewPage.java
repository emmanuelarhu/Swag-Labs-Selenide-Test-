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
 * Page Object class for Checkout Overview page
 */
public class CheckoutOverviewPage {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutOverviewPage.class);

    // Page elements
    private final SelenideElement pageTitle = $(".title");
    private final SelenideElement cancelButton = $("#cancel");
    private final SelenideElement finishButton = $("#finish");

    // Order summary elements
    private final ElementsCollection cartItems = $$(".cart_item");
    private final ElementsCollection cartItemNames = $$(".inventory_item_name");
    private final ElementsCollection cartItemPrices = $$(".inventory_item_price");
    private final ElementsCollection cartItemQuantities = $$(".cart_quantity");

    // Payment and shipping info
    private final SelenideElement paymentInformation = $(".summary_info_label:contains('Payment Information:')");
    private final SelenideElement shippingInformation = $(".summary_info_label:contains('Shipping Information:')");
    private final SelenideElement itemTotalPrice = $(".summary_subtotal_label");
    private final SelenideElement taxAmount = $(".summary_tax_label");
    private final SelenideElement totalPrice = $(".summary_total_label");

    @Step("Verify checkout overview page is displayed")
    public CheckoutOverviewPage verifyCheckoutOverviewPageDisplayed() {
        logger.info("Verifying checkout overview page is displayed");
        pageTitle.shouldBe(Condition.visible);
        pageTitle.shouldHave(Condition.text("Checkout: Overview"));
        finishButton.shouldBe(Condition.visible);
        cancelButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get cart items count in overview")
    public int getCartItemsCount() {
        int count = cartItems.size();
        logger.info("Checkout overview contains {} items", count);
        return count;
    }

    @Step("Verify item is in checkout overview: {itemName}")
    public CheckoutOverviewPage verifyItemInOverview(String itemName) {
        logger.info("Verifying item is in checkout overview: {}", itemName);
        cartItemNames.findBy(Condition.text(itemName)).shouldBe(Condition.visible);
        return this;
    }

    @Step("Get all item names in overview")
    public List<String> getAllItemNames() {
        List<String> names = cartItemNames.texts();
        logger.info("Checkout overview item names: {}", names);
        return names;
    }

    @Step("Get all item prices in overview")
    public List<String> getAllItemPrices() {
        List<String> prices = cartItemPrices.texts();
        logger.info("Checkout overview item prices: {}", prices);
        return prices;
    }

    @Step("Get item total price")
    public String getItemTotalPrice() {
        String total = itemTotalPrice.getText();
        logger.info("Item total price: {}", total);
        return total;
    }

    @Step("Get tax amount")
    public String getTaxAmount() {
        String tax = taxAmount.getText();
        logger.info("Tax amount: {}", tax);
        return tax;
    }

    @Step("Get total price")
    public String getTotalPrice() {
        String total = totalPrice.getText();
        logger.info("Total price: {}", total);
        return total;
    }

    @Step("Get payment information")
    public String getPaymentInformation() {
        SelenideElement paymentValue = paymentInformation.sibling(0);
        String payment = paymentValue.getText();
        logger.info("Payment information: {}", payment);
        return payment;
    }

    @Step("Get shipping information")
    public String getShippingInformation() {
        SelenideElement shippingValue = shippingInformation.sibling(0);
        String shipping = shippingValue.getText();
        logger.info("Shipping information: {}", shipping);
        return shipping;
    }

    @Step("Finish checkout")
    public CheckoutCompletePage finishCheckout() {
        logger.info("Finishing checkout process");
        finishButton.click();
        return new CheckoutCompletePage();
    }

    @Step("Cancel checkout")
    public ProductsPage cancelCheckout() {
        logger.info("Cancelling checkout process");
        cancelButton.click();
        return new ProductsPage();
    }

    @Step("Verify payment information is displayed")
    public CheckoutOverviewPage verifyPaymentInformationDisplayed() {
        logger.info("Verifying payment information is displayed");
        paymentInformation.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify shipping information is displayed")
    public CheckoutOverviewPage verifyShippingInformationDisplayed() {
        logger.info("Verifying shipping information is displayed");
        shippingInformation.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify price total is displayed")
    public CheckoutOverviewPage verifyPriceTotalDisplayed() {
        logger.info("Verifying price total is displayed");
        itemTotalPrice.shouldBe(Condition.visible);
        taxAmount.shouldBe(Condition.visible);
        totalPrice.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify finish button is enabled")
    public CheckoutOverviewPage verifyFinishButtonEnabled() {
        logger.info("Verifying finish button is enabled");
        finishButton.shouldBe(Condition.enabled);
        return this;
    }

    @Step("Get item quantity in overview: {itemName}")
    public int getItemQuantity(String itemName) {
        logger.info("Getting quantity for item in overview: {}", itemName);
        SelenideElement item = findItemByName(itemName);
        String quantityText = item.$(".cart_quantity").getText();
        int quantity = Integer.parseInt(quantityText);
        logger.info("Item {} quantity in overview: {}", itemName, quantity);
        return quantity;
    }

    @Step("Get item price in overview: {itemName}")
    public String getItemPrice(String itemName) {
        logger.info("Getting price for item in overview: {}", itemName);
        SelenideElement item = findItemByName(itemName);
        String price = item.$(".inventory_item_price").getText();
        logger.info("Item {} price in overview: {}", itemName, price);
        return price;
    }

    // Helper methods
    private SelenideElement findItemByName(String itemName) {
        logger.debug("Finding item by name in overview: {}", itemName);
        return cartItems.findBy(Condition.text(itemName));
    }

    // Validation methods
    public boolean isItemInOverview(String itemName) {
        return cartItemNames.findBy(Condition.text(itemName)).isDisplayed();
    }

    public boolean isFinishButtonDisplayed() {
        return finishButton.isDisplayed();
    }

    public boolean isCancelButtonDisplayed() {
        return cancelButton.isDisplayed();
    }

    public boolean isPaymentInformationDisplayed() {
        return paymentInformation.isDisplayed();
    }

    public boolean isShippingInformationDisplayed() {
        return shippingInformation.isDisplayed();
    }
}