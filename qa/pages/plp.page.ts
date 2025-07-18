import { Page, Locator } from '@playwright/test';

/**
 * Page Object for the Product Listing Page (PLP).
 * Handles navigation and product selection from the listing.
 */
export class PlpPage {
    /** Locator for the first product's 'View Product' link on the PLP. */
    private readonly firstProductLink: Locator;

    /**
     * Initializes a new instance of the PlpPage.
     * @param page The Playwright Page object for the PLP.
     */
    constructor(private readonly page: Page) {
        this.firstProductLink = this.page.locator('a:has-text("View Product")').first();
    }

    /**
     * Navigates to the product listing page.
     * Waits for the page to fully load.
     */
    async navigate() {
        await this.page.goto(`${process.env.BASE_URL}/products`);
        await this.page.waitForLoadState('load');
    }

    /**
     * Selects the first product on the PLP by clicking 'View Product'.
     * Waits for the product detail page to fully load.
     */
    async selectProduct() {
        await this.firstProductLink.click();
        await this.page.waitForLoadState('load');
    }
}