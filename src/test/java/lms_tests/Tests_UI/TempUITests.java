package lms_tests.Tests_UI;

import com.microsoft.playwright.ElementHandle;
import lms_tests.BaseTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class TempUITests extends BaseTest {
    @Test
    public void fillManyTextBoxes() {
        page.navigate("https://datatables.net/examples/api/form.html");
        page.selectOption("[name=example_length]", "100");
        List<ElementHandle> fields = page.querySelectorAll("//tbody//input[@type='text']");
        fields.forEach(x -> x.fill("threadqa playwright"));
    }

//    @Test
//    public void recordVideoWithPW() {
//        Playwright playwright = Playwright.create();
//        Browser chrome = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
//        BrowserContext video = chrome.newContext(new Browser.NewContextOptions().setRecordHarPath(Paths.get("src/test_logs/har.har")).setRecordVideoDir(Paths.get("src/test_logs/")));
//        video.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true));
//        Page page = video.newPage();
//        page.navigate("http://the-internet.herokuapp.com/");
//        page.locator("xpath=//a[text()='Form Authentication']").click();
//        page.fill("#username", "tomsmith");
//        page.fill("#password", "SuperSecretPassword!");
//        page.click(".radius");
//        assertThat(page.locator(".flash.success")).isVisible();
//        video.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("src/test_logs/trace.zip")));
//        video.close();
//        playwright.close();
//    }
@Test
    public void textBoxFillTest() {
        page.navigate("http://85.192.34.140:8081/");
        page.getByText("Elements").click();
        page.querySelector("//li[@id='item-0']/span[1]").click();
        page.fill("[id=userName]", "ThreadQA Test");
        page.fill("[id=userEmail]", "threadqa@gmail.com");
        page.fill("[id=currentAddress]", "somewhere");
        page.click("[id=submit]");
        assertTrue(page.isVisible("[id=output]"));
        assertTrue(page.locator("[id=name]").textContent().contains("ThreadQA Test"));
    }
}
