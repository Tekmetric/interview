import {After, Before} from '@cucumber/cucumber';
import { chromium } from 'playwright';
import config from './playwright.config';

Before(async function () {
    console.log('Before hook');
    this.browser = await chromium.launch(config.use);
    this.context = await this.browser.newContext(); // Create a new context
    this.page = await this.context.newPage(); // Create a page within the context
});

After(async function () {
    console.log('After hook');
    this.page = null;
    if (this.context) {
        await this.context.close();
        this.context = null;
    }
    if (this.browser) {
        try {
            await Promise.race([
                this.browser.close(),
                new Promise((_, reject) => setTimeout(() => reject(new
                Error('Browser close timeout')), 30000)) // 30 seconds timeout
            ]);
        } catch (error) {
            console.error('Error closing browser:', error);
        }
    }
});