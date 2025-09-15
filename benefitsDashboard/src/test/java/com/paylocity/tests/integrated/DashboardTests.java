package com.paylocity.tests.integrated;

import org.openqa.selenium.WebDriver;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.paylocity.tests.integrated.pages.PageFactory.dashboardPage;
import static com.paylocity.tests.integrated.pages.PageFactory.loginPage;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;


public class DashboardTests {
    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // GIVEN an Employer
        // AND I am on the Benefits Dashboard page
        loginPage().doLogin("TestUser793", "&os!|@r^Su2}");
    }

    @AfterMethod
    public void tearDown() {
        dashboardPage().deleteAllEmployees();
        dashboardPage().clickLogOutButton();
        if (driver != null) driver.quit();
    }

    @Test
    public void addEmployee() {
        // WHEN I select Add Employee
        dashboardPage().clickAddEmployeeButton();

        // THEN I should be able to enter employee details
        dashboardPage().enterEmployeeDetails("John", "Bowie", 1);
        dashboardPage().clickAddButton();

        // AND the employee should save
        List<String> lastNamesInTable = dashboardPage().getAllEmployeesLastName();
        assertTrue(lastNamesInTable.contains("John"));
    }

    @Test
    public void updateEmployee() {
        // AND I Add an Employee
        String firstName = "Sam";
        dashboardPage().clickAddEmployeeButton();
        dashboardPage().enterEmployeeDetails(firstName, "Thomson", 1);
        dashboardPage().clickAddButton();

        // WHEN I select the Action Edit
        dashboardPage().clickEditButton(firstName);

        // THEN I can edit employee details
        String editedFirstName = "edited-Sam";
        dashboardPage().editFirstname(editedFirstName);
        dashboardPage().clickUpdateButton();

        // TODO this re-work because the bug BUG-UI-02
        // AND the data should change in the table
        List<String> lastNamesInTable = dashboardPage().getAllEmployeesLastName();
        assertTrue(lastNamesInTable.contains(editedFirstName));
    }

    @Test
    public void deleteEmployee() {
        // AND I Add an Employee
        String firstName = "John";
        dashboardPage().clickAddEmployeeButton();
        dashboardPage().enterEmployeeDetails(firstName, "Bowie", 1);
        dashboardPage().clickAddButton();

        // WHEN I click the Action X
        dashboardPage().clickDeleteButtonByFirstName(firstName);

        // AND I confirm the deletion
        dashboardPage().clickConfirmDeleteButton();

        //THEN the employee should be deleted
        assertFalse(dashboardPage().getAllEmployeesLastName().contains("John"));
    }
}
