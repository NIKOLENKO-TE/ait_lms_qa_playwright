package lms_pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import org.testng.Assert;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void isCurrentPage(String expectedUrl, boolean expectedResult) {
        boolean currentPageMatches = page.url().equals(expectedUrl);
        String ERROR_MESSAGE = "\nCurrent page URL has not expected value\nExpected result [" + expectedResult + "]\nCurrent URL  %s\nExpected URL %s";
        Allure.step("Check if current page matches the expected URL", () -> {
            try {
                page.waitForURL(url -> url.equals(expectedUrl), new Page.WaitForURLOptions().setTimeout(500));
            } catch (TimeoutError e) {
                if (expectedResult) {
                    throw new RuntimeException(String.format(ERROR_MESSAGE, page.url(), expectedUrl), e);
                }
            }
            if (expectedResult) {
                assertTrue(currentPageMatches, String.format(ERROR_MESSAGE, page.url(), expectedUrl));
            } else {
                Assert.assertFalse(currentPageMatches, String.format(ERROR_MESSAGE, page.url(), expectedUrl));
            }
        });
    }
    public void requestResponseByURL(String url, String responseMethod, int expectedStatusCode, boolean expectedResult) {
        page.navigate(url);
        page.waitForRequest(url, new Page.WaitForRequestOptions().setTimeout(2000), () -> {
            page.onResponse(response -> {
                try {
                    if (response.url() != null && response.url().equals(url)) {
                        int actualStatusCode = response.status();
                        assertEquals(response.request().method(), responseMethod, "\nExpected response code:" + expectedStatusCode + ", the actual response code is:" + actualStatusCode);
                        assertEquals(expectedResult, (actualStatusCode == expectedStatusCode));
                    }
                } catch (NullPointerException e) {
                    Assert.fail("NullPointerException: " + e.getMessage());
                } catch (TimeoutError | AssertionError e) {
                    Assert.fail("\nExpected response code from the server: [" + expectedStatusCode + "], and the expected result: [" + expectedResult + "]\nTimeoutError: " + e.getMessage());
                } catch (Exception e) {
                    Assert.fail("Exception: " + e.getMessage());
                } catch (Throwable e) {
                    Assert.fail("Throwable: " + e.getMessage());
                }
            });
        });
    }
    public void listAllButtonsOnPage() {
        List<Locator> buttons = page.locator("button").all();
        for (Locator button : buttons) {
            System.out.println(button.innerText());
        }
    }
}