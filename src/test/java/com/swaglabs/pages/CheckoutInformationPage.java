package com.swaglabs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object class for Checkout Information page
 */
public class CheckoutInformationPage {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutInformationPage.class);

    // Page elements
    private final SelenideElement pageTitle = $(".title");
    private final SelenideElement firstNameField = $("#first-name");
    private final SelenideElement lastNameField = $("#last-name");
    private final SelenideElement postalCodeField = $("#postal-code");
    private final SelenideElement cancelButton = $("#cancel");
    private final SelenideElement continueButton = $("#continue");
    private final SelenideElement errorMessage = $("[data-test='error']");

    @Step("Verify checkout information page is displayed")
    public CheckoutInformationPage verifyCheckoutInformationPageDisplayed() {
        logger.info("Verifying checkout information page is displayed");
        pageTitle.shouldBe(Condition.visible);
        pageTitle.shouldHave(Condition.text("Checkout: Your Information"));
        firstNameField.shouldBe(Condition.visible);
        lastNameField.shouldBe(Condition.visible);
        postalCodeField.shouldBe(Condition.visible);
        return this;
    }

    @Step("Fill checkout information - First Name: {firstName}, Last Name: {lastName}, Postal Code: {postalCode}")
    public CheckoutInformationPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        logger.info("Filling checkout information - First: {}, Last: {}, Postal: {}", firstName, lastName, postalCode);

        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);

        return this;
    }

    @Step("Enter first name: {firstName}")
    public CheckoutInformationPage enterFirstName(String firstName) {
        logger.debug("Entering first name: {}", firstName);
        firstNameField.clear();
        firstNameField.setValue(firstName);
        return this;
    }

    @Step("Enter last name: {lastName}")
    public CheckoutInformationPage enterLastName(String lastName) {
        logger.debug("Entering last name: {}", lastName);
        lastNameField.clear();
        lastNameField.setValue(lastName);
        return this;
    }

    @Step("Enter postal code: {postalCode}")
    public CheckoutInformationPage enterPostalCode(String postalCode) {
        logger.debug("Entering postal code: {}", postalCode);
        postalCodeField.clear();
        postalCodeField.setValue(postalCode);
        return this;
    }

    @Step("Click continue button")
    public CheckoutOverviewPage clickContinue() {
        logger.info("Clicking continue button to proceed to checkout overview");
        continueButton.click();
        return new CheckoutOverviewPage();
    }

    @Step("Click cancel button")
    public CartPage clickCancel() {
        logger.info("Clicking cancel button to return to cart");
        cancelButton.click();
        return new CartPage();
    }

    @Step("Submit incomplete form")
    public CheckoutInformationPage submitIncompleteForm() {
        logger.info("Submitting incomplete form to trigger validation");
        continueButton.click();
        return this;
    }

    @Step("Verify error message is displayed")
    public CheckoutInformationPage verifyErrorMessageDisplayed() {
        logger.info("Verifying error message is displayed");
        errorMessage.shouldBe(Condition.visible);
        return this;
    }

    @Step("Verify error message is displayed: {expectedMessage}")
    public CheckoutInformationPage verifyErrorMessage(String expectedMessage) {
        logger.info("Verifying error message: {}", expectedMessage);
        errorMessage.shouldBe(Condition.visible);
        errorMessage.shouldHave(Condition.text(expectedMessage));
        return this;
    }

    @Step("Get error message text")
    public String getErrorMessageText() {
        String errorText = errorMessage.getText();
        logger.info("Error message text: {}", errorText);
        return errorText;
    }

    @Step("Clear all fields")
    public CheckoutInformationPage clearAllFields() {
        logger.debug("Clearing all checkout information fields");
        firstNameField.clear();
        lastNameField.clear();
        postalCodeField.clear();
        return this;
    }

    @Step("Verify continue button is enabled")
    public CheckoutInformationPage verifyContinueButtonEnabled() {
        logger.info("Verifying continue button is enabled");
        continueButton.shouldBe(Condition.enabled);
        return this;
    }

    @Step("Get first name field value")
    public String getFirstNameValue() {
        String value = firstNameField.getValue();
        logger.debug("First name field value: {}", value);
        return value != null ? value : "";
    }

    @Step("Get last name field value")
    public String getLastNameValue() {
        String value = lastNameField.getValue();
        logger.debug("Last name field value: {}", value);
        return value != null ? value : "";
    }

    @Step("Get postal code field value")
    public String getPostalCodeValue() {
        String value = postalCodeField.getValue();
        logger.debug("Postal code field value: {}", value);
        return value != null ? value : "";
    }

    // Validation methods
    public boolean isFirstNameFieldDisplayed() {
        return firstNameField.isDisplayed();
    }

    public boolean isLastNameFieldDisplayed() {
        return lastNameField.isDisplayed();
    }

    public boolean isPostalCodeFieldDisplayed() {
        return postalCodeField.isDisplayed();
    }

    public boolean isContinueButtonDisplayed() {
        return continueButton.isDisplayed();
    }

    public boolean isCancelButtonDisplayed() {
        return cancelButton.isDisplayed();
    }

    public boolean isErrorMessageDisplayed() {
        return errorMessage.isDisplayed();
    }
}