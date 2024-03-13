package lms_tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import lms_pages.BaseHelper;
import lms_pages.BasePage;
import lms_pages.UI.HomePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static lms_pages.BaseHelper.*;

public class BaseTest {
    private BrowserContext context;
    public String LOGS_PATH = "src/test_logs/";
    public String DATE_TIME_PATTERN = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));
    private Instant TEST_START_TIME;

    protected static Page page;
    private Browser browser;
    BasePage basePage = new BasePage(page);
    static Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite
    public void setUp() {
        browser = BaseHelper.CHROME;
        BaseHelper.BROWSER_HEADLESS_MODE = false; // * Run the browser in headless mode
        BaseHelper.BROWSER_DEVTOOLS_MODE = false; // * Open DevTools in the browser
        BaseHelper.BROWSER_SLOW_DOWN_STEPS = 0; // * Latency of each test step in milliseconds
        BaseHelper.ALLURE = !true; // ! Add files to ALLURE-report only for FAILED tests
        BaseHelper.ADD_TRACE_ZIP_TO_REPORT = ALLURE; // !!! Adding a trace to a report requires large resources and does not support WebKit browser
        BaseHelper.ADD_SCREENSHOT_TO_REPORT = ALLURE; // ? Add screenshots to the report (a screenshot will always be created in the folder)
        BaseHelper.ADD_VIDEO_TO_REPORT = ALLURE; // ? Add video to report
        BaseHelper.ADD_PAGE_SOURCE_TO_REPORT = ALLURE; // ? Add page source code to report
        BaseHelper.ADD_HAR_TO_REPORT = ALLURE; // ? Add a HAR file to the report
    }

    @BeforeMethod
    public void initContext(Method method) {
        Allure.step("Initialize browser and page context for tests", () -> {
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setViewportSize(2000, 1000);
            if (ADD_VIDEO_TO_REPORT) {
                contextOptions.setRecordVideoSize(2000, 1000).setRecordVideoDir(Paths.get(LOGS_PATH));
            }
            if (ADD_HAR_TO_REPORT) {
                contextOptions.setRecordHarPath(Paths.get(LOGS_PATH + "Har_temp.har"));
            }
            this.context = browser.newContext(contextOptions);
            if (ADD_TRACE_ZIP_TO_REPORT) {
                this.context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
            }
            page = this.context.newPage();
            basePage = new BasePage(page);
            Allure.step("Open browser and navigate to Home Page", () -> {
                page.navigate(HomePage.homePageURL());
            });
            LOG_START(method);
        });
    }

//    @AfterMethod
//    public void addFileToAllure(ITestResult result) throws IOException {
//        String TEST_NAME = result.getMethod().getMethodName();
//        Instant testFinish = Instant.now();
//        String LOGS_PATH = "/logs/";
//        long timeElapsed = Duration.between(TEST_START_TIME, testFinish).toMillis();
//        Object[] parameters = result.getParameters();
//        String params = parameters.length > 0 ? ", with VALUES: " + Arrays.toString(parameters) : "";
//        String errorDirName = TEST_NAME + "_" + DATE_TIME_PATTERN;
//        Path errorDirPath = Paths.get(LOGS_PATH + errorDirName);
//        FOLDER(result, errorDirPath);
//        SCREENSHOT(result, page, errorDirPath, ADD_SCREENSHOT_TO_REPORT);
//        TRACE_ZIP(result, context, errorDirPath, ADD_TRACE_ZIP_TO_REPORT);
//        PAGE_SOURCE(result, page, errorDirPath, ADD_PAGE_SOURCE_TO_REPORT);
//        closeContext(context);
//        VIDEO(result, page, errorDirPath, ADD_VIDEO_TO_REPORT);
//        HAR_FILE(result, errorDirPath, ADD_HAR_TO_REPORT, LOGS_PATH);
//        deleteVideoAndHarFilesIfTestPassed(result, page, errorDirPath, ALLURE, ADD_VIDEO_TO_REPORT, ADD_HAR_TO_REPORT,LOGS_PATH);
//        LOG_END(result, params, timeElapsed);
//        page.close();
//    }




    @AfterMethod
    public void attachFilesToFailedTest(ITestResult result) throws IOException {
        String TEST_NAME = result.getMethod().getMethodName();
        Instant testFinish = Instant.now();
        long timeElapsed = Duration.between(TEST_START_TIME, testFinish).toMillis();
        Object[] parameters = result.getParameters();
        String params = parameters.length > 0 ? ", with VALUES: " + Arrays.toString(parameters) : "";
        String errorDirName = TEST_NAME + "_" + DATE_TIME_PATTERN;
        Path errorDirPath = Paths.get(LOGS_PATH + errorDirName);
        if (!result.isSuccess()) {
            Files.createDirectories(errorDirPath);
        }
        Path harFilePath = errorDirPath.resolve("Har.har");
        Path pageSourcePath = errorDirPath.resolve("Page Source.html");
        Path tracePath = errorDirPath.resolve("Trace.zip");
        Path screenshotPath = errorDirPath.resolve("Screenshot.png");
        Path videoPath = errorDirPath.resolve("Video.webm");
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
        if (context != null) {
            context.close();
            context = null;
        }
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
        try {
            if (ADD_HAR_TO_REPORT && !result.isSuccess()) {
                Path harTempFilePath = Paths.get(LOGS_PATH + "Har_temp.har");
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
                            Path harTempFilePath = Paths.get(LOGS_PATH + "Har_temp.har");
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
        if (result.isSuccess()) {
            logger.info("TEST PASSED{}, Time taken: [{}] milliseconds", params, timeElapsed);
        } else {
            logger.error("TEST FAILED{}, Time taken: [{}] milliseconds", params, timeElapsed);
        }
        page.close();
    }

    // Задаём аннотации для тест-кейсов
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface TestCaseID {
        String value();
    }

    // Задаём аннотации для сценрия тестов
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface TestType {
        enum Type {
            POSITIVE, NEGATIVE
        }

        Type value();
    }

    @AfterSuite
    public void tearDown() {
        Allure.step("Closing the browser context and browser after all tests in the class are executed", () -> {
            if (browser != null) {
                for (BrowserContext context : browser.contexts()) {
                    context.close();
                }
                browser.close();
            }
        });
    }

    private void LOG_START(Method method) {
        TEST_START_TIME = Instant.now();
        logger.info("[===================================[ {} ]===================================]", method.getName());
        logger.info("TEST START;");
    }

    public static void LOG_END(ITestResult result, String params, long timeElapsed) {
        if (result.isSuccess()) {
            logger.info("TEST PASSED{}, Time taken: [{}] milliseconds;", params, timeElapsed);
        } else {
            logger.error("TEST FAILED{}, Time taken: [{}] milliseconds;", params, timeElapsed);
        }
    }
}