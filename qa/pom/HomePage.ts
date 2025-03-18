import { Page } from '@playwright/test';

export class HomePage {
    constructor(private page: Page) {}

    async navigate() {
        await this.page.goto('/');
    }
}
