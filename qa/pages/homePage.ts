import { expect, type Locator, type Page } from '@playwright/test';

export class HomePage {
    // Define locators
    readonly page: Page;
    readonly webLogo: Locator;
    readonly logoutLink: Locator;

    constructor(page: Page) {
        this.page = page;
        this.webLogo = page.getByRole('link', { name: 'Website for automation' });
        this.logoutLink = page.getByRole('link', { name: 'Logout' });
    }

    // Navigate to url
    async goToHomePage() {
        await this.page.goto(process.env.BASE_URL);
        await expect(this.webLogo).toBeVisible();
      }

    // Successful logout
    async clickLogoutLink() {
        await this.logoutLink.click();
        await expect(this.webLogo).toBeVisible();
      }
}