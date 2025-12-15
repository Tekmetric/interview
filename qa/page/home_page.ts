import { expect, type Locator, type Page } from '@playwright/test';

/**
 * Represents the Home Page and stores its locators and functions.
 */
export class HomePage {
    readonly page: Page;
    readonly signup_login: Locator;
    readonly logout: Locator;
    readonly products: Locator;
    readonly cart: Locator;

    constructor(page: Page) {
        this.page = page;
        this.signup_login = page.getByRole('link', { name: ' Signup / Login' });
        this.logout = page.getByRole('link', { name: ' Logout' });
        this.products = page.getByRole('link', { name: ' Products' });
        this.cart = page.getByRole('link', { name: ' Cart' });
    }

    /**
     * Navigates to the home page URL.
     */
    async goto() {
        await this.page.goto('https://www.automationexercise.com/');
    }
}