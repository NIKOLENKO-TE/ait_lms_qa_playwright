package lms_pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;
import lms_pages.UI.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.fail;

public class BaseHelper extends BasePage{
    public String methodName;
    public Locator signOutButton;
    public Locator errorLocator;
    public AtomicBoolean isErrorPresent;

    public BaseHelper(Page page) {
        super(page);
    }

    public void loginVariables() {
        methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        signOutButton = page.locator("button:has-text('SignOut')");
        errorLocator = page.locator("div").filter(new Locator.FilterOptions().setHasText("ErrorInvalid login or password")).nth(2);
        isErrorPresent = new AtomicBoolean(false);
    }

    Logger logger = LoggerFactory.getLogger(LoginPage.class);
    public void checkIfUserIsLoggedIn(Locator signOutButton) {
        Allure.step("Check if user is already logged in", () -> {
            boolean userIsLoggedIn = signOutButton.count() > 0;
            if (userIsLoggedIn) {
                signOutButton.first().click();
            }
        });
    }

    public void fillEmail(String username, String methodName) {
        Allure.step("Fill in Email address", () -> {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();
            page.getByPlaceholder("Email address").click();
            if (username == null || username.isEmpty()) {
                logger.error("[{}]: Email address is empty.", methodName);
            } else {
                page.fill("//input[@id='email-login-page']", username);
            }
            if (page.locator("text=Invalid email format").count() > 0) {
                logger.error("[{}]: Invalid email format error occurred", methodName);
            }
            page.waitForSelector("text=Invalid email format", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        });
    }

    public void fillPassword(String password, String methodName) {
        Allure.step("Fill in Password", () -> {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
            if (password == null || password.isEmpty()) {
                logger.error("[{}]: Password is empty.", methodName);
            } else {
                page.fill("input[type='password']", password);
            }
            if (page.locator("text=The password must be at least").count() > 0) {
                logger.error("[{}]: Invalid password format error occurred", methodName);
            }
            page.waitForSelector("text=The password must be at least", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(500));
        });
    }

    public void clickSignInButton(String username,String password, String methodName) { //String methodName
        Allure.step("Click on Sign In button", () -> {
            try {
                ElementHandle signInButton = page.querySelector("button[label='Sign In']");
                if (signInButton != null && signInButton.isEnabled()) {
                    signInButton.press("Enter");
                } else {
                    logger.error("[{}]: Sign In button not found or not enabled.", methodName);
                    if ((password == null || password.isEmpty()) || (username == null || username.isEmpty())) {
                        isErrorPresent.set(true);
                    }
                }
            } catch (PlaywrightException e) {
                logger.error("[{}]: Error occurred: {}", methodName, e.getMessage());
            }
        });
    }

    public void checkLoginStatus(Locator errorLocator, AtomicBoolean isErrorPresent, String username, String password, boolean expectedLoginStatus, String methodName) {
        Allure.step("Check login status", () -> {
            try {
                page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(1000));
                if (errorLocator.count() > 0) {
                    isErrorPresent.set(true);
                    logger.error("[{}]: USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", methodName, username, password);
                }
            } catch (TimeoutError e) {
                // System.out.println("USER [" + credentials.getUser_email() + "] is logged in");
            }
            boolean actualLoginStatus = !isErrorPresent.get();
            if (actualLoginStatus != expectedLoginStatus) {
                logger.error("[{}]: Login status is not as expected. Expected login status: [{}]. Error is present on Login Page?: [{}]. User [{}]. Password [{}]", methodName, expectedLoginStatus, !actualLoginStatus, username, password);
                fail("Login status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + username + "]\nPassword [" + password + "]");
            }
        });
    }
}
