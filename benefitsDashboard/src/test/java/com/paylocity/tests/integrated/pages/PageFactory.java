package com.paylocity.tests.integrated.pages;

public class PageFactory {
    private static LoginPage _loginPage;
    private static DashboardPage _dashboardPage;

    static {
        _loginPage = new LoginPage();
        _dashboardPage = new DashboardPage();
    }

    public static LoginPage loginPage() {
        return _loginPage;
    }
    public static DashboardPage
    dashboardPage() {
        return _dashboardPage;
    }

}
