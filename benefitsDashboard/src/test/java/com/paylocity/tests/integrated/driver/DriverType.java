package com.paylocity.tests.integrated.driver;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public enum DriverType {
    FIREFOX {
        @Override
        WebDriver createDriver() {
            FirefoxOptions options = new FirefoxOptions();
            WebDriver driver = new FirefoxDriver(options);
            driver.manage().timeouts().pageLoadTimeout(24, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
            return driver;
        }
    },

    CHROME {
        @Override
        WebDriver createDriver() {
            boolean isWin = System.getProperty("os.name").contains("Windows");
            setDriverPropertyIfUnset("webdriver.chrome.driver", driverDir + "chromedriver" + (isWin ? ".exe" : ""));
            HashMap<String, Object> chromePref = new HashMap<String, Object>();
            chromePref.put("profile.default_content_settings.popups", 0);
            chromePref.put("download.default_directory", System.getProperty("user.dir") + "/src/downloads");
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePref);
            //setLoggingCapabilities(options);
            determineHeadless(options);
            options.addArguments("--ignore-certificate-errors", "--window-size=1920,1080", "--remote-allow-origins=*");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().timeouts().getImplicitWaitTimeout();
            return driver;
        }
    },

    REMOTE {
        @Override
        WebDriver createDriver() {
            WebDriver driver = determineRemoteDriver();
            driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
            return driver;
        }

        private WebDriver determineRemoteDriver() {
            Optional<Configuration> conf = findRemoteConfig();
            assertTrue(conf.isPresent());
            if (conf.get().browser.equals("CHROME")) {
                final ChromeOptions options = new ChromeOptions();
                options.addArguments(
                        "--incognito",
                        "--no-network-proxy",
                        "--window-size=1920,1080"
                );
                options.setAcceptInsecureCerts(true);
            }
            try {
            } catch (UnreachableBrowserException e) {
                fail(e.getMessage());
            }
            fail("No appropriate remote driver found.");
            return null;
        }
    };

    private static final String driverDir = "src/test/java/com/paylocity/tests/integrated/tests/drivers/";

    abstract WebDriver createDriver();

    private static void setDriverPropertyIfUnset(String key, String absolutePath) {
        String value = System.getProperty(key);
        if (isNull(value)) {
            value = absolutePath;
            System.setProperty(key, value);
        }
        assertTrue(new File(value).exists(), "Driver does not exist at: " + value);
    }

    private static void setLoggingCapabilities(MutableCapabilities capabilities) {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.SEVERE);
        capabilities.setCapability("loggingPrefs", logPrefs);
    }
    private static void determineHeadless(ChromeOptions options) {
        String headless = System.getenv("HEADLESS");
        if(headless == null || Boolean.valueOf(headless)) {
            options.addArguments("--headless");
        }
    }

    protected static class Configuration {
        public final String browser, os;
        public URL remoteAddress = null;

        Configuration(String browser, String os) {
            this.browser = browser;
            this.os = os;
        }

        public String key() {
            return format("REMOTE_%s_%s", os, browser);
        }
    }

    public static Optional<Configuration> findRemoteConfig() {
        final String[] OSTypes = {"LINUX", "DARWIN", "WIN"};
        final String[] BRTypes = {"CHROME", "IE9", "FF"};
        for (String os : OSTypes) {
            for (String browser : BRTypes) {
                final Configuration conf = new Configuration(browser, os);
                final String val = System.getenv(conf.key());
                if (!val.isEmpty()) {
                    try {
                        conf.remoteAddress = new URL(val.trim());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    return Optional.of(conf);
                }
            }
        }
        return empty();
    }
}
