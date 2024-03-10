package lms_tests.login_tests;

import io.qameta.allure.Owner;
import lms_pages.BasePage;
import lms_pages.LessonsPage;
import lms_pages.LoginPage;
import lms_pages.UserCredentials;
import lms_tests.TestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static lms_tests.TestBase.TestType.Type.NEGATIVE;
import static lms_tests.TestBase.TestType.Type.POSITIVE;

public class LoginTests extends TestBase {
    public LoginPage loginPage;
    public BasePage basePage;

    @BeforeMethod
    public void init() {
        super.setUp();
        loginPage = new LoginPage(page);
        basePage = new BasePage(page);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-1")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITH_NAME_PASSWORD_BY_USERNAME_PASSWORD() {
        loginPage.login("s01@dev-lms.de", "lms-dev-pass-2024", true); // * true
        loginPage.isUserLoggedIn(true);
        basePage.isCurrentPage(LessonsPage.lessonsPageURL(), true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-2")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_EMAIL, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-3")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_4, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-4")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_WITHOUT_PASS_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-5")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_WITHOUT_PASS() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_WITHOUT_PASS, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-6")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_NONE, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-7")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_1() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_1, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-8")
    @TestType(POSITIVE)
    public void LOGIN_STUDENT_CONFIRMED_PRIMARY_COHORT_NONE() {
        loginPage.login(UserCredentials.STUDENT_CONFIRMED_PRIMARY_COHORT_NONE, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-9")
    @TestType(NEGATIVE)
    public void LOGIN_STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5() {
        loginPage.login(UserCredentials.STUDENT_NOT_CONFIRMED_PRIMARY_COHORT_5, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-10")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-11")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-12")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-13")
    @TestType(NEGATIVE)
    public void LOGIN_TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-14")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_VALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-15")
    @TestType(POSITIVE)
    public void LOGIN_TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.TEACHER_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-16")
    @TestType(NEGATIVE)
    public void LOGIN_ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_NOT_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-17")
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT_WITHOUT_PASSWORD, false); // ! false
        loginPage.isUserLoggedIn(false);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-18")
    @TestType(POSITIVE)
    public void LOGIN_ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITHOUT_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-19")
    @TestType(POSITIVE)
    public void LOGIN_ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT() {
        loginPage.login(UserCredentials.ADMIN_CONFIRMED_WITH_INVALID_ZOOM_ACCOUNT, true); // * true
        loginPage.isUserLoggedIn(true);
    }

    @Test(invocationCount = 1)
    @Owner("Tymofii Nikolenko")
    @TestCaseID("TC-20")
    @TestType(NEGATIVE)
    public void LOGIN_NON_EXISTENT_ACCOUNT() {
        loginPage.login(UserCredentials.NON_EXISTENT_ACCOUNT, false); // ! false
        loginPage.isUserLoggedIn(false);
    }
}