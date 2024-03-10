package lms_pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.fail;

public class LoginPage {
    private final Page page;
    Logger logger = LoggerFactory.getLogger(LoginPage.class);
    public LoginPage(Page page) {
        this.page = page;
        new BasePage(page);
    }

    public static String loginPageURL() {
        return HomePage.homePageURL() + "auth/login";
    }

    public void login(UserCredentials credentials, boolean expectedLoginStatus) {
        // ѕроверка, залогинен ли пользователь
        Locator signOutButton = page.locator("button:has-text('SignOut')");
        boolean userIsLoggedIn = signOutButton.count() > 0;
        if (userIsLoggedIn) {
            // ≈сли пользователь уже залогинен, нажимаем на кнопку 'SignOut'
            signOutButton.first().click();
        }
        boolean isErrorPresent = false;
        page.navigate(HomePage.homePageURL());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
        page.getByPlaceholder("Email address").click();
        if (credentials.getUser_email() == null || credentials.getUser_email().isEmpty()) {
            isErrorPresent = true;
            logger.error("Email address is empty.");
        } else {
            page.fill("//input[@id='email-login-page']", credentials.getUser_email());
        }
        if (page.locator("text=Invalid email format").count() > 0) {
            isErrorPresent = true;
            logger.error("Invalid email format error occurred");
        }
        page.waitForSelector("text=Invalid email format", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();

        if (credentials.getUser_password() == null || credentials.getUser_password().isEmpty()) {
            isErrorPresent = true;
            logger.error("Password is empty.");
        } else {
            page.fill("input[type='password']", credentials.getUser_password());
        }
        if (page.locator("text=The password must be at least").count() > 0) {
            isErrorPresent = true;
            logger.error("Invalid password format error occurred");
        }
        page.waitForSelector("text=The password must be at least", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        try {
            ElementHandle signInButton = page.querySelector("button[label='Sign In']");
            if (signInButton != null && signInButton.isEnabled()) {
                signInButton.press("Enter");
            } else {
                isErrorPresent = true;
                logger.error("Sign In button not found or not enabled.");
            }
        } catch (PlaywrightException e) {
            logger.error("Error occurred: {}", e.getMessage());
        }

        try {
            page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(500));
            Locator errorLocator = page.locator("div").filter(new Locator.FilterOptions().setHasText("ErrorInvalid login or password")).nth(2);
            if (errorLocator.count() > 0) {
                isErrorPresent = true;
                logger.error("USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", credentials.getUser_email(), credentials.getUser_password());
            }
        } catch (TimeoutError e) {
        // System.out.println("USER [" + credentials.getUser_email() + "] is logged in");
        }
        // ѕроверка фактического статуса логина
        boolean actualLoginStatus = !isErrorPresent; // ≈сли ошибка присутствует, значит логин не прошел
        // —равнение фактического статуса с ожидаемым и генераци€ исключени€ при несоответствии
        if (actualLoginStatus != expectedLoginStatus) {
            fail("Login status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + credentials.getUser_email() + "]\nPassword [" + credentials.getUser_password() + "]");
        }
    }
    public void login(String username, String password, boolean expectedLoginStatus) {
        // ѕроверка, залогинен ли пользователь
        Locator signOutButton = page.locator("button:has-text('SignOut')");
        boolean userIsLoggedIn = signOutButton.count() > 0;
        if (userIsLoggedIn) {
            // ≈сли пользователь уже залогинен, нажимаем на кнопку 'SignOut'
            signOutButton.first().click();
        }
        boolean isErrorPresent = false;
        page.navigate(HomePage.homePageURL());
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
        page.getByPlaceholder("Email address").click();
        if (username.isEmpty()) {
            isErrorPresent = true;
            logger.error("Email address is empty.");
        } else {
            page.fill("//input[@id='email-login-page']", username);
        }
        if (page.locator("text=Invalid email format").count() > 0) {
            isErrorPresent = true;
            logger.error("Invalid email format error occurred");
        }
        page.waitForSelector("text=Invalid email format", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();

        if (password.isEmpty()) {
            isErrorPresent = true;
            logger.error("Password is empty.");
        } else {
            page.fill("input[type='password']", password);
        }
        if (page.locator("text=The password must be at least").count() > 0) {
            isErrorPresent = true;
            logger.error("Invalid password format error occurred");
        }
        page.waitForSelector("text=The password must be at least", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        try {
            ElementHandle signInButton = page.querySelector("button[label='Sign In']");
            if (signInButton != null && signInButton.isEnabled()) {
                signInButton.press("Enter");
            } else {
                isErrorPresent = true;
                logger.error("Sign In button not found or not enabled.");
            }
        } catch (PlaywrightException e) {
            logger.error("Error occurred: {}", e.getMessage());
        }

        try {
            page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(500));
            Locator errorLocator = page.locator("div").filter(new Locator.FilterOptions().setHasText("ErrorInvalid login or password")).nth(2);
            if (errorLocator.count() > 0) {
                isErrorPresent = true;
                logger.error("USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", username, password);
            }
        } catch (TimeoutError e) {
//            System.out.println("USER [" + credentials.getUser_email() + "] is logged in");
        }
        // ѕроверка фактического статуса логина
        boolean actualLoginStatus = !isErrorPresent; // ≈сли ошибка присутствует, значит логин не прошел
        // —равнение фактического статуса с ожидаемым и генераци€ исключени€ при несоответствии
        if (actualLoginStatus != expectedLoginStatus) {
            fail("Login status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + username + "]\nPassword [" + password + "]");
        }
    }
    public void isUserLoggedIn(boolean expectedLoginStatus) {
        //page.navigate(LessonsPage.lessonsPageURL());
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
            logger.error(userIsLoggedIn ? "User is logged in." : "User is not logged in.");
            if (userIsLoggedIn != expectedLoginStatus) {
                fail("\nUser logged in status is not expected.\nExpected logged in status: [" + expectedLoginStatus + "]\nActual logged in status: [" + userIsLoggedIn + "]");
            }
        } catch (TimeoutError e) {
            logger.error("Error occurred: {}\nUser is not logged in.", e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}