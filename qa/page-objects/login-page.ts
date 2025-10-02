import { Page } from '@playwright/test';
import { LoginApi } from '../api/login.api';

export class LoginPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	// Navigates to the login page
	async navigateToLoginView() {
		await this.page.goto('https://www.automationexercise.com/login');
	}

	// Returns the email input field
	getEmailInput() {
		return this.page.locator('input[data-qa="login-email"]');
	}

	// Returns the password input field
	getPasswordInput() {
		return this.page.locator('input[data-qa="login-password"]');
	}

	// Returns the login button
	getLoginButton() {
		return this.page.locator('button[data-qa="login-button"]');
	}

	// Returns the logged in header
	getLoggedInHeader() {
		return this.page.locator('a:has-text("Logged in as")');
	}

	// Returns the logout button
	getLogoutButton() {
		return this.page.locator('a[href="/logout"]');
	}

	// Returns the delete account button
	getDeleteAccountButton() {
		return this.page.locator('a[href="/delete_account"]');
		
	}

	// Returns the login error message
	getloginErrorMessage() {
		return this.page.locator('p:has-text("Your email or password is incorrect!")');
	}

	// Fills in the email field
	async enterEmail(email: string) {
		await this.getEmailInput().fill(email);
	}

	// Fills in the password field
	async enterPassword(password: string) {
		await this.getPasswordInput().fill(password);
	}

	// Clicks the login button
	async clickLoginButton() {
		await this.getLoginButton().click();
	}

	// Returns true if the login error message is visible
	async validateLoginErrorMessage() {
		return this.getloginErrorMessage().isVisible();
	}

	/**
	 * Validates if the logout button is visible
	 * @param options Optional configuration for validation
	 * @returns Promise<boolean> true if button is visible
	 * @throws Error if the validation fails unexpectedly
	 */
	async validateLogoutButton(options = { timeout: 5000 }): Promise<boolean> {
		try {
			const button = this.getLogoutButton();
			await button.waitFor({ state: 'visible', timeout: options.timeout });
			return true;
		} catch (error) {
			throw new Error(`Failed to validate logout button: ${error.message}`);
		}
	}
	
	/**
	 * Validates if the delete account button is visible and accessible
	 * @param options Optional configuration for validation
	 * @returns Promise<boolean> true if button is visible and accessible
	 * @throws Error if the validation fails unexpectedly
	 */
	async validateDeleteAccountButton(options = { timeout: 5000 }): Promise<boolean> {
		try {
			const button = this.getDeleteAccountButton();
			await button.waitFor({ state: 'attached', timeout: options.timeout });
			const [isVisible, isEnabled] = await Promise.all([
				button.isVisible(),
				button.isEnabled()
			]);
			return isVisible && isEnabled;
		} catch (error) {
			throw new Error(`Failed to validate delete account button: ${error.message}`);
		}
	}

	// Logs in using the provided email and password
	async loginAs(email: string, password: string) {
		await this.navigateToLoginView();
		await this.enterEmail(email);
		await this.enterPassword(password);
		await this.clickLoginButton();
	}

	/**
	 * Logs in using the API instead of the UI
	 * @param email User's email
	 * @param password User's password
	 * @returns Promise<boolean> true if login was successful
	 * @throws Error if login fails
	 */
	async loginViaApi(email: string, password: string): Promise<boolean> {
		const api = new LoginApi();
		const response = await api.verifyLogin(email, password);

		if (response.responseCode !== 200) {
			throw new Error(`API login failed: ${response.message}`);
		}

		// Navigate to the site after successful API login
		await this.navigateToLoginView();
		return true;
	}
}
