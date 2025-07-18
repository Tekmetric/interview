import { Page, Locator, expect } from '@playwright/test';

/**
 * Page Object for the Login page.
 * Represents the Login page for handling login-related actions.
 */
export class LoginPage {
    /** Locators for the login page: email, password, button, success and error indicators. */
    private readonly emailInput: Locator;
    private readonly passwordInput: Locator;
    private readonly loginButton: Locator;
    private readonly loggedInIndicator: Locator;
    private readonly loginErrorMessage: Locator;

    /**
     * Initializes a new instance of the LoginPage.
     * @param page The Playwright Page object for the login page.
     */
    constructor(private readonly page: Page) {
        this.emailInput = this.page.locator('input[data-qa="login-email"]');
        this.passwordInput = this.page.locator('input[data-qa="login-password"]');
        this.loginButton = this.page.locator('button[data-qa="login-button"]');
        this.loggedInIndicator = this.page.locator('a:has-text("Logged in as")');
        this.loginErrorMessage = this.page.locator('p:has-text("Your email or password is incorrect!")');
    }

    /**
     * Navigates to the login page and waits for it to load.
     * @throws Error if BASE_URL is not defined.
     */
    async navigate() {
        const baseUrl = process.env.BASE_URL;
        if (!baseUrl) {
            throw new Error('BASE_URL is not defined in .env');
        }
        await this.page.goto(`${baseUrl}/login`);
        await this.page.waitForLoadState('load');
    }

    /**
     * Performs login with the given credentials and waits for the page to load.
     * @param email The email to use.
     * @param password The password to use.
     */
    async login(email: string, password: string) {
        await this.emailInput.fill(email);
        await this.passwordInput.fill(password);
        await this.loginButton.click();
        await this.page.waitForLoadState('load');
    }

    /**
     * Verifies successful login by checking the logged-in indicator.
     * @param username The expected username.
     */
    async verifyLoginSuccess(username: string) {
        await expect(this.loggedInIndicator).toContainText(`Logged in as ${username}`);
    }

    /**
     * Verifies login failure by checking the error message.
     */
    async verifyLoginFailure() {
        await expect(this.loginErrorMessage).toContainText("Your email or password is incorrect!");
    }
}