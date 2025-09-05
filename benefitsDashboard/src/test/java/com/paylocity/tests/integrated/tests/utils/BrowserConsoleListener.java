package com.paylocity.tests.integrated.tests.utils;

import com.paylocity.tests.integrated.driver.DriverFactory;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;

public class BrowserConsoleListener implements IInvokedMethodListener {
    public void beforeInvocation(IInvokedMethod var1, ITestResult var2){

    }

    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        ConstructorOrMethod constructor = method.getTestMethod().getConstructorOrMethod();
        IgnoreConsoleErrors disable = constructor.getMethod().getAnnotation(IgnoreConsoleErrors.class);
        if (disable != null) {
            return;
        }
        if (!DriverFactory.isInitialized()) {
            return;
        }
        LogEntries logEntries = driver().manage().logs().get(LogType.BROWSER);
        if (!containsValidErrors(logEntries)) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== Begin Browser Logs for test method: " + result.getMethod().getMethodName() + " ======\n");
        for (LogEntry entry : logEntries) {
            sb.append(entry.getLevel().toString() + " - " + entry.getMessage() + "\n");
        }
        sb.append("===== End Browser Logs =====\n");
        System.out.println(sb);

        ITestContext context = result.getTestContext();
        context.getPassedTests().removeResult(result);
        context.getPassedTests().getAllMethods().remove(method.getTestMethod());
        result.setStatus(ITestResult.FAILURE);
        result.setThrowable(new Exception("Browser logged an error: " + sb.toString()));
        context.getFailedTests().addResult(result); //, method.getTestMethod()
    }

    private static boolean containsValidErrors(LogEntries logEntries) {
        return logEntries.getAll().stream().anyMatch(logEntry -> !logEntry.getMessage().contains("favicon") && !logEntry.getMessage().contains("by2.uservoice"));
    }
}
