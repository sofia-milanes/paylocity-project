package com.paylocity.tests.integrated.pages;

import org.openqa.selenium.By;

public class DashboardPage extends BasePage {
    private By addEmployeeButton = By.cssSelector("button[id='add']");
    private By firstNameField = By.cssSelector("input[name='firstName']");
    private By lastNameField = By.cssSelector("input[name='lastName']");
    private By dependentsField = By.cssSelector("input[name='dependants']");
    private By addButton = By.cssSelector("button[id='addEmployee']");
    private By deleteButton = By.cssSelector("i.fa-times");
    private By confirmDeleteButton = By.cssSelector("button[id='deleteEmployee']");
    private By employeesTable = By.cssSelector("table[id='employeesTable']");

    @Override
    protected By onPageMarkerLoc() {
        return addEmployeeButton;
    }

    public void addEmployee() {
        click(addEmployeeButton);
    }

    /***********
     * Actions *
     ***********/
    public void enterFirstname(String firstname) {
        enterText(firstNameField, firstname, false);
    }

    public void lastNameField(String lastName) {
        enterText(lastNameField, lastName, false);
    }

    public void enterDependents(int dependents) {
        enterText(dependentsField, String.valueOf(dependents), false);
    }

    public void clickAddButton() {
        click(addButton);
    }

    public void clickDeleteButton() {
        click(deleteButton);
    }

    public void clickConfirmDeleteButton() {
        click(confirmDeleteButton);
    }
}
