import { expect, type Locator, type Page } from '@playwright/test';

export class HomePage {
    readonly page: Page;
    readonly signup_login: Locator;
    readonly logout: Locator;



    constructor(page: Page) {
        this.page = page;
        this.signup_login = page.getByRole('link', { name: ' Signup / Login' });
        this.logout = page.getByRole('link', { name: ' Logout' });
    }

    async goto() {
        await this.page.goto('https://www.automationexercise.com/');
    }
}