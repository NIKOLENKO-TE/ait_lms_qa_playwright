package lms_tests.Tests_UI.login_tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import lms_pages.BasePage;
import lms_pages.UI.LessonsPage;
import lms_pages.UI.LoginPage;
import lms_pages.UI.UserCredentials;
import lms_tests.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static lms_tests.BaseTest.TestType.Type.NEGATIVE;
import static lms_tests.BaseTest.TestType.Type.POSITIVE;

public class LoginTests extends BaseTest {
    public LoginPage loginPage;
    public BasePage basePage;

    @BeforeMethod
    public void init() {
        super.setUp();
        loginPage = new LoginPage(page);
        basePage = new BasePage(page);
    }

    @Test
    @Feature("Login tests")
    @Story("Test Case #01")
    @TestCaseID("TC-1")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITH_NAME_PASSWORD_BY_USERNAME_PASSWORD() {
        loginPage.login("s01@dev-lms.de", "lms-dev-pass-2024", !true); // * true
        loginPage.isUserLoggedIn(true);
        basePage.isCurrentPage(LessonsPage.lessonsPageURL(), true);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #02")
    @TestCaseID("TC-02")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #03")
    @TestCaseID("TC-03")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Feature("Login tests")
    @Story("Test Case #04")
    @TestCaseID("TC-04")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #05")
    @TestCaseID("TC-05")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_PASS() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_PASS, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #06")
    @TestCaseID("TC-06")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #07")
    @TestCaseID("TC-07")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_1() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_1, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #08")
    @TestCaseID("TC-08")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_NONE, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @Feature("Login tests")
    @Story("Test Case #09")
    @TestCaseID("TC-09")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-10")
    @Feature("Login tests")
    @Story("Test Case #10")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @TestCaseID("TC-11")
    @Feature("Login tests")
    @Story("Test Case #11")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-12")
    @Feature("Login tests")
    @Story("Test Case #12")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-13")
    @Feature("Login tests")
    @Story("Test Case #13")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-14")
    @Feature("Login tests")
    @Story("Test Case #14")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @TestCaseID("TC-15")
    @Feature("Login tests")
    @Story("Test Case #15")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @TestCaseID("TC-16")
    @Feature("Login tests")
    @Story("Test Case #16")
    @TestType(NEGATIVE)
    public void LOGIN_ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-17")
    @Feature("Login tests")
    @Story("Test Case #17")
    @TestType(NEGATIVE)
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
    @Test
    @TestCaseID("TC-18")
    @Feature("Login tests")
    @Story("Test Case #18")
    @TestType(POSITIVE)
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @TestCaseID("TC-19")
    @Feature("Login tests")
    @Story("Test Case #19")
    @TestType(POSITIVE)
    public void LOGIN_ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }
    @Test
    @TestCaseID("TC-20")
    @Feature("Login tests")
    @Story("Test Case #20")
    @TestType(NEGATIVE)
    public void LOGIN_NON_EXISTENT_ACCOUNT() {
        loginPage.login(UserCredentials.NON_EXISTENT_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
}