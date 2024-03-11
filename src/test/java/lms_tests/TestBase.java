package lms_tests;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import lms_pages.BasePage;
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

public class TestBase {
    public boolean ADD_FILES_TO_ALLURE_REPORT = false; // * true - добавляются файлы только для упавших тестов
    public final boolean HEADLESS_MODE = false;
    public final boolean DEVTOOLS_MODE = false;
    public int SLOW_DOWN_STEPS = 0; // Milliseconds
    public boolean ADD_TRACE_ZIP_TO_REPORT = ADD_FILES_TO_ALLURE_REPORT; // ! Добавлять трейс в отчёт, требует больших ресурсов и не поддерживает WebKit браузер
    public boolean ADD_SCREENSHOT_TO_REPORT = ADD_FILES_TO_ALLURE_REPORT; // ? Добавлять скриншоты в отчёт (скриншот в папке будет всегда)
    public boolean ADD_VIDEO_TO_REPORT = ADD_FILES_TO_ALLURE_REPORT; // ? Добавлять видео в отчёт
    public boolean ADD_PAGE_SOURCE_TO_REPORT = ADD_FILES_TO_ALLURE_REPORT; // ? Добавлять исходный код страницы в отчёт
    public boolean ADD_HAR_TO_REPORT = ADD_FILES_TO_ALLURE_REPORT; // ? Добавлять HAR файл в отчёт
    protected Page page;
    private Browser browser;
    private BrowserContext context;
    public final Browser CHROME = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS_MODE).setChannel("chrome").setDevtools(DEVTOOLS_MODE).setSlowMo(SLOW_DOWN_STEPS));
    public final Browser CHROMIUM = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS_MODE).setDevtools(DEVTOOLS_MODE).setSlowMo(SLOW_DOWN_STEPS));
    public final Browser EDGE = Playwright.create().chromium().launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS_MODE).setChannel("msedge").setDevtools(DEVTOOLS_MODE).setSlowMo(SLOW_DOWN_STEPS));
    public final Browser SAFARI = Playwright.create().webkit().launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS_MODE).setDevtools(DEVTOOLS_MODE).setSlowMo(SLOW_DOWN_STEPS));
    public final Browser FIREFOX = Playwright.create().firefox().launch(new BrowserType.LaunchOptions().setHeadless(HEADLESS_MODE).setDevtools(DEVTOOLS_MODE).setSlowMo(SLOW_DOWN_STEPS));
    public String LOGS_PATH = "src/test_logs/";
    public String DATE_TIME_PATTERN = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss"));

    Logger logger = LoggerFactory.getLogger(TestBase.class);
    private Instant TEST_START_TIME;
    BasePage basePage = new BasePage(page);

    @BeforeSuite
    public void setUp() {
        browser = CHROME;
    }

    @BeforeMethod
    public void initContext(Method method) {

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

        page = this.context.newPage(); // * right
        basePage = new BasePage(page);
        // page.navigate(HomePage.homePageURL());
        TEST_START_TIME = Instant.now();
        logger.info("[===================================[ {} ]===================================]", method.getName());
        logger.info("TEST START;");
    }

    @AfterMethod
    public void attachFilesToFailedTest(ITestResult result) throws IOException {
        String TEST_NAME = result.getMethod().getMethodName();
        String pageSource = page.content();
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

        if (!result.isSuccess()) {
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
        if (ADD_PAGE_SOURCE_TO_REPORT && !result.isSuccess()) {
            try {
                Files.write(pageSourcePath, pageSource.getBytes());
                Allure.addAttachment("Page Source.html", new ByteArrayInputStream(Files.readAllBytes(pageSourcePath)));
                if (Files.exists(pageSourcePath)) {
                    logger.info("FILE PAGE : {}", pageSourcePath.toAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Error while writing and adding page source to report: ", e);
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
        if (result.isSuccess() && ADD_FILES_TO_ALLURE_REPORT) {
            try {
                if (ADD_VIDEO_TO_REPORT) {
                    try {
                        Path videoFileName = page.video().path();
                        if (Files.exists(videoFileName)) {
                            Files.delete(videoFileName);
                        }
                    } catch (IOException e) {
                        logger.error("Error while deleting video file: ", e);
                    }
                }
                if (ADD_HAR_TO_REPORT) {
                    try {
                        if (Files.exists(harFilePath)) {
                            Files.delete(harFilePath);
                        }
                    } catch (IOException e) {
                        logger.error("Error while deleting HAR file: ", e);
                    }
                    Path harTempFilePath = Paths.get(LOGS_PATH + "Har_temp.har");
                    if (Files.exists(harTempFilePath)) {
                        Files.delete(harTempFilePath);
                    }
                }
            } catch (IOException e) {
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

    // Закрывает браузер после выполнения всех тестов в классе
    @AfterSuite
    public void tearDown() {
        if (browser != null) {
            browser.close();
            browser = null;
        }
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
}