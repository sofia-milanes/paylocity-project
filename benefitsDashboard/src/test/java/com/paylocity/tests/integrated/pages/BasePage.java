package com.paylocity.tests.integrated.pages;

import com.paylocity.tests.integrated.tests.utils.PaylocityWait;
import org.openqa.selenium.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;

public abstract class BasePage {

    protected PaylocityWait paylocityWait() {
        return new PaylocityWait();
    }

    protected Optional<String> pageUrl() {
        return Optional.empty();
    }
    protected Optional<String> onPageUrl() { return pageUrl(); }
    protected abstract By onPageMarkerLoc();


    public final boolean isOnPage() {
        boolean onPage = true;
        final Optional<String> expectedUrl = onPageUrl();
        if (expectedUrl.isPresent()) {
            onPage = driver().getCurrentUrl().contains(expectedUrl.get());
        }
        onPage &= isElementVisible(onPageMarkerLoc());
        return onPage;
    }

    /***********
     * Helpers *
     ***********/

    public void waitForPageLoaded() {
        paylocityWait().waitUntilStable();
        paylocityWait().For(onPageMarkerLoc());
        assertTrue(isOnPage(), "Cannot find on page marker, or page URL is not a match.");
    }

    public void goToPage() {
        goToPage(false, false);
    }

    public void goToPage(boolean clearStorage, boolean forcePageLoad) {
        if (isOnPage()) {
            if (forcePageLoad) {
                driver().navigate().refresh();
                waitForPageLoaded();
            }
            return;
        }

        if (clearStorage) {
            final JavascriptExecutor js = (JavascriptExecutor) driver();
            js.executeScript("window.localStorage.clear();");
        }

        assertTrue(pageUrl().isPresent(), "Cannot goToPage() without a page URL");
        navigateTo(pageUrl().get(), forcePageLoad);
        waitForPageLoaded();
    }

    /*******
     * Set *
     *******/

    public void enterText(WebElement ele, String text, boolean clearFirst) {
        if (clearFirst) {
            clearText(ele);
        }
        ele.sendKeys(text);
        paylocityWait().waitUntilStable();
    }

    public void enterText(By loc, String text, boolean clearFirst) {
        enterText(paylocityWait().For(loc), text, clearFirst);
    }

    public void click(By loc) {
        click(loc, false);
    }

    public void click(By loc, boolean expectRefresh) {
        paylocityWait().For(loc);
        List<WebElement> eles = driver().findElements(loc);
        assertEquals(eles.size(), 1,
                format("Expected exactly 1 element to click; got %d for loc %s", eles.size(), loc));
        click(eles.get(0), expectRefresh);
    }

    public void click(WebElement ele, boolean expectRefresh) {
        WebElement body = driver().findElement(By.cssSelector("body"));
        JavascriptExecutor js = (JavascriptExecutor) driver();

        try {
            int y = ele.getLocation().y;
            Float yOffset = Float.parseFloat(js.executeScript("return window.pageYOffset;").toString());

            int h = driver().manage().window().getSize().getHeight();
            if (y > h + yOffset.intValue()){
                js.executeScript("window.scrollTo({left: 0, top: arguments[0], behavior: 'instant'});", ele.getLocation().y - 50);
            }
            ele.click();
        } catch (WebDriverException e) {
            js.executeScript("window.scrollTo({left: 0, top: arguments[0], behavior: 'instant'});", ele.getLocation().y - 50);
            try {
                ele.click();
            } catch (RuntimeException e1) {
                fail("Failed trying to click element after scrolling: " + ele, e1);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed trying to click element: " + ele, e);
        }
        if (expectRefresh) {
            paylocityWait().untilElementNotPresent(body);
        }
        paylocityWait().waitUntilStable();
    }

    public void click(WebElement ele) {
        click(ele, false);
    }

    public void selectDropdownOption(By dropdownLocator, String option) {
        click(dropdownLocator);
        click(findByText(By.cssSelector(".oxd-select-option"), option).get());
        click(By.cssSelector("body"));
    }

    public void selectAutocompleteOption (String text, Boolean clearInputBefore) {
        enterText(By.cssSelector("input[placeholder=\"Type for hints...\"]"), text, clearInputBefore);
        paylocityWait().hardwait(3);
        click(By.cssSelector("div.oxd-autocomplete-dropdown div"));
    }

    public Optional<WebElement> findByText(By locator, String text) {
        Predicate<WebElement> filter = e -> e.getText().contains(text);
        return findElementBy(locator, filter);
    }

    public Optional<WebElement> findElementBy(By locator, Predicate<WebElement> filter) {
        List<WebElement> matches = paylocityWait().ForMany(locator, false).stream().filter(filter).collect(toList());
        switch (matches.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(matches.get(0));
            default:
                throw new RuntimeException("findElementBy() got multiple matching elements for locator: " + locator);
        }
    }

    public void navigateTo(String path, boolean pageRefreshes) {
        paylocityWait().waitUntilStable();

        final WebElement body = driver().findElement(By.cssSelector("body"));
        final URL destinationURL = urlForPath(path);

        final WebDriver.Navigation nav = driver().navigate();
        nav.to(destinationURL);

        paylocityWait().waitUntilStable();
        if (pageRefreshes) {
            paylocityWait().untilElementNotPresent(body);
        }
    }

    private static URL urlForPath(String path) {
        try {
            if (path.startsWith("http")) {
                return new URL(path);
            } else {
                URL currentURL = new URL(driver().getCurrentUrl());
                return new URL(
                        currentURL.getProtocol(),
                        currentURL.getHost(),
                        currentURL.getPort(),
                        path);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadPage() {
        driver().navigate().refresh();
        waitForPageLoaded();
    }

    /*******
     * Clear *
     *******/

    public void clearText(WebElement ele) {
        Keys modifierKey = Keys.CONTROL;
        if(System.getProperty("os.name").contains("Mac")) {
            modifierKey = Keys.COMMAND;
        }
        ele.sendKeys(Keys.chord(modifierKey, "a"));
        ele.sendKeys(Keys.BACK_SPACE);
        ele.sendKeys(Keys.ESCAPE);
        paylocityWait().waitUntilStable();
    }

    /************
     * Booleans *
     ************/
    public boolean isElementVisible(By locator) {
        paylocityWait().waitUntilStable();
        try {
            List<WebElement> elements = driver().findElements(locator);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isSuccessMessageOnPage() {
        paylocityWait().hardwait(2);
        return isElementVisible(By.xpath("//p[contains(.,'Success')]"));
    }

    /*******
     * Get *
     *******/

    public String getText(By locator) { return getText(paylocityWait().For(locator)); }

    public String getText(WebElement element) {
        return element.getText().trim();
    }

    public List<String> getTexts(By locator) {
        return getTexts(paylocityWait().ForMany(locator, false));
    }

    public List<String> getTexts(List<WebElement> eles) {
        return eles.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(s -> !asList("choose one", "select").contains(s.toLowerCase()))
                .filter(s -> s.length() > 0)
                .collect(toList());
    }

    public String getLoggedUsername() {
        return getText(By.cssSelector("p.oxd-userdropdown-name"));
    }
}
