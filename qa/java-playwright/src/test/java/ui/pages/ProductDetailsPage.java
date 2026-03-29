package ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class ProductDetailsPage {
    private final Page page;

    public ProductDetailsPage(Page page) {
        this.page = page;
    }

    public void addToCartAndOpenCart() {
        page.locator("button:has-text('Add to cart')").click();

        // модалка "Added!" и ссылка "View Cart"
        PlaywrightAssertions.assertThat(page.locator("text=Added!")).isVisible();
        page.locator("a:has-text('View Cart')").click();
    }
}
