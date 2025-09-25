import { test, expect } from '@playwright/test';
import { AccountPage } from '../../pages/AccountPage';
import { Homepage } from '../../pages/Homepage';
import { SignupPage } from '../../pages/SignupPage';

test('Create Account', async ({ page }) => {
    const homePage = new Homepage(page);
    const accountPage = new AccountPage(page);
    const signupPage = new SignupPage(page);
    
    await homePage.goto();
    await homePage.goToSignupLogin();

    await expect(accountPage.signupForm).toBeVisible();
    await accountPage.signupName.fill('Name');
    await accountPage.signupEmail.fill('ektestCreateAccount@test.com');

    await accountPage.signupButton.click();

    // Fill required fields
    await signupPage.fillAccountInformation();
    await signupPage.fillAddressInformation();

    // Click 'Create Account' CTA
    await signupPage.createAccountButton.click();
    await expect(signupPage.accountCreatedConfirmation).toBeVisible();
    
    // Logout link should be visible after returning to homepage
    await signupPage.continueButton.click();
    await expect(homePage.logoutLink).toBeVisible();

    // Delete account after test
    await homePage.deleteAccount();
});
