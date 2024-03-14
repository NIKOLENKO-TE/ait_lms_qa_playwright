package lms_tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import lms_pages.BaseHelper;
import lms_pages.BasePage;
import lms_pages.UI.HomePage;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import static lms_pages.BaseHelper.*;

public class BaseTest {
    private BrowserContext context;
    private Instant TEST_START_TIME;

    protected static Page page;
    private static Browser browser;
    BasePage basePage = new BasePage(page);
    public static Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeAll
    public static void SET_UP() {
        browser = BaseHelper.CHROME;
        BaseHelper.BROWSER_HEADLESS_MODE = false; // * Run the browser in headless mode
        BaseHelper.BROWSER_DEVTOOLS_MODE = false; // * Open DevTools in the browser
        BaseHelper.BROWSER_SLOW_DOWN_STEPS = 0; // * Latency of each test step in milliseconds
        BaseHelper.ALLURE = false; // ! Add files to ALLURE-report only for FAILED tests
        BaseHelper.ADD_TRACE_ZIP_TO_REPORT = ALLURE; // !!! Adding a trace to a report requires large resources and does not support WebKit browser
        BaseHelper.ADD_SCREENSHOT_TO_REPORT = ALLURE; // ? Add screenshots to the report (a screenshot will always be created in the folder)
        BaseHelper.ADD_VIDEO_TO_REPORT = ALLURE; // ? Add video to report
        BaseHelper.ADD_PAGE_SOURCE_TO_REPORT = ALLURE; // ? Add page source code to report
        BaseHelper.ADD_HAR_TO_REPORT = ALLURE; // ? Add a HAR file to the report
    }

    @BeforeMethod
    public void INIT_CONTEXT(Method method) {
        Allure.step("Initialize browser and page context for tests", () -> {
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setViewportSize(2000, 1000).setBaseURL(HomePage.homePageURL());
            if (ADD_VIDEO_TO_REPORT) {
                contextOptions.setRecordVideoSize(2000, 1000).setRecordVideoDir(Paths.get("src/test_logs/"));
            }
            if (ADD_HAR_TO_REPORT) {
                contextOptions.setRecordHarPath(Paths.get("src/test_logs/" + "Har_temp.har"));
            }
            if (browser == null) {
                SET_UP();
            }
            this.context = browser.newContext(contextOptions);
            if (ADD_TRACE_ZIP_TO_REPORT) {
                this.context.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(false));
            }
            page = this.context.newPage();
            basePage = new BasePage(page); // Инициализация basePage после инициализации page
            Allure.step("Open browser and navigate to Home Page", () -> {
                page.navigate(HomePage.homePageURL());;
            });
            LOG_START(method);
        });
    }

    @AfterMethod
    public void ADD_FILES_RO_REPORT(ITestResult result) throws IOException {
        String PARAMS = BaseHelper.getParams(result);
        Path ERROR_DIR = BaseHelper.getErrorDirFolderPath(result);
        FOLDER_CREATE_IF_ERROR(result, ERROR_DIR);
        ZIP(result, ERROR_DIR.resolve("Trace.zip"), context);
        SCREENSHOT(result, ERROR_DIR.resolve("Screenshot.png"), page);
        PAGE_SOURCE(result, ERROR_DIR.resolve("Page Source.html"), page);
        CLOSE_BROWSER_CONTEXT();
        VIDEO(result, ERROR_DIR.resolve("Video.webm"), page);
        HAR(result, ERROR_DIR, ERROR_DIR.resolve("Har.har"));
        DELETING_UNUSED_VIDEO_AND_HAR(result, ERROR_DIR.resolve("Har.har"), page);
        LOG_END(result, PARAMS, Duration.between(TEST_START_TIME, Instant.now()).toMillis());
        page.close();
    }

    private void CLOSE_BROWSER_CONTEXT() {
        if (context != null) {
            context.close();
            context = null;
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

    @AfterSuite
    public void TEAR_DOWN() {
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