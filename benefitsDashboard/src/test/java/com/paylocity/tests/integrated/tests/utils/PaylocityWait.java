package com.paylocity.tests.integrated.tests.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf;

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

    public List<WebElement> ForMany(By locator, boolean skipStabilityCheck) {
        if (!skipStabilityCheck) {
        }
        Wait<WebDriver> wait = new FluentWait<>(driver())
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(100))
                .ignoring(NoSuchElementException.class);
        try {
            wait.until(elementToBeClickable(locator));
        } catch (TimeoutException e) {
            System.out.println("Element not found");
        }
        return driver().findElements(locator);
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

    public void untilElementNotPresent(WebElement element) {
        until(stalenessOf(element), true);
    }

    public <T> void until(ExpectedCondition<T> condition, boolean allowMissing) {
        try {
            FluentWait<WebDriver> myWait = new FluentWait<>(driver())
                    .withTimeout(Duration.ofSeconds(30))
                    .pollingEvery(Duration.ofMillis(100));
            if (allowMissing) {
                myWait.ignoring(NoSuchElementException.class);
            }
            myWait.until(condition);
        } catch (WebDriverException e) {
            Assert.fail("Time out on waiting until condition: \"" + condition + "\".\n" + e.getMessage());
        }
    }

    public void waitUntilStable() {
        waitForDocument();
        waitForAngular();
    }

    private void waitForAngular() {
        try {
            Object angularCheck = jsExec.executeScript("return typeof window.getAngularTestability;");
            boolean isAngular = (angularCheck != null && !angularCheck.toString().equals("undefined"));
            if (isAngular) {
                waitForScript("return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1;");
            }
        } catch (WebDriverException ignored) {
        }
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
