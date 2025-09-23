package com.paylocity.tests.integrated.pages;

import com.paylocity.tests.integrated.tests.utils.TestingContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;

public class LoginPage extends BasePage {
    private static final By userNameField = By.cssSelector("input[name='Username']");
    private static final By passwordField = By.cssSelector("input[name='Password']");


    public void doLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        WebElement loginButton = driver().findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();
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
