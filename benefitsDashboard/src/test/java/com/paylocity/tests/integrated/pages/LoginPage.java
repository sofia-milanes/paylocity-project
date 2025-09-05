package com.paylocity.tests.integrated.pages;

import com.paylocity.tests.integrated.tests.utils.TestingContext;
import org.openqa.selenium.By;

import java.util.Optional;

public class LoginPage extends BasePage {
    private By userNameField = By.cssSelector("input[name='Username']");
    private By passwordField = By.cssSelector("input[name='Password']");
    private By loginButton = By.cssSelector("button[type='submit']");

    public void doLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton, true);
    }

    @Override
    protected Optional<String> pageUrl() {
        return Optional.of(TestingContext.appUrl + "auth/login");
    }

    @Override
    protected By onPageMarkerLoc() {
        return userNameField;
    }

    /***********
     * Actions *
     ***********/
    public void enterUsername(String username) {
        enterText(userNameField, username, false);
    }

    public void enterPassword(String password) {
        enterText(passwordField, password, false);
    }

}
