package lms_pages.UI;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import lms_pages.BaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;

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

    public void login(Object credentials, boolean expectedLoginStatus) {
        if (credentials instanceof UserCredentials userCredentials) {
            loginMethod(userCredentials.getUser_email(), userCredentials.getUser_password(), expectedLoginStatus);
        } else if (credentials instanceof String[] loginInfo) {
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
                    userIsLoggedIn = false; // кнопка "LOGIN" найдена если пользователь не залогинен
                }
                if (signOutButton.count() > 0) {
                    userIsLoggedIn = true; // кнопка "USER" найдена если пользователь залогинен
                }
                logger.error("[{}]: {}", methodName, userIsLoggedIn ? "User is logged in." : "User is not logged in.");
                if (userIsLoggedIn != expectedLoginStatus) {
                    fail("\nUser logged in status is not expected.\nExpected logged in status: [" + expectedLoginStatus + "]\nActual logged in status: [" + userIsLoggedIn + "]");
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
//    public void loginMethod(String username, String password, boolean expectedLoginStatus) {
//        // Проверка, залогинен ли пользователь
//        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        Locator signOutButton = page.locator("button:has-text('SignOut')");
//        boolean userIsLoggedIn = signOutButton.count() > 0;
//        if (userIsLoggedIn) {
//            // Если пользователь уже залогинен, нажимаем на кнопку 'SignOut'
//            signOutButton.first().click();
//        }
//        boolean isErrorPresent = false;
//        if (!page.url().equals(HomePage.homePageURL())) {
//            page.navigate(HomePage.homePageURL());
//        }
//        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
//        page.getByPlaceholder("Email address").click();
//        if (username == null || username.isEmpty()) {
//            isErrorPresent = true;
//            logger.error("[{}]: Email address is empty.", methodName);
//        } else {
//            page.fill("//input[@id='email-login-page']", username);
//        }
//        if (page.locator("text=Invalid email format").count() > 0) {
//            isErrorPresent = true;
//            logger.error("[{}]: Invalid email format error occurred", methodName);
//        }
//        page.waitForSelector("text=Invalid email format", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
//        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
//
//        if (password == null || password.isEmpty()) {
//            isErrorPresent = true;
//            logger.error("[{}]: Password is empty.", methodName);
//        } else {
//            page.fill("input[type='password']", password);
//        }
//        if (page.locator("text=The password must be at least").count() > 0) {
//            isErrorPresent = true;
//            logger.error("[{}]: Invalid password format error occurred", methodName);
//        }
//        page.waitForSelector("text=The password must be at least", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
//        try {
//            ElementHandle signInButton = page.querySelector("button[label='Sign In']");
//            if (signInButton != null && signInButton.isEnabled()) {
//                signInButton.press("Enter");
//            } else {
//                isErrorPresent = true;
//                logger.error("[{}]: Sign In button not found or not enabled.", methodName);
//            }
//        } catch (PlaywrightException e) {
//            logger.error("[{}]: Error occurred: {}", methodName, e.getMessage());
//        }
//
//        try {
//            page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(500));
//            Locator errorLocator = page.locator("div").filter(new Locator.FilterOptions().setHasText("ErrorInvalid login or password")).nth(2);
//            if (errorLocator.count() > 0) {
//                isErrorPresent = true;
//                logger.error("[{}]: USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", methodName, username, password);
//            }
//        } catch (TimeoutError e) {
//            // System.out.println("USER [" + credentials.getUser_email() + "] is logged in");
//        }
//        // Проверка фактического статуса логина
//        boolean actualLoginStatus = !isErrorPresent; // Если ошибка присутствует, значит логин не прошел
//        // Сравнение фактического статуса с ожидаемым и генерация исключения при несоответствии
//        if (actualLoginStatus != expectedLoginStatus) {
//            logger.error("[{}]: Login status is not as expected. Expected login status: [{}]. Error is present on Login Page?: [{}]. User [{}]. Password [{}]", methodName, expectedLoginStatus, !actualLoginStatus, username, password);
//            fail("Login status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + username + "]\nPassword [" + password + "]");
//        }
//    }

//    public void isUserLoggedIn(boolean expectedLoginStatus) {
//        //page.navigate(LessonsPage.lessonsPageURL());
//        Locator loginButton = page.locator("button:has-text('Login')");
//        Locator signOutButton = page.locator("button:has-text('SignOut')");
//        boolean userIsLoggedIn = signOutButton.count() > 0;
//        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        try {
//            if (loginButton.count() > 0) {
//                userIsLoggedIn = false; // кнопка "LOGIN" найдена если пользователь не залогинен
//            }
//            if (signOutButton.count() > 0) {
//                userIsLoggedIn = true; // кнопка "USER" найдена если пользователь залогинен
//            }
//            logger.error("[{}]: {}", methodName, userIsLoggedIn ? "User is logged in." : "User is not logged in.");
//            if (userIsLoggedIn != expectedLoginStatus) {
//                fail("\nUser logged in status is not expected.\nExpected logged in status: [" + expectedLoginStatus + "]\nActual logged in status: [" + userIsLoggedIn + "]");
//            }
//        } catch (TimeoutError e) {
//            logger.error("{}: Error occurred: [{}]\nUser is not logged in.", methodName, e.getMessage());
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            logger.error("[{}]: An unexpected error occurred: {}", methodName, e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}