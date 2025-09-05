//package com.paylocity.tests.integrated;
//
//import com.paylocity.tests.integrated.driver.DriverFactory;
//import com.paylocity.tests.integrated.tests.utils.BrowserConsoleListener;
//import org.testng.ITestResult;
//import org.testng.annotations.*;
//
//import static org.testng.Assert.fail;
//import static org.testng.AssertJUnit.assertTrue;
//
//@Listeners(BrowserConsoleListener.class)
//public class PaylocityTest {
//    @Parameters({"Browser", "SiteAddress"})
//    @BeforeSuite(alwaysRun = true)
//    public void paylocityTestSuiteSetup(
//            @Optional final String driverType,
//            @Optional final String siteAddress
//    ) {
//        DriverFactory.instance().ensureConfig("chrome", siteAddress);
//    }
//
//    @AfterMethod(alwaysRun = true)
//    public void paylocityTestTearDown(ITestResult result) {
//        try {
//            DriverFactory.instance().destroyDriver();
//        } catch (RuntimeException e) {
//            fail("Failed to destroy driver: " + e.toString());
//        }
//    }
//
//    @Test
//    public void shouldAnswerWithTrue()
//    {
//        assertTrue( true );
//    }
//}
