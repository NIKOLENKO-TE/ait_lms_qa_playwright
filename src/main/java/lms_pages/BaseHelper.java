package lms_pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;
import lms_pages.UI.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.fail;

public class BaseHelper extends BasePage {
    static Logger logger = LoggerFactory.getLogger(LoginPage.class);
    public String methodName;
    public Locator signOutButton;
    public Locator errorLocator;
    public AtomicBoolean isErrorPresent;
    public static boolean BROWSER_HEADLESS_MODE = false;
    public static boolean BROWSER_DEVTOOLS_MODE = false;
    public static int BROWSER_SLOW_DOWN_STEPS = 0;
    public static boolean ALLURE = true;
    public static boolean ADD_TRACE_ZIP_TO_REPORT = ALLURE;
    public static boolean ADD_SCREENSHOT_TO_REPORT = ALLURE;
    public static boolean ADD_VIDEO_TO_REPORT = ALLURE;
    public static boolean ADD_PAGE_SOURCE_TO_REPORT = ALLURE;
    public static boolean ADD_HAR_TO_REPORT = ALLURE;
    public static final Browser CHROME = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(BROWSER_HEADLESS_MODE).setChannel("chrome").setDevtools(BROWSER_DEVTOOLS_MODE).setSlowMo(BROWSER_SLOW_DOWN_STEPS));
    public static final Browser CHROMIUM = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(BROWSER_HEADLESS_MODE).setDevtools(BROWSER_DEVTOOLS_MODE).setSlowMo(BROWSER_SLOW_DOWN_STEPS));
    public static final Browser EDGE = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(BROWSER_HEADLESS_MODE).setChannel("msedge").setDevtools(BROWSER_DEVTOOLS_MODE).setSlowMo(BROWSER_SLOW_DOWN_STEPS));
    public static final Browser SAFARI_WEBKIT = Playwright.create().webkit().launch(new BrowserType.LaunchOptions().setHeadless(BROWSER_HEADLESS_MODE).setDevtools(BROWSER_DEVTOOLS_MODE).setSlowMo(BROWSER_SLOW_DOWN_STEPS));
    public static final Browser FIREFOX = Playwright.create().firefox().launch(new BrowserType.LaunchOptions().setHeadless(BROWSER_HEADLESS_MODE).setDevtools(BROWSER_DEVTOOLS_MODE).setSlowMo(BROWSER_SLOW_DOWN_STEPS));

    public BaseHelper(Page page) {
        super(page);
    }

    public static void FOLDER_CREATE_IF_ERROR(ITestResult result, Path errorDirPath) throws IOException {
        if (!result.isSuccess()) {
            Files.createDirectories(errorDirPath);
        }
    }

    public static void SCREENSHOT(ITestResult result, Path screenshotPath, Page page) {
        if (!result.isSuccess() && !ADD_SCREENSHOT_TO_REPORT) {
            try {
                page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));
                if (Files.exists(screenshotPath)) {
                    logger.info("FILE SCREENSHOT: {}", screenshotPath.toAbsolutePath());
                }
            } catch (Exception e) {
                logger.error("Error while taking and saving screenshot: ", e);
            }
        } else if (ADD_SCREENSHOT_TO_REPORT && !result.isSuccess()) {
            try {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));
                if (Files.exists(screenshotPath)) {
                    logger.info("FILE SCREENSHOT: {}", screenshotPath.toAbsolutePath());
                }
                if (ADD_SCREENSHOT_TO_REPORT) {
                    Allure.addAttachment("Screenshot.png", new ByteArrayInputStream(screenshot));
                }
            } catch (Exception e) {
                logger.error("Error while adding screenshot to report: ", e);
            }
        }
    }

    public static void PAGE_SOURCE(ITestResult result, Path pageSourcePath, Page page) {
        if (ADD_PAGE_SOURCE_TO_REPORT && !result.isSuccess()) {
            try {
                String pageSource = page.content();
                Files.write(pageSourcePath, pageSource.getBytes());
                Allure.addAttachment("Page Source.html", new ByteArrayInputStream(Files.readAllBytes(pageSourcePath)));
                if (Files.exists(pageSourcePath)) {
                    logger.info("FILE PAGE : {}", pageSourcePath.toAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Error while writing and adding page source to report: ", e);
            }
        }
    }

    public static void VIDEO(ITestResult result, Path videoPath, Page page) {
        if (ADD_VIDEO_TO_REPORT && !result.isSuccess()) {
            try {
                Path videoFileName = page.video().path();
                Files.move(videoFileName, videoPath);
                Allure.addAttachment("Screen capture.webm", new ByteArrayInputStream(Files.readAllBytes(videoPath)));
                if (Files.exists(videoPath)) {
                    logger.info("FILE VIDEO: {}", videoPath.toAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Error while renaming and adding video file to report: ", e);
            }
        }
    }

    public static void DELETING_UNUSED_VIDEO_AND_HAR(ITestResult result, Path harFilePath, Page page) {
        if (result.isSuccess() && ALLURE) {
            try {
                if (ADD_VIDEO_TO_REPORT) {
                    Allure.step("Delete video files because test passed", () -> {
                        try {
                            Path videoFileName = page.video().path();
                            if (Files.exists(videoFileName)) {
                                Files.delete(videoFileName);
                            }
                        } catch (IOException e) {
                            logger.error("Error while deleting video file: ", e);
                        }
                    });
                }
                if (ADD_HAR_TO_REPORT) {
                    Allure.step("Delete HAR files because test passed", () -> {
                        try {
                            if (Files.exists(harFilePath)) {
                                Files.delete(harFilePath);
                            }
                            Path harTempFilePath = Paths.get("src/test_logs/" + "Har_temp.har");
                            if (Files.exists(harTempFilePath)) {
                                Files.delete(harTempFilePath);
                            }
                        } catch (IOException e) {
                            logger.error("Error while deleting HAR file: ", e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.error("Error while deleting video and HAR files: ", e);
            }
        }
    }

    public static void HAR(ITestResult result, Path errorDirPath, Path harFilePath) {
        try {
            if (ADD_HAR_TO_REPORT && !result.isSuccess()) {
                Path harTempFilePath = Paths.get("src/test_logs/" + "Har_temp.har");
                Files.move(harTempFilePath, errorDirPath.resolve("Har.har"));
                byte[] harData = Files.readAllBytes(harFilePath);
                Allure.addAttachment("Har.har", new ByteArrayInputStream(harData));
            }
            if (Files.exists(harFilePath)) {
                logger.info("FILE HAR  : {}", harFilePath.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error while adding HAR file to report: ", e);
        }
    }

    public static void ZIP(ITestResult result, Path tracePath, BrowserContext context) {
        try {
            if (ADD_TRACE_ZIP_TO_REPORT && !result.isSuccess()) {
                context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                Allure.addAttachment("Trace.zip", new ByteArrayInputStream(Files.readAllBytes(tracePath)));
                if (Files.exists(tracePath)) {
                    logger.info("FILE TRACE: {}", tracePath.toAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Error while adding trace file to report: ", e);
        }
    }

    public static String getParams(ITestResult result) {
        return result.getParameters().length > 0 ? ", with VALUES: " + Arrays.toString(result.getParameters()) : "";
    }

    public static Path getErrorDirFolderPath(ITestResult result) {
        return Paths.get("src/test_logs/" + result.getMethod().getMethodName() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")));
    }

    public void loginVariables() {
        methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        signOutButton = page.locator("button:has-text('SignOut')");
        errorLocator = page.locator("div").filter(new Locator.FilterOptions().setHasText("ErrorInvalid login or password")).nth(2);
        isErrorPresent = new AtomicBoolean(false);
    }

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

    public void clickSignInButton(String username, String password, String methodName) { //String methodName
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
