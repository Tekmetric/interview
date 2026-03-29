package ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class ProductsPage {
    private final Page page;

    public ProductsPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://automationexercise.com/products");
        PlaywrightAssertions.assertThat(page.locator("text=All Products")).isVisible();
    }

    public void openFirstProduct() {
        // "View Product" у первого товара (стабильнее, чем hover по карточке)
        page.locator("a[href^='/product_details/']").first().click();
        PlaywrightAssertions.assertThat(page.locator("text=Write Your Review")).isVisible();
    }
}
