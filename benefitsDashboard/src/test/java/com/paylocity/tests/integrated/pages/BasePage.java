package com.paylocity.tests.integrated.pages;

import com.paylocity.tests.integrated.tests.utils.PaylocityWait;
import org.openqa.selenium.*;

import java.util.Optional;

public abstract class BasePage {

    protected PaylocityWait paylocityWait() {
        return new PaylocityWait();
    }

    protected Optional<String> pageUrl() {
        return Optional.empty();
    }
    protected Optional<String> onPageUrl() { return pageUrl(); }
    protected abstract By onPageMarkerLoc();

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
        paylocityWait().waitUntilStable();
    }
}
