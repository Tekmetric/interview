import { test, expect } from '@playwright/test';
import { HomePage } from '../page/home_page'
import { LoginPage } from '../page/login_page';
import { RegisterHelper } from '../lib/register_helper';

// Logs in to an existing account.
test('Account Login', async ({ page }) => {
    const home_page = new HomePage(page);
    const login_page = new LoginPage(page);

    // Go to automation exercise website.
    await home_page.goto();

    // Click the 'Signup / Login' tab.
    await home_page.signup_login.click();

    // Fill out the email and password field.
    await login_page.login_email_address.fill("qatest@tester.com");
    await login_page.login_password.fill("Testing12");

    // Click the 'Login' button.
    await login_page.login_button.click();

    // Assert Logout tab appears.
    await expect(home_page.logout).toBeVisible();

});

// Logs in to an account created via the API.
test('Account Login with API registration', async ({ page }) => {
    const home_page = new HomePage(page);
    const login_page = new LoginPage(page);
    const register_helper = new RegisterHelper();

    // Create account via API.
    await register_helper.api_create_account();

    // Go to automation exercise website.
    await home_page.goto();

    // Click the 'Signup / Login' tab.
    await home_page.signup_login.click();

    // Fill out the email and password field.
    await login_page.login_email_address.fill(register_helper.email);
    await login_page.login_password.fill(register_helper.password);

    // Click the 'Login' button.
    await login_page.login_button.click();

    // Assert Logout tab appears.
    await expect(home_page.logout).toBeVisible();

});
