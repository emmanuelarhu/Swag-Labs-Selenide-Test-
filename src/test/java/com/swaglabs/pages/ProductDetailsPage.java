package com.swaglabs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;


public class ProductDetailsPage {
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsPage.class);

    private final SelenideElement productName = $(".inventory_details_name");
    private final SelenideElement productDescription = $(".inventory_details_desc");
    private final SelenideElement productPrice = $(".inventory_details_price");
    private final SelenideElement productImage = $(".inventory_details_img");
    private final SelenideElement addToCartButton = $("button[data-test^='add-to-cart']");
    private final SelenideElement removeButton = $("button[data-test^='remove']");
    private final SelenideElement backToProductsButton = $("#back-to-products");
    private final SelenideElement shoppingCartLink = $(".shopping_cart_link");
    private final SelenideElement cartBadge = $(".shopping_cart_badge");

    @Step("Verify product details page is displayed")
    public ProductDetailsPage verifyProductDetailsPageDisplayed() {
        logger.info("Verifying product details page is displayed");
        productName.shouldBe(Condition.visible);
        productDescription.shouldBe(Condition.visible);
        productPrice.shouldBe(Condition.visible);
        productImage.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get product name")
    public String getProductName() {
        String name = productName.getText();
        logger.info("Product name: {}", name);
        return name;
    }

    @Step("Get product description")
    public String getProductDescription() {
        String description = productDescription.getText();
        logger.info("Product description: {}", description);
        return description;
    }

    @Step("Get product price")
    public String getProductPrice() {
        String price = productPrice.getText();
        logger.info("Product price: {}", price);
        return price;
    }

    @Step("Add product to cart")
    public ProductDetailsPage addToCart() {
        logger.info("Adding product to cart from details page");
        addToCartButton.shouldBe(Condition.visible).click();
        removeButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Remove product from cart")
    public ProductDetailsPage removeFromCart() {
        logger.info("Removing product from cart from details page");
        removeButton.shouldBe(Condition.visible).click();
        addToCartButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Go back to products")
    public ProductsPage goBackToProducts() {
        logger.info("Going back to products page");
        backToProductsButton.click();
        return new ProductsPage();
    }

    @Step("Navigate to cart")
    public CartPage navigateToCart() {
        logger.info("Navigating to cart from product details");
        shoppingCartLink.click();
        return new CartPage();
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

    @Step("Verify product name: {expectedName}")
    public ProductDetailsPage verifyProductName(String expectedName) {
        logger.info("Verifying product name: {}", expectedName);
        productName.shouldHave(Condition.text(expectedName));
        return this;
    }

    @Step("Verify add to cart button is displayed")
    public ProductDetailsPage verifyAddToCartButtonDisplayed() {
        logger.info("Verifying add to cart button is displayed");
        addToCartButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify remove button is displayed")
    public ProductDetailsPage verifyRemoveButtonDisplayed() {
        logger.info("Verifying remove button is displayed");
        removeButton.shouldBe(Condition.visible);
        return this;
    }

    public boolean isAddToCartButtonDisplayed() {
        return addToCartButton.isDisplayed();
    }

    public boolean isRemoveButtonDisplayed() {
        return removeButton.isDisplayed();
    }

    public boolean isProductImageDisplayed() {
        return productImage.isDisplayed();
    }

    public boolean isBackToProductsButtonDisplayed() {
        return backToProductsButton.isDisplayed();
    }
}