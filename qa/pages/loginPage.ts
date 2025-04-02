import { expect, type Locator, type Page } from '@playwright/test';

export class LoginPage {
    // Define locators
    readonly page: Page;
    readonly loginLink: Locator;
    readonly loginHeader: Locator;
    readonly emailField: Locator;
    readonly passwordField: Locator;
    readonly loginButton: Locator;
    readonly headerText: Locator;

    constructor(page: Page) {
        this.page = page;
        this.loginLink = page.getByRole('link', { name: 'Signup / Login' })
        this.loginHeader = page.getByRole('heading', { name: 'New User Signup!' })
        this.emailField = page.locator('form').filter({ hasText: 'Login' }).getByPlaceholder('Email Address');
        this.passwordField = page.getByRole('textbox', { name: 'Password' });
        this.loginButton = page.getByRole('button', { name: 'Login' });
        this.headerText = page.locator('#header');
    }

    // Navigate to login page
    async clickLoginLink() {
        await this.loginLink.click();
        await expect(this.loginHeader).toBeVisible();
    }

    // Successful login
    async loginSuccess(email: string, password: string, userName: string) {
        await this.loginLink.click();
        await this.emailField.fill(email);
        await this.passwordField.fill(password);
        await this.loginButton.click();
        await expect(this.headerText).toContainText(userName);
    }
}