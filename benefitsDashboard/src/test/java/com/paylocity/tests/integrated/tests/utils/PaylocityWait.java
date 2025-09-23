package com.paylocity.tests.integrated.tests.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class PaylocityWait {

    private Wait<WebDriver> wait;
    private JavascriptExecutor jsExec;

    public PaylocityWait() {
        this.wait = new FluentWait<>(driver())
                .withTimeout(Duration.ofSeconds(50))
                .pollingEvery(Duration.ofMillis(2000))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        this.jsExec = (JavascriptExecutor) driver();
    }

    /**
     * Waits for an element to be clickable
     * @param locator The locator to find the element
     * @return The WebElement once it is clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        return wait.until(elementToBeClickable(locator));
    }

    public WebElement For(By locator, boolean skipStabilityCheck) {
        if (!skipStabilityCheck) {
        }
        Wait<WebDriver> wait = new FluentWait<>(driver())
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);
        wait.until(elementToBeClickable(locator));
        return driver().findElement(locator);
    }

    public WebElement For(By locator) {
        return For(locator, false);
    }

    public void waitUntilStable() {
        waitForDocument();
    }

    private void waitForScript(String script) {
        try {
            wait.until(driver -> (Boolean) jsExec.executeScript(script));
        } catch (WebDriverException ignored) {
        }
    }

    private void waitForDocument() {
        waitForScript("return document.readyState === 'complete';");
    }

    public void hardwait(int seconds) {
        try{
            Thread.sleep(seconds * 1000);
        } catch (Exception e){
        }
    }
}
