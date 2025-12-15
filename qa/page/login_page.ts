import { expect, type Locator, type Page } from '@playwright/test';

export class LoginPage {
    readonly login_email_address: Locator;
    readonly login_password: Locator;
    readonly login_button: Locator;
    readonly signup_name: Locator;
    readonly signup_email_address: Locator;
    readonly signup_button: Locator;



    constructor(page: Page) {
        this.login_email_address = page.getByTestId('login-email');
        this.login_password = page.getByTestId('login-password');
        this.login_button = page.getByTestId('login-button');
        this.signup_name = page.getByTestId('signup-name');
        this.signup_email_address = page.getByTestId('signup-email');
        this.signup_button = page.getByTestId('signup-button');
    }

    async login(email: string, password: string) {
        // Fill out the email and password field.
        await this.login_email_address.fill(email);
        await this.login_password.fill(password);

        // Click the 'Login' button.
        await this.login_button.click();
    }
}