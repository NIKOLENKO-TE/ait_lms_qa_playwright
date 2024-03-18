package lms_pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;
import lms_pages.UI.LoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.File;
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
    public static boolean ENABLE_OFFLINE_REPORT = Boolean.parseBoolean(System.getProperty("allure_report_offline", String.valueOf(true)));

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
        BrowserType.LaunchOptions launchOptions = new BrowserType
                .LaunchOptions()
                .setDevtools(DEVTOOL)
                .setSlowMo(SLOWDOWN)
                .setHeadless(HEADLESS);
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
        Allure.step("Check if user is already logged in", () -> {
            boolean userIsLoggedIn = signOutButton.count() > 0;
            if (userIsLoggedIn) {
                signOutButton.first().click();
            }
        });
    }

    public void fillEmail(String username, String methodName) {
        Allure.step("Fill in Email address", () -> {
            if (page.isClosed()) {
                logger.error("Page is closed before email could be filled.");
                return;
            }
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
            page.waitForSelector("text=Invalid email format", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(200));
        });
    }

    public void fillPassword(String password, String methodName) {
        Allure.step("Fill in Password", () -> {
            if (page.isClosed()) {
                logger.error("Page is closed before password could be filled.");
                return;
            }
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
            if (password == null || password.isEmpty()) {
                logger.error("[{}]: Password is empty.", methodName);
            } else {
                page.fill("input[type='password']", password);
            }
            if (page.locator("text=The password must be at least").count() > 0) {
                logger.error("[{}]: Invalid password format error occurred", methodName);
            }
            page.waitForSelector("text=The password must be at least", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(200));
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
                Assert.fail("Login status is not as expected.\nExpected login status: [" + expectedLoginStatus + "]\nError is present on Login Page?: [" + !actualLoginStatus + "]\nUser [" + username + "]\nPassword [" + password + "]");
            }
        });
    }

    public static void GENERATE_OFFLINE_ALLURE_REPORT(Page page) {
        Allure.step("Generating offline Allure report", () -> {
            if (ENABLE_OFFLINE_REPORT) {
                try {
                    String command = "node_modules/.bin/allure.cmd generate build/allure-results -o src/test_logs/ALLURE_REPORT_OFFLINE --clean";
                    ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                    processBuilder.directory(new File(System.getProperty("user.dir")));
                    Process process = processBuilder.start();
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        logger.info("Offline report generated successfully");
                    } else {
                        logger.error("Failed to generate offline report. Exit code: {}", exitCode);
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error while generating offline report: ", e);
                }
            }
        });
    }
}
