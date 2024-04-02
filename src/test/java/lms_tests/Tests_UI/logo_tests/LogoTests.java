package lms_tests.Tests_UI.logo_tests;

import lms_pages.BasePage;
import lms_tests.BaseTest;
import org.testng.annotations.BeforeMethod;

public class LogoTests extends BaseTest {
    public BasePage basePage;

    @BeforeMethod
    public void INIT() {
        setUp();
        basePage = new BasePage(page);
    }
//    @Test
//    public void logoAvailabilityTest() {
//        basePage.requestResponseByURL("https://lms-development-rtprf.ondigitalocean.app/assets/layout/images/ait-tr.svg", "GET", 304, true);
//        String locator = "path[fill=\"#C33A3A\"]";
//        page.waitForSelector(locator);
//        assertTrue(page.isVisible(locator));
//    }
}