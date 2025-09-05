package com.paylocity.tests.integrated.driver;

import com.paylocity.tests.integrated.tests.utils.TestingContext;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.Optional;

import static com.paylocity.tests.integrated.driver.DriverType.CHROME;
import static java.util.Optional.empty;
import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.internal.Utils.log;

public class DriverFactory {
    private static ThreadLocal<DriverFactory> _instance = ThreadLocal.withInitial(DriverFactory::new);
    private WebDriver webDriver = null;
    private DriverType driverType = null;
    private String siteAddress = null;
    public static DriverFactory instance() {
        return _instance.get();
    }
    public static boolean isInitialized() {
        return instance().webDriver != null;
    }
    private final static DriverType defaultDriverType() {
        return CHROME;
    }
    private DriverFactory() {
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    public void run() {
                        destroyDriver();
                    }
                }
        );
    }

    public static WebDriver driver() {
        WebDriver d = instance().cachedGet();
        assertNotNull(d);
        return d;
    }

    public void ensureConfig() {
        ensureConfig(empty(), empty());
    }

    public void ensureConfig(String driverType, String siteAddress) {
        ensureConfig(Optional.ofNullable(DriverType.valueOf(driverType.trim().toUpperCase())),
                Optional.ofNullable(siteAddress));
    }

    public synchronized void ensureConfig(Optional<DriverType> driverType, Optional<String> siteAddress) {
        if (isInitialized()) {
            return;
        }
        this.driverType = driverType.orElse(defaultDriverType());
        this.siteAddress = siteAddress.orElse(TestingContext.appUrl);
    }

    public WebDriver cachedGet() {
        if (webDriver != null) {
            ensureWindow();
            return webDriver;
        }
        webDriver = createDriver();
        visitSite();

        return webDriver;
    }

    WebDriver createDriver() {
        ensureConfig();
        log("Creating driver");
        try {
            WebDriver d = driverType.createDriver();
            log("Created driver");
            return d;
        } catch (RuntimeException e) {
            log("Failed to create driver");
            throw e;
        }
    }
    private void ensureWindow() {
        try {
            if (webDriver.getWindowHandle() != null) {
                return;
            }
        } catch (NoSuchWindowException e) {
        }
        try {
            Collection<String> tabs = webDriver.getWindowHandles();
            if (!tabs.isEmpty()) {
                webDriver.switchTo().window(tabs.iterator().next());
            }
        } catch (RuntimeException e) {
            fail("exception in DriverFactory.ensureWindow()", e);
        }
    }

    void visitSite() {
        webDriver.get(siteAddress);
        try {
            webDriver.manage().window().maximize();
            Dimension d = webDriver.manage().window().getSize();
            webDriver.manage().window().setSize(new Dimension(d.getWidth() + 100, d.getHeight()));
        } catch (RuntimeException e) {
        }
    }

    public void destroyDriver() {
        if (webDriver == null) {
            return;
        }
        try {
            webDriver.quit();
            log("Destroyed driver");
        } catch (RuntimeException e) {
        }
        webDriver = null;
    }
}
