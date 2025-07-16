import { Page, expect } from '@playwright/test';

export class LoginPage {
    private page: Page;
    private emailInput = '[data-qa="login-email"]';
    private passwordInput = '[data-qa="login-password"]';
    private loginButton = '[data-qa="login-button"]';
    private loginLink = 'a[href="/login"]';
    private logoutLink = 'a[href="/logout"]';
    private loginToYourAccountText = 'div[class="login-form"] h2';
    private userInfoSelector = 'ul > li:nth-child(10) > a';

    constructor(page: Page) {
        this.page = page;
    }

    async navigate() {
        await this.page.goto('https://www.automationexercise.com');
        // Expect a title "to contain" a substring.
        await expect(this.page).toHaveTitle(/Automation Exercise/);
        await this.page.click(this.loginLink);
    }

    async verifyLoginUI() {
        const elements = [
            { name: 'Email Input', selector: this.emailInput },
            { name: 'Password Input', selector: this.passwordInput },
            { name: 'Login Button', selector: this.loginButton },
            { name: 'Login Header Text', selector: this.loginToYourAccountText },
        ];

        for (const { name, selector } of elements) {
            await expect(this.page.locator(selector)).toBeVisible({ timeout: 5000 });
            //console.log(`${name} is visible`);
        }

        // Check the text "Login to your account"
        await expect(this.page.locator(this.loginToYourAccountText)).toHaveText('Login to your account');

        // Check placeholders
        await expect(this.page.locator(this.emailInput)).toHaveAttribute('placeholder', 'Email Address');
        await expect(this.page.locator(this.passwordInput)).toHaveAttribute('placeholder', 'Password');
    }

    async login(email: string, password: string) {
        await this.page.fill(this.emailInput, email);
        await this.page.fill(this.passwordInput, password);
        await this.page.click(this.loginButton);
    }

    async verifyLoginSuccess(firstName: string, lastName: string) {
    const expectedText = `Logged in as ${firstName} ${lastName}`;
    await expect(this.page.locator(this.userInfoSelector)).toHaveText(expectedText);
}

    async verifyLoginFailure() {
        await expect(this.page).toHaveURL('https://www.automationexercise.com/login');
    }

    async logout() {
        await this.page.click(this.logoutLink);
        await expect(this.page).toHaveURL('https://www.automationexercise.com/login'); // Confirm redirected URL
        await expect(this.page.locator(this.logoutLink)).not.toBeVisible(); // Ensure logout link is not visible
    }
}