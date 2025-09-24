package com.paylocity.tests.integrated.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

import java.time.Duration;
import java.util.HashMap;

import static java.util.Objects.isNull;
import static org.testng.Assert.assertTrue;

public enum DriverType {
    CHROME {
        @Override
        WebDriver createDriver() {
            String driverDir = "src/test/java/com/paylocity/tests/integrated/tests/drivers/";
            setDriverPropertyIfUnset(driverDir + "chromedriver");
            ChromeOptions options = new ChromeOptions();
            HashMap<String, Object> chromePref = new HashMap<>();
            chromePref.put("download.default_directory", System.getProperty("user.dir") + "/src/downloads");
            options.setExperimentalOption("prefs", chromePref);
            determineHeadless(options);
            options.addArguments("--ignore-certificate-errors", "--window-size=1920,1080", "--remote-allow-origins=*");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(24));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
            return driver;
        }
    };

    abstract WebDriver createDriver();

    private static void setDriverPropertyIfUnset(String absolutePath) {
        String value = System.getProperty("webdriver.chrome.driver");
        if (isNull(value)) {
            value = absolutePath;
        }
        System.setProperty("webdriver.chrome.driver", value);
        assertTrue(new File(value).exists(), "Driver does not exist at: " + value);
    }

    private static void determineHeadless(ChromeOptions options) {
        String headless = System.getenv("HEADLESS");
        if(headless == null || Boolean.parseBoolean(headless)) {
            options.addArguments("--headless");
        }
    }
}
