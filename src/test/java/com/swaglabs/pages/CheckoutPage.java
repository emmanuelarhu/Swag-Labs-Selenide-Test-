package com.swaglabs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;

/**
 * Base Page Object class for common checkout operations
 * This class provides common functionality used across checkout pages
 */
public class CheckoutPage {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutPage.class);

    // Common checkout elements that appear on multiple checkout pages
    protected final SelenideElement pageTitle = $(".title");
    protected final SelenideElement cancelButton = $("#cancel");
    protected final SelenideElement errorMessage = $("[data-test='error']");

    @Step("Verify checkout page is displayed")
    public CheckoutPage verifyCheckoutPageDisplayed() {
        logger.info("Verifying checkout page is displayed");
        pageTitle.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify error message is displayed")
    public CheckoutPage verifyErrorMessageDisplayed() {
        logger.info("Verifying error message is displayed");
        errorMessage.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get error message text")
    public String getErrorMessageText() {
        String errorText = errorMessage.getText();
        logger.info("Error message text: {}", errorText);
        return errorText;
    }

    // Validation methods
    public boolean isCancelButtonDisplayed() {
        return cancelButton.isDisplayed();
    }

    public boolean isErrorMessageDisplayed() {
        return errorMessage.isDisplayed();
    }
}