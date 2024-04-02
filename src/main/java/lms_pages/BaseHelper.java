package lms_pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Allure;
import lms_pages.UI.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
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

public class BaseHelper extends BasePage {
    static Logger logger = LoggerFactory.getLogger(LoginPage.class);
    public String methodName;
    public Locator signOutButton;
    public Locator errorLocator;
    public AtomicBoolean isErrorPresent;

    public static boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", String.valueOf(false))); // ? Headless mode
    public static boolean DEVTOOL = Boolean.parseBoolean(System.getProperty("devtools", String.valueOf(false))); // ? DevTools
    public static int SLOWDOWN = Integer.parseInt(System.getProperty("slowdown", String.valueOf(0))); // ? Slowdown steps

    public static boolean ENABLE_ALL_FILES = Boolean.parseBoolean(System.getProperty("allure_report", String.valueOf(true))); // ! Add files to ALLURE-report only for FAILED tests
    public static boolean TRACE = Boolean.parseBoolean(System.getProperty("trace", String.valueOf(ENABLE_ALL_FILES))); // !!! Adding a trace to a report requires large resources and does not support WebKit browser
    public static boolean SCREENSHOT = Boolean.parseBoolean(System.getProperty("screenshot", String.valueOf(ENABLE_ALL_FILES))); // ? Add screenshots to the report (a screenshot will always be created in the folder)
    public static boolean VIDEO = Boolean.parseBoolean(System.getProperty("video", String.valueOf(ENABLE_ALL_FILES))); // ? Add video to report
    public static boolean PAGE = Boolean.parseBoolean(System.getProperty("page", String.valueOf(ENABLE_ALL_FILES))); // ? Add page source code to report
    public static boolean HAR = Boolean.parseBoolean(System.getProperty("har", String.valueOf(ENABLE_ALL_FILES))); // ? Add a HAR file to the report

    public BaseHelper(Page page) {
        super(page);
    }

    public static Browser setupBrowser(String browserType, boolean headless, boolean devtools, int slowdown, boolean allure_report) {
        HEADLESS = headless;
        DEVTOOL = devtools;
        SLOWDOWN = slowdown;
        ENABLE_ALL_FILES = allure_report;
        TRACE = allure_report;
        SCREENSHOT = allure_report;
        VIDEO = allure_report;
        PAGE = allure_report;
        HAR = allure_report;
        Browser browser;
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setDevtools(DEVTOOL).setSlowMo(SLOWDOWN).setHeadless(HEADLESS);
        switch (browserType.toUpperCase()) {
            case "CHROME":
                browser = Playwright.create().chromium().launch(launchOptions.setChannel("chrome"));
                break;
            case "CHROMIUM":
                browser = Playwright.create().chromium().launch(launchOptions);
                break;
            case "EDGE":
                browser = Playwright.create().chromium().launch(launchOptions.setChannel("msedge"));
                break;
            case "SAFARI":
                browser = Playwright.create().webkit().launch(launchOptions);
                break;
            case "FIREFOX":
                browser = Playwright.create().firefox().launch(launchOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
        return browser;
    }

    public static Browser setupBrowser() {
        String browserType = System.getProperty("browserType", "CHROME");

        Browser browser;
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setDevtools(DEVTOOL).setSlowMo(SLOWDOWN).setHeadless(HEADLESS);
        switch (browserType.toUpperCase()) {
            case "CHROME":
                browser = Playwright.create().chromium().launch(launchOptions.setChannel("chrome"));
                break;
            case "CHROMIUM":
                browser = Playwright.create().chromium().launch(launchOptions);
                break;
            case "EDGE":
                browser = Playwright.create().chromium().launch(launchOptions.setChannel("msedge"));
                break;
            case "SAFARI":
                browser = Playwright.create().webkit().launch(launchOptions);
                break;
            case "FIREFOX":
                browser = Playwright.create().firefox().launch(launchOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        }
        return browser;
    }

    public static void FOLDER_CREATE_IF_ERROR(ITestResult result, Path errorDirPath) throws IOException {
        if (!result.isSuccess()) {
            Files.createDirectories(errorDirPath);
        }
    }

    public static void SCREENSHOT(ITestResult result, Path screenshotPath, Page page) {
        if (!result.isSuccess() && !SCREENSHOT) {
            try {
                page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));
                if (Files.exists(screenshotPath)) {
                    logger.info("FILE SCREENSHOT: {}", screenshotPath.toAbsolutePath());
                }
            } catch (Exception e) {
                logger.error("Error while taking and saving screenshot: ", e);
            }
        } else if (SCREENSHOT && !result.isSuccess()) {
            try {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setPath(screenshotPath).setFullPage(true));
                if (Files.exists(screenshotPath)) {
                    logger.info("FILE SCREENSHOT: {}", screenshotPath.toAbsolutePath());
                }
                if (SCREENSHOT) {
                    Allure.addAttachment("Screenshot.png", new ByteArrayInputStream(screenshot));
                }
            } catch (Exception e) {
                logger.error("Error while adding screenshot to report: ", e);
            }
        }
    }

    public static void PAGE_SOURCE(ITestResult result, Path pageSourcePath, Page page) {
        if (PAGE && !result.isSuccess()) {
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
        if (VIDEO && !result.isSuccess()) {
            try {
                Video video = page.video();
                if (video != null) {
                    Path videoFileName = video.path();
                    Files.move(videoFileName, videoPath);
                    Allure.addAttachment("Screen capture.webm", new ByteArrayInputStream(Files.readAllBytes(videoPath)));
                    if (Files.exists(videoPath)) {
                        logger.info("FILE VIDEO: {}", videoPath.toAbsolutePath());
                    }
                }
            } catch (IOException e) {
                logger.error("Error while renaming and adding video file to report: ", e);
            }
        }
    }

    public static void DELETING_UNUSED_VIDEO_AND_HAR(ITestResult result, Path harFilePath, Page page) {
        if (result.isSuccess() && (VIDEO || HAR)) {
            try {
                if (VIDEO) {
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
                if (HAR) {
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

    public static void createInternetShortcut(String url, Path shortcutPath) {
        String shortcut = "[InternetShortcut]\nURL=" + url + "\n";
        try {
            Files.write(shortcutPath, shortcut.getBytes());
        } catch (IOException e) {
            logger.error("Error while creating internet shortcut: ", e);
        }
    }

    public static void HAR(ITestResult result, Path errorDirPath, Path harFilePath) {
        try {
            if (HAR && !result.isSuccess()) {
                Path harTempFilePath = Paths.get("src/test_logs/" + "Har_temp.har");
                Files.move(harTempFilePath, errorDirPath.resolve("Har.har"));
                byte[] harData = Files.readAllBytes(harFilePath);
                Allure.addAttachment("Har.har", new ByteArrayInputStream(harData));
                Path shortcutPath = errorDirPath.resolve("Link to HAR Viewer.url");  // * Create a shortcut to the website after saving the HAR file
                createInternetShortcut("http://www.softwareishard.com/har/viewer/", shortcutPath);
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
            if (TRACE && !result.isSuccess()) {
                context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                Allure.addAttachment("Trace.zip", new ByteArrayInputStream(Files.readAllBytes(tracePath)));
                if (Files.exists(tracePath)) {
                    logger.info("FILE TRACE: {}", tracePath.toAbsolutePath());
                }
                // Создание ярлыка в папке для упавшего теста
                Path shortcutPath = tracePath.getParent().resolve("Link to TRACE Viewer.url");
                createInternetShortcut("https://trace.playwright.dev/", shortcutPath);
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
        Allure.step("Check if user is logged in", () -> {
            boolean userIsLoggedIn = signOutButton.count() > 0;
            if (userIsLoggedIn) {
                signOutButton.first().click();
            }
        });
    }

    public void clickLoginButton() {
        //isLoginButtonPresent(100);
        page.locator("button:has-text('Login')").dispatchEvent("click");
    }
    public boolean isSignOutButtonPresent(int timeoutMillis) {
        Locator signOutButton = page.locator("button:has-text('SignOut')");
        try {
            return signOutButton.isEnabled(new Locator.IsEnabledOptions().setTimeout(timeoutMillis));
        } catch (TimeoutError e) {
            return false;
        }
    }

    public boolean isLoginButtonPresent(int timeoutMillis) {
        Locator loginButton = page.locator("button:has-text('Login')");
        try {
            return loginButton.isEnabled(new Locator.IsEnabledOptions().setTimeout(timeoutMillis));
        } catch (TimeoutError e) {
            return false;
        }
    }
    public void fillEmail(String username, String methodName) {
        Allure.step("Fill in Email address", () -> {
            page.locator("input[placeholder='Email address']").click();
            page.locator("input[placeholder='Email address']").pressSequentially(username);
            if (username == null || username.isEmpty()) {
                logger.warn("[{}]: Email address is empty.", methodName);
            }
            if (page.locator("text=Invalid email format").isVisible()) {
                logger.warn("[{}]: Invalid email format error occur", methodName);
            }
            if (page.locator("text='This field is required'").isVisible()) {
                logger.warn("[{}]: 'This field is required' error occur", methodName);
            }
        });
    }
    public void fillPassword(String password, String methodName) {
        Allure.step("Fill in Password", () -> {
            page.locator("input.p-password-input").fill(password);
            if (password == null || password.isEmpty()) {
                logger.warn("[{}]: Password is empty.", methodName);
            }
            if (page.locator("text='Invalid password format'").isVisible()) {
                logger.warn("[{}]: Invalid password format error occur", methodName);
            }
            if (page.locator("text='This field is required'").isVisible()) {
                logger.warn("[{}]: 'This field is required' error occur", methodName);
            }
        });
    }

    public void clickSignInButton(String username, String password, String methodName) {
        Allure.step("Click on Sign In button", () -> {
            try {
                ElementHandle signInButton = page.querySelector("button[label='Sign In']");
                if (signInButton != null && signInButton.isEnabled()) {
                    signInButton.press("Enter");
                } else {
                    logger.warn("[{}]: Sign In button not found or not enabled.", methodName);
                    if ((password == null || password.isEmpty()) || (username == null || username.isEmpty())) {
                        isErrorPresent.set(true);
                    }
                }
            } catch (PlaywrightException e) {
                logger.error("[{}]: Error occurred: {}", methodName, e.getMessage());
            }
            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(2000));
        });

    }

     public void checkLoginAvailability(Locator errorLocator, AtomicBoolean isErrorPresent, String username, String password, boolean expectedLoginStatus, String methodName) {
        Allure.step("Check login status", () -> {
            try {
                page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(1000));
                if (errorLocator.count() > 0) {
                    isErrorPresent.set(true);
                    logger.error("[{}]: USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", methodName, username, password);
                }
            } catch (TimeoutError e) {
                logger.error("[{}]: An error occurred while checking for 'Invalid login or password' error: {}", methodName, e.getMessage());
            }
            try {
                if (page.locator("text='Invalid email format'").isVisible()) {
                    isErrorPresent.set(true);
                    logger.warn("[{}]: USER [{}] is not logged in because Invalid email format.", methodName, username);
                    throw new AssertionError("Invalid email format");
                }
            } catch (TimeoutError e) {
                logger.error("[{}]: An error occurred while checking for 'Invalid email format' error: {}", methodName, e.getMessage());
            }
            boolean actualLoginStatus = !isErrorPresent.get();
            if (actualLoginStatus != expectedLoginStatus) {
                logger.error("[{}]: Login status is not as expected. Expected login status: [{}]. Error is present on Login Page?: [{}]. User [{}]. Password [{}]", methodName, expectedLoginStatus, !actualLoginStatus, username, password);
                Assert.fail("\nLogin status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + username + "]\nPassword [" + password + "]");
            }
        });
    }

    public void checkLoginAvailability2(Locator errorLocator, AtomicBoolean isErrorPresent, String username, String password, boolean expectedLoginStatus, String methodName) {
        Allure.step("Check errors on the Login page", () -> {
            boolean errorLogin = false; // новая переменная
            try {
                // Проверяем, есть ли сообщение об ошибке "Invalid login or password" или errorLocator содержит элементы
                try {
                    page.waitForSelector("div:has-text('ErrorInvalid login or password')", new Page.WaitForSelectorOptions().setTimeout(1000));
                    if (errorLocator.count() > 0) {
                        isErrorPresent.set(true);
                        errorLogin = true; // устанавливаем errorLogin в true, если есть сообщение "Invalid login or password"
                        logger.error("[{}]: USER [{}] and PASSWORD [{}] is not logged in because login or password is invalid, user not exist or not confirmed yet.", methodName, username, password);
                        throw new AssertionError("\nUSER [" + username + "] and PASSWORD [" + password + "] is not logged in because login or password is invalid, user not exist or not confirmed yet.");
                    }
                } catch (TimeoutError e) {
                    // logger.error("[{}]: An error occurred while checking for 'Invalid login or password' error: {}", methodName, e.getMessage());
                }
                // Проверяем, есть ли сообщение 'Login has been successful'
                try {
                    if (page.locator("text='Login has been successful'").isVisible()) {
                        isErrorPresent.set(false);
                        logger.warn("[{}]: USER [{}] and PASSWORD [{}] successfully logged in.", methodName, username, password);
                    }
                } catch (Exception e) {
                    logger.error("[{}]: An error occurred while checking for 'Login has been successful' message: {}", methodName, e.getMessage());
                }
                // Проверяем, есть ли сообщение об ошибке "The password must be at least"
                try {
                    if (page.locator("text='The password must be at least'").isVisible()) {
                        isErrorPresent.set(true);
                        logger.warn("[{}]: USER [{}] is not logged in because the password is empty.", methodName, username);
                        throw new AssertionError("The password must be at least");
                    }
                } catch (Exception e) {
                    logger.error("[{}]: An error occurred while checking for 'The password must be at least' error: {}", methodName, e.getMessage());
                }
                // Проверяем, есть ли сообщение об ошибке "This field is required"
                try {
                    if (page.locator("text='This field is required'").isVisible()) {
                        isErrorPresent.set(true);
                        logger.warn("[{}]: USER [{}] is not logged in because a required field is empty.", methodName, username);
                        throw new AssertionError("This field is required");
                    }
                } catch (Exception e) {
                    logger.error("[{}]: An error occurred while checking for 'This field is required' error: {}", methodName, e.getMessage());
                }
                // Проверяем, есть ли сообщение об ошибке "Invalid email format"
                try {
                    if (page.locator("text='Invalid email format'").isVisible()) {
                        isErrorPresent.set(true);
                        logger.warn("[{}]: USER [{}] is not logged in because Invalid email format.", methodName, username);
                        throw new AssertionError("Invalid email format");
                    }
                } catch (Exception e) {
                    logger.error("[{}]: An error occurred while checking for 'Invalid email format' error: {}", methodName, e.getMessage());
                }
            } catch (Exception e) {
                logger.error("[{}]: An unexpected error occurred: {}", methodName, e.getMessage());
                throw new RuntimeException(e);
            }
            try {
                // Проверяем соответствие фактического статуса логина ожидаемому
                if (expectedLoginStatus && isErrorPresent.get()) {
                    // Если успешный вход, но ошибка присутствует
                    throw new AssertionError("\nLogin status is not as expected. \nExpected login status: [" + expectedLoginStatus + "]. \nUser [" + username + "]. \nPassword [" + password + "]");
                } else if (!expectedLoginStatus && isErrorPresent.get()) {
                    // Если неудачный вход, и ошибка присутствует
                    logger.info("[{}]: Login status is as expected. Expected login status: [{}]. User [{}]. Password [{}]", methodName, expectedLoginStatus, username, password);
                } else if (!isErrorPresent.get() && errorLogin) {
                    // Если ошибок на странице нет, но есть сообщение "Invalid login or password"
                    throw new AssertionError("\nLogin status is not as expected. \nExpected login status: [" + expectedLoginStatus + "]. \nUser [" + username + "]. \nPassword [" + password + "]");
                } else if (expectedLoginStatus != isErrorPresent.get()) {
                    // Если статус входа не соответствует ожидаемому
                    throw new AssertionError("\nLogin status is not as expected. \nExpected login status: [" + expectedLoginStatus + "]. \nUser [" + username + "]. \nPassword [" + password + "]");
                } else if (!expectedLoginStatus && !isErrorPresent.get()) {
                    // Если ошибка ожидается, но пользователь все равно успешно проходит логин
                    throw new AssertionError("\nLogin status is not as expected. \nExpected login status: [" + expectedLoginStatus + "]. \nUser [" + username + "]. \nPassword [" + password + "]");
                }
            } catch (AssertionError e) {
                logger.error("[{}]: Login status is not as expected. Expected login status: [{}]. User [{}]. Password [{}]", methodName, expectedLoginStatus, username, password);
                Assert.fail(e.getMessage() + "\nError is present on Login Page?: [" + isErrorPresent.get() + "]\nUser can logged in?: [" + !isErrorPresent.get() + "]");
            }
           // page.pause();
        });
    }
}
