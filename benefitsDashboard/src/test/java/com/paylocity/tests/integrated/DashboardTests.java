package com.paylocity.tests.integrated;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static com.paylocity.tests.integrated.pages.PageFactory.loginPage;
import static org.testng.Assert.assertEquals;


public class DashboardTests {
    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get("https://wmxrwq14uc.execute-api.us-east-1.amazonaws.com/Prod/Account/Login");
        WebElement userNameField = driver.findElement(By.name("Username"));
        userNameField.sendKeys("TestUser793");
        WebElement passwordField = driver.findElement(By.name("Password"));
        passwordField.sendKeys("&os!|@r^Su2}");
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.submit();

        // loginPage().doLogin("TestUser793", "&os!|@r^Su2}");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void addEmployee() {
        // GIVEN an Employer
        // AND I am on the Benefits Dashboard page
        assertEquals(driver.getTitle(), "Employees - Paylocity Benefits Dashboard");

        // WHEN I select Add Employee
        WebElement addEmployeeButton = driver.findElement(By.cssSelector("button[id='add']"));
        addEmployeeButton.click();

        // THEN I should be able to enter employee details
        WebElement firstNameField = driver.findElement(By.cssSelector("input[name='firstName']"));
        firstNameField.sendKeys("John");

        WebElement lastNameField = driver.findElement(By.cssSelector("input[name='lastName']"));
        lastNameField.sendKeys("Bowie");

        WebElement dependentsField = driver.findElement(By.cssSelector("input[name='dependants']"));
        dependentsField.sendKeys("1");

        WebElement addButton = driver.findElement(By.cssSelector("button[id='addEmployee']"));
        addButton.click();

        // AND the employee should save
        List<WebElement> lastNameInTable = driver.findElements(By.cssSelector("td:nth-of-type(2)"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeButton));

        // TODO this re-work because the bug BUG-UI-02
        assertEquals("John", lastNameInTable.get(0).getText());
    }

    @Test
    public void updateEmployee() {
        // GIVEN an Employer
        // AND I am on the Benefits Dashboard page
        assertEquals(driver.getTitle(), "Employees - Paylocity Benefits Dashboard");

        // AND I Add an Employee
        WebElement addEmployeeButton = driver.findElement(By.cssSelector("button[id='add']"));
        addEmployeeButton.click();

        WebElement firstNameField = driver.findElement(By.cssSelector("input[name='firstName']"));
        firstNameField.sendKeys("John");

        WebElement lastNameField = driver.findElement(By.cssSelector("input[name='lastName']"));
        lastNameField.sendKeys("Bowie");

        WebElement dependentsField = driver.findElement(By.cssSelector("input[name='dependants']"));
        dependentsField.sendKeys("1");

        WebElement addButton = driver.findElement(By.cssSelector("button[id='addEmployee']"));
        addButton.click();

        // WHEN I select the Action Edit
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("i.fa-edit")));
        WebElement editButton = driver.findElement(By.cssSelector("i.fa-edit"));
        editButton.click();

        // THEN I can edit employee details
        firstNameField.sendKeys("Edited-John");
        WebElement updateButton = driver.findElement(By.cssSelector("button[id='updateEmployee']"));
        updateButton.click();

        // AND the data should change in the table
        List<WebElement> lastNameInTable = driver.findElements(By.cssSelector("td:nth-of-type(2)"));
        // TODO this re-work because the bug BUG-UI-02
        assertEquals("Edited-John", lastNameInTable.get(0).getText());
    }

    @Test
    public void deleteEmployee() {
        // GIVEN an Employer
        // AND I am on the Benefits Dashboard page
        assertEquals(driver.getTitle(), "Employees - Paylocity Benefits Dashboard");

        // AND I Add an Employee
        WebElement addEmployeeButton = driver.findElement(By.cssSelector("button[id='add']"));
        addEmployeeButton.click();

        WebElement firstNameField = driver.findElement(By.cssSelector("input[name='firstName']"));
        firstNameField.sendKeys("John");

        WebElement lastNameField = driver.findElement(By.cssSelector("input[name='lastName']"));
        lastNameField.sendKeys("Bowie");

        WebElement dependentsField = driver.findElement(By.cssSelector("input[name='dependants']"));
        dependentsField.sendKeys("1");

        WebElement addButton = driver.findElement(By.cssSelector("button[id='addEmployee']"));
        addButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("i.fa-times")));

        // WHEN I click the Action X
        WebElement deleteButton = driver.findElement(By.cssSelector("i.fa-times"));
        deleteButton.click();

        // AND I confirm the deletion
        WebElement confirmDeleteButton = driver.findElement(By.cssSelector("button[id='deleteEmployee']"));
        confirmDeleteButton.click();

        //THEN the employee should be deleted
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("i.fa-times")));

        WebElement employeesTable = driver.findElement(By.cssSelector("table[id='employeesTable']>tbody>tr>td"));
        assertEquals("No employees found.", employeesTable.getText());
    }
}
