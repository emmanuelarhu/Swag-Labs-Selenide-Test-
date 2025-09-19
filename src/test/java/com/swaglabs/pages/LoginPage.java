package com.swaglabs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class LoginPage {
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

    private final SelenideElement usernameField = $("#user-name");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement loginButton = $("#login-button");
    private final SelenideElement errorMessage = $("[data-test='error']");
    private final SelenideElement loginLogo = $(".login_logo");
    private final SelenideElement loginCredentials = $("#login_credentials");

    @Step("Verify login page is displayed")
    public LoginPage verifyLoginPageDisplayed() {
        logger.info("Verifying login page is displayed");
        loginLogo.shouldBe(Condition.visible);
        usernameField.shouldBe(Condition.visible);
        passwordField.shouldBe(Condition.visible);
        loginButton.shouldBe(Condition.visible);
        return this;
    }

    @Step("Login with username: {username}")
    public ProductsPage login(String username, String password) {
        logger.info("Attempting to login with username: {}", username);

        enterUsername(username);
        enterPassword(password);
        clickLoginButton();

        logger.info("Login attempt completed");
        return new ProductsPage();
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        logger.debug("Entering username: {}", username);
        usernameField.clear();
        usernameField.setValue(username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        logger.debug("Entering password");
        passwordField.clear();
        passwordField.setValue(password);
        return this;
    }

    @Step("Click login button")
    public LoginPage clickLoginButton() {
        logger.debug("Clicking login button");
        loginButton.click();
        return this;
    }

    @Step("Verify error message is displayed: {expectedMessage}")
    public LoginPage verifyErrorMessage(String expectedMessage) {
        logger.info("Verifying error message: {}", expectedMessage);
        errorMessage.shouldBe(Condition.visible);
        errorMessage.shouldHave(Condition.text(expectedMessage));
        return this;
    }

    @Step("Verify error message is displayed")
    public LoginPage verifyErrorMessageDisplayed() {
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

    @Step("Clear login form")
    public LoginPage clearForm() {
        logger.debug("Clearing login form");
        usernameField.clear();
        passwordField.clear();
        return this;
    }

    @Step("Verify accepted usernames are displayed")
    public LoginPage verifyAcceptedUsernamesDisplayed() {
        logger.info("Verifying accepted usernames are displayed");
        loginCredentials.shouldBe(Condition.visible);
        return this;
    }

    @Step("Get accepted usernames text")
    public String getAcceptedUsernamesText() {
        String credentialsText = loginCredentials.getText();
        logger.info("Login credentials text: {}", credentialsText);
        return credentialsText;
    }

    // Validation methods
    public boolean isUsernameFieldDisplayed() {
        return usernameField.isDisplayed();
    }

    public boolean isPasswordFieldDisplayed() {
        return passwordField.isDisplayed();
    }

    public boolean isLoginButtonDisplayed() {
        return loginButton.isDisplayed();
    }

    public boolean isErrorMessageDisplayed() {
        return errorMessage.isDisplayed();
    }
}