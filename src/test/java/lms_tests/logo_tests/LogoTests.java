package lms_tests.logo_tests;

import lms_pages.BasePage;
import lms_tests.TestBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LogoTests extends TestBase {
    public BasePage basePage;

    @BeforeMethod
    public void init() {
        super.setUp();
        basePage = new BasePage(page);
    }
    @Test(invocationCount = 5)
    public void logoAvailabilityTest() {
        basePage.requestResponseByURL("https://lms-development-rtprf.ondigitalocean.app/assets/layout/images/ait-tr.svg", "GET", 200, true);
    }
}