// testsUI/accountUi.spec.ts
import { test } from '@playwright/test';
import { AccountPage } from '../pageObjects/AccountPage';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';

test.describe('Account Creation Tests', () => {
  let email: string;
  const password = userData.password; // Extract password from user data

  test.beforeEach(async ({ page }) => {
    const accountPage = new AccountPage(page);

    // Generate a unique email for the test run
    email = generateUniqueEmail();

    await accountPage.navigate();
  });

  test('Create account with generated details', async ({ page }) => {
    const accountPage = new AccountPage(page);

    // Conduct UI interactions for account creation
    await accountPage.createAccount(`${userData.firstname} ${userData.lastname}`, email, password);

    // Verify account was created successfully
    await accountPage.verifyAccountCreationRedirected();
    await accountPage.fillSignupForm(userData.firstname, userData.lastname, email, password);
    await accountPage.verifyAccountCreation();
  });

  test.afterEach(async ({ playwright }) => {
    // Cleanup using API call
    const apiRequestContext = await playwright.request.newContext();
    const api = new UserAccountAPI(apiRequestContext, 'https://automationexercise.com/api');

    // Use the API to delete the user
    const deleteUserResponse = await api.deleteUser(email, password);
    const deleteUserResponseData = await deleteUserResponse.json();

    console.log('API Cleanup: Delete user response:', deleteUserResponseData);

    await apiRequestContext.dispose();
  });
});