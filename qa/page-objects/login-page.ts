import { Page } from '@playwright/test';

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

	// Returns the "Logged in as [username]" header
	getLoggedInHeader() {
		return this.page.locator('h2:has-text("Logged In")');
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

	// Returns true if login header is visible
	async validateLoggedinHeader() {
		return this.getLoggedInHeader().isVisible();
	}

	// Returns true if logout button is visible
	async validateLogoutButton() {
		return this.getLogoutButton().isVisible();
	}
	
	// Returns true if delete account button is visible
	async validateDeleteAccountButton() {
		return this.getDeleteAccountButton().isVisible();
	}

	// Logs in using the provided email and password
	async loginAs(email: string, password: string) {
		await this.navigateToLoginView();
		await this.enterEmail(email);
		await this.enterPassword(password);
		await this.clickLoginButton();
	}
}
