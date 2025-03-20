import { test, expect } from '@playwright/test';
import {  LoginPage } from '../../page/LoginPage';

test('User Login Automation', async ({ page }) => {
    const loginPage = new LoginPage (page);

    // Navigate to Login Page
    await loginPage.navigate();

    // Perform Login
    await loginPage.login('alan-test@test.com', 'SecurePassword123');

    // Verify successful login
    await expect(page.locator('//a[contains(text(), "Logout")]')).toBeVisible();

});
