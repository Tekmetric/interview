package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class PaymentPage {
    private final Page page;

    public PaymentPage(Page page) {
        this.page = page;
    }

    public void payAndConfirm(String name, String cardNumber, String cvc, String month, String year) {
        PlaywrightAssertions.assertThat(page.locator("h2.heading:has-text('Payment')")).isVisible();

        page.locator("input[name='name_on_card']").fill(name);
        page.locator("input[name='card_number']").fill(cardNumber);
        page.locator("input[name='cvc']").fill(cvc);
        page.locator("input[name='expiry_month']").fill(month);
        page.locator("input[name='expiry_year']").fill(year);

        page.locator("button:has-text('Pay and Confirm Order')").click();
    }

    public void assertOrderConfirmed() {
        Locator success =
                page.locator("text=Your order has been placed successfully!")
                        .or(page.locator("text=Congratulations! Your order has been confirmed!"));

        PlaywrightAssertions.assertThat(success).isVisible();
    }
}
