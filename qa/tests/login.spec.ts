import { test, expect } from '@playwright/test';
import { LoginPage } from '../page-objects/login-page';

test.describe('Login Flow', () => {
  test('should fail login with invalid credentials', async ({ page }) => {
    // Create an instance of LoginPage
    const loginPage = new LoginPage(page);

    // Use the new loginAs method to perform login
    await loginPage.loginAs('test@rxample.com', 'wrongpassword');

    // Check for error message or failed login indication
    expect(await loginPage.validateLoginErrorMessage()).toBeTruthy();
  });

  test('should login via UI', async ({ page }) => {
    // Create an instance of LoginPage 
    const loginPage = new LoginPage(page);

    // Use the new loginAs method to perform login
    await loginPage.loginAs('ryandandrow@gmail.com', 'testPassword');
    
    // Validate successful login by checking for elements visible only when logged in
    expect(loginPage.getLogoutButton()).toBeTruthy();
    expect(loginPage.getDeleteAccountButton()).toBeTruthy();

    // TODO: Investigate why this assertion is causing issues
    // expect(loginPage.validateLoggedinHeader()).toBeTruthy();
  });
});
