package com.paylocity.tests.integrated.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.paylocity.tests.integrated.driver.DriverFactory.driver;
import static java.lang.String.format;

public class DashboardPage extends BasePage {
    private static final By addEmployeeButton = By.cssSelector("button[id='add']");
    private static final By firstNameField = By.cssSelector("input[name='firstName']");
    private static final By lastNameField = By.cssSelector("input[name='lastName']");
    private static final By dependentsField = By.cssSelector("input[name='dependants']");

    @Override
    protected By onPageMarkerLoc() {
        return addEmployeeButton;
    }

    public void enterEmployeeDetails(String firstname, String lastname, int dependents) {
        enterFirstname(firstname);
        enterLastName(lastname);
        enterDependents(dependents);
    }

    /***********
     * Actions *
     ***********/
    public void enterFirstname(String firstname) {
        enterText(firstNameField, firstname, false);
    }

    public void editFirstname(String firstname) {
        WebElement inputFirstName = driver().findElement(firstNameField);
        clearText(inputFirstName);
        enterText(firstNameField, firstname, false);
    }

    public void enterLastName(String lastName) {
        enterText(lastNameField, lastName, false);
    }

    public void enterDependents(int dependents) {
        enterText(dependentsField, String.valueOf(dependents), false);
    }

    public void clickAddEmployeeButton() {
        WebElement addEmployeeBtn = paylocityWait().waitForElementToBeClickable(addEmployeeButton);
        addEmployeeBtn.click();
    }

    public void clickAddButton() {
        WebElement addButton = driver().findElement(By.cssSelector("button[id='addEmployee']"));
        addButton.click();
        paylocityWait().hardwait(3);
    }

    public void clickUpdateButton() {
        WebElement updateButton = driver().findElement(By.cssSelector("button[id='updateEmployee']"));
        updateButton.click();
        paylocityWait().hardwait(3);
    }

    public List<String> getAllEmployeesLastName() {
        return driver().findElements(By.cssSelector("td:nth-of-type(2)")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public void deleteAllEmployees() {
        while (!getAllEmployeesLastName().isEmpty()) {
            clickDeleteButton();
        }
    }

    public void clickDeleteButtonByFirstName(String lastName) {
        WebElement deleteButton = driver().findElement(By.xpath(format("//tr[td[contains(.,'%s')]]//i[contains(@class, 'fa-times')]", lastName)));
        deleteButton.click();
    }

    public void clickDeleteButton() {
        List<WebElement> deleteButtons = driver().findElements(By.cssSelector("i.fa-times"));
        if (!deleteButtons.isEmpty()) {
            deleteButtons.getFirst().click();
            clickConfirmDeleteButton();
        } else {
            System.out.println("No elements found");
        }
    }

    public void clickConfirmDeleteButton() {
        WebElement confirmDeleteButton = driver().findElement(By.cssSelector("button[id='deleteEmployee']"));
        confirmDeleteButton.click();
        paylocityWait().hardwait(3);
    }

    public void clickEditButton(String lastName) {
        WebElement editButton = driver().findElement(By.xpath(format("//tr[td[contains(.,'%s')]]//i[contains(@class, 'fa-edit')]", lastName)));
        editButton.click();
    }

    public void clickLogOutButton() {
        WebElement logOutButton = driver().findElement(By.cssSelector("a[href*='/Prod/Account/LogOut']"));
        logOutButton.click();
    }
}
