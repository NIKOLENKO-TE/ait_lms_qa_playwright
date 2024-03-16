package lms_pages.UI;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import lms_pages.BaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class LoginPage {

    private final Page page;
    BaseHelper baseHelper;
    HomePage homePage;

    public static String loginPageURL() {
        return HomePage.homePageURL() + "auth/login";
    }

    Logger logger = LoggerFactory.getLogger(LoginPage.class);

    public LoginPage(Page page) {
        this.page = page;
        this.baseHelper = new BaseHelper(page);
        this.homePage = new HomePage(page);
    }

    public void login(String username, String password, boolean expectedLoginStatus) {
        login(new UserCredentials(username, password), expectedLoginStatus);
    }

//    public void login(Object credentials, boolean expectedLoginStatus) {
//        if (credentials instanceof UserCredentials userCredentials) {
//            loginMethod(userCredentials.getUser_email(), userCredentials.getUser_password(), expectedLoginStatus);
//        } else if (credentials instanceof String[] loginInfo) {
//            if (loginInfo.length >= 2) {
//                loginMethod(loginInfo[0], loginInfo[1], expectedLoginStatus);
//            } else {
//                logger.error("Insufficient login information provided.");
//            }
//        } else {
//            logger.error("Unsupported credential type.");
//        }
//    }
public void login(Object credentials, boolean expectedLoginStatus) {
    if (credentials instanceof UserCredentials) {
        UserCredentials userCredentials = (UserCredentials) credentials;
        loginMethod(userCredentials.getUser_email(), userCredentials.getUser_password(), expectedLoginStatus);
    } else if (credentials instanceof String[]) {
        String[] loginInfo = (String[]) credentials;
        if (loginInfo.length >= 2) {
            loginMethod(loginInfo[0], loginInfo[1], expectedLoginStatus);
        } else {
            logger.error("Insufficient login information provided.");
        }
    } else {
        logger.error("Unsupported credential type.");
    }
}
    public void loginMethod(String username, String password, boolean expectedLoginStatus) {
        baseHelper.loginVariables();
        baseHelper.checkIfUserIsLoggedIn(baseHelper.signOutButton);
        homePage.navigateToHomePage();
        baseHelper.fillEmail(username, baseHelper.methodName);
        baseHelper.fillPassword(password, baseHelper.methodName);
        baseHelper.clickSignInButton(username, password, baseHelper.methodName);
        baseHelper.checkLoginStatus(baseHelper.errorLocator, baseHelper.isErrorPresent, username, password, expectedLoginStatus, baseHelper.methodName);
    }

    public void isUserLoggedIn(boolean expectedLoginStatus) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Allure.step("Check if user is logged in", () -> {
            Locator loginButton = page.locator("button:has-text('Login')");
            Locator signOutButton = page.locator("button:has-text('SignOut')");
            boolean userIsLoggedIn = signOutButton.count() > 0;
            try {
                if (loginButton.count() > 0) {
                    userIsLoggedIn = false; // "LOGIN" button found if user is not logged in
                }
                if (signOutButton.count() > 0) {
                    userIsLoggedIn = true; // "USER" button found if user is logged in
                }
                logger.error("[{}]: {}", methodName, userIsLoggedIn ? "User is logged in." : "User is not logged in.");
                if (userIsLoggedIn != expectedLoginStatus) {
                    Assert.fail("\nUser logged in status is not expected.\nExpected logged in status: [" + expectedLoginStatus + "]\nActual logged in status: [" + userIsLoggedIn + "]");
                }
            } catch (TimeoutError e) {
                logger.error("{}: Error occurred: [{}]\nUser is not logged in.", methodName, e.getMessage());
                throw new RuntimeException(e);
            } catch (Exception e) {
                logger.error("[{}]: An unexpected error occurred: {}", methodName, e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
}