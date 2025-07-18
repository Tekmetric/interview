import { Page, Locator } from '@playwright/test';

/**
 * Page Object for the Cart page.
 * Handles navigation actions from the cart, such as proceeding to checkout.
 */
export class CartPage {
    /** Locator for the proceed to checkout button on the cart page. */
    private readonly proceedToCheckoutButton: Locator;

    /**
     * Initializes a new instance of the CartPage.
     * @param page The Playwright Page object to interact with the cart page.
     */
    constructor(private readonly page: Page) {
        // Initialize locator for the checkout button using a specific CSS selector
        this.proceedToCheckoutButton = this.page.locator('a[class="btn btn-default check_out"]');
    }

    /**
     * Proceeds to the checkout page by clicking the proceed to checkout button.
     * Waits for the page to fully load after the action.
     * @throws Will fail if the button is not clickable or the page fails to load.
     */
    async proceedToCheckout() {
        // Click the proceed to checkout button and wait for the page to load
        await this.proceedToCheckoutButton.click();
        await this.page.waitForLoadState('load');
    }
}