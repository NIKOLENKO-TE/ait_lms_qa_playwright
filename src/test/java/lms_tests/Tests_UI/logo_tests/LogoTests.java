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
//    @Test(invocationCount = 5)
//    public void logoAvailabilityTest() {
//        basePage.requestResponseByURL("https://lms-development-rtprf.ondigitalocean.app/assets/layout/images/ait-tr.svg", "GET", 200, true);
//    }
}