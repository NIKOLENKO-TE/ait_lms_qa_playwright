package lms_pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import io.qameta.allure.Allure;
import junit.framework.AssertionFailedError;
import org.testng.Assert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
                Assert.assertTrue(currentPageMatches, String.format(ERROR_MESSAGE, page.url(), expectedUrl));
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
                        assertEquals(response.request().method(), responseMethod, "\nОжидаемый код ответа: " + expectedStatusCode + ", фактический код ответа: " + actualStatusCode);
                        org.junit.Assert.assertEquals(expectedResult, (actualStatusCode == expectedStatusCode));
                    }
                } catch (NullPointerException e) {
                    fail("NullPointerException: " + e.getMessage());
                } catch (AssertionFailedError e) {
                    fail("AssertionFailedError: " + e.getMessage());
                } catch (TimeoutError | AssertionError e) {
                    fail("\nОжидаемый код ответа от сервера: [" + expectedStatusCode + "], и ожидаемый результат: [" + expectedResult + "]\nTimeoutError: " + e.getMessage());
                } catch (Exception e) {
                    fail("Exception: " + e.getMessage());
                } catch (Throwable e) {
                    fail("Throwable: " + e.getMessage());
                }
            });
        });
    }

}