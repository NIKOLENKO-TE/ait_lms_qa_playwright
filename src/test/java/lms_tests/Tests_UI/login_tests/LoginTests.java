package lms_tests.Tests_UI.login_tests;

import lms_pages.BasePage;
import lms_pages.UI.LessonsPage;
import lms_pages.UI.LoginPage;
import lms_pages.UI.UserCredentials;
import lms_tests.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    @Param(feature = "Login tests", story = "Test Case #01", testCaseID = "TC-01", testType = "POSITIVE")
    public void LOGIN_STUDENT_CONFIRMED_WITH_NAME_PASSWORD_BY_USERNAME_PASSWORD() {
        loginPage.login("s01@dev-lms.de", "lms-dev-pass-2024", true); // * true
        loginPage.isUserLoggedIn(true);
        basePage.isCurrentPage(LessonsPage.lessonsPageURL(), true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #02", testCaseID = "TC-02", testType = "NEGATIVE")
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #03", testCaseID = "TC-03", testType = "NEGATIVE")
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #04", testCaseID = "TC-04", testType = "NEGATIVE")
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #05", testCaseID = "TC-05", testType = "NEGATIVE")
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_PASS() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_PASS, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #06", testCaseID = "TC-06", testType = "NEGATIVE")
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #07", testCaseID = "TC-07", testType = "POSITIVE")
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_1() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_1, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #08", testCaseID = "TC-08", testType = "POSITIVE")
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_NONE, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #09", testCaseID = "TC-09", testType = "NEGATIVE")
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #10", testCaseID = "TC-10", testType = "POSITIVE")
    public void LOGIN_TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #11", testCaseID = "TC-11", testType = "NEGATIVE")
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #12", testCaseID = "TC-12", testType = "NEGATIVE")
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #13", testCaseID = "TC-13", testType = "NEGATIVE")
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #14", testCaseID = "TC-14", testType = "POSITIVE")
    public void LOGIN_TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #15", testCaseID = "TC-15", testType = "POSITIVE")
    public void LOGIN_TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #16", testCaseID = "TC-16", testType = "NEGATIVE")
    public void LOGIN_ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #17", testCaseID = "TC-17", testType = "NEGATIVE")
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #18", testCaseID = "TC-18", testType = "POSITIVE")
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #19", testCaseID = "TC-19", testType = "POSITIVE")
    public void LOGIN_ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test
    @Param(feature = "Login tests", story = "Test Case #20", testCaseID = "TC-20", testType = "NEGATIVE")
    public void LOGIN_NON_EXISTENT_ACCOUNT() {
        loginPage.login(UserCredentials.NON_EXISTENT_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
}