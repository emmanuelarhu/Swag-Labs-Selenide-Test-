package com.swaglabs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object class for Checkout Complete page
 */
public class CheckoutCompletePage {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutCompletePage.class);

    // Page elements
    private final SelenideElement pageTitle = $(".title");
    private final SelenideElement completeHeader = $(".complete-header");
    private final SelenideElement completeText = $(".complete-text");
    private final SelenideElement backHomeButton = $("#back-to-products");
    private final SelenideElement checkmarkIcon = $(".pony_express");

    @Step("Verify checkout complete page is displayed")
    public CheckoutCompletePage verifyCheckoutCompletePageDisplayed() {
        logger.info("Verifying checkout complete page is displayed");
        pageTitle.shouldBe(Condition.visible);
        pageTitle.shouldHave(Condition.text("Checkout: Complete!"));
        completeHeader.shouldBe(Condition.visible);
        completeText.shouldBe(Condition.visible);
        backHomeButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify success message is displayed")
    public CheckoutCompletePage verifySuccessMessage() {
        logger.info("Verifying success message is displayed");
        completeHeader.shouldHave(Condition.text("Thank you for your order!"));
        completeText.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get success header text")
    public String getSuccessHeaderText() {
        String headerText = completeHeader.getText();
        logger.info("Success header text: {}", headerText);
        return headerText;
    }

    @Step("Get success message text")
    public String getSuccessMessageText() {
        String messageText = completeText.getText();
        logger.info("Success message text: {}", messageText);
        return messageText;
    }

    @Step("Go back to home/products page")
    public ProductsPage goBackHome() {
        logger.info("Going back to home/products page");
        backHomeButton.click();
        return new ProductsPage();
    }

    @Step("Verify checkmark icon is displayed")
    public CheckoutCompletePage verifyCheckmarkIconDisplayed() {
        logger.info("Verifying checkmark icon is displayed");
        checkmarkIcon.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify back home button is displayed")
    public CheckoutCompletePage verifyBackHomeButtonDisplayed() {
        logger.info("Verifying back home button is displayed");
        backHomeButton.shouldBe(Condition.visible);
        backHomeButton.shouldBe(Condition.enabled);
        return this;
    }

    @Step("Verify order completion confirmation")
    public CheckoutCompletePage verifyOrderCompletionConfirmation() {
        logger.info("Verifying complete order confirmation");
        verifyCheckoutCompletePageDisplayed();
        verifySuccessMessage();
        verifyCheckmarkIconDisplayed();
        return this;
    }

    // Validation methods
    public boolean isSuccessHeaderDisplayed() {
        return completeHeader.isDisplayed();
    }

    public boolean isSuccessMessageDisplayed() {
        return completeText.isDisplayed();
    }

    public boolean isBackHomeButtonDisplayed() {
        return backHomeButton.isDisplayed();
    }

    public boolean isCheckmarkIconDisplayed() {
        return checkmarkIcon.isDisplayed();
    }
}