import { test, expect } from '@playwright/test';
import { AccountPage } from '../../pages/AccountPage';
import { Homepage } from '../../pages/Homepage';

test('Log in to account - Successful', async ({ page }) => {
  const homePage = new Homepage(page);
  const accountPage = new AccountPage(page);

  await homePage.goto();

  // Click on 'Signup/Login' link
  await homePage.goToSignupLogin();

  // Fill Login email/password fields
  await expect(accountPage.loginForm).toBeVisible();
  await accountPage.loginEmail.fill('ektest@test.com');
  await accountPage.loginPassword.fill('123test');

  // Click 'Login' CTA
  await accountPage.loginButton.click();

  // Expect to be logged in - Logout link / 'Logged in as 'name'' now present in top nav
  await expect(homePage.logoutLink).toBeVisible();
  await expect(homePage.userInfo).toBeVisible();
});

test('Log in to account - Unsuccessful', async({page}) => {
    const homePage = new Homepage(page);
    const accountPage = new AccountPage(page);

    await homePage.goto();

    // Click on 'Signup/Login' link
    await homePage.goToSignupLogin();

    // Fill Login email/password fields with incorrect value
    await expect(accountPage.loginForm).toBeVisible();
    await accountPage.loginEmail.fill('ektest@test.com');
    await accountPage.loginPassword.fill('wrongpassword');

    // Click 'Login' CTA
    await accountPage.loginButton.click();
  
    // Expect error text
    await expect(accountPage.loginError).toHaveText('Your email or password is incorrect!');
});
