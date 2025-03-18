import { Page, expect } from '@playwright/test';

export class ModalPage {
    constructor(private page: Page) {}

    async verifyProductAdded() {
        await this.page.waitForSelector('.modal-title');
        await expect(this.page.locator('.modal-title')).toHaveText('Added!');
    }

    async goToCart() {
        await this.page.locator('.modal-content [href="/view_cart"]').click();
    }
}
