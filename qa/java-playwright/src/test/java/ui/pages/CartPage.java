package ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void assertCartVisible() {
        PlaywrightAssertions.assertThat(page.locator("text=Shopping Cart")).isVisible();
    }

    public void proceedToCheckout() {
        page.locator("a:has-text('Proceed To Checkout')").click();
    }
}
