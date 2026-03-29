package ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class CheckoutPage {
    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public void assertCheckoutVisible() {
        PlaywrightAssertions.assertThat(page.locator("text=Address Details")).isVisible();
        PlaywrightAssertions.assertThat(page.locator("text=Review Your Order")).isVisible();
    }

    public void addCommentAndPlaceOrder(String comment) {
        page.locator("textarea[name='message']").fill(comment);
        page.locator("a:has-text('Place Order')").click();
    }
}
