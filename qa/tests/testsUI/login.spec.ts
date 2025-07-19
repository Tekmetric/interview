import { test, expect } from '@playwright/test';
import { LoginPage } from '../pageObjects/LoginPage';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';
import { getApiUrl } from '../../utils/envHelpers';
import dotenv from 'dotenv';
import path from 'path';

// Load environment variables
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

const apiUrl = getApiUrl();

test.describe('Login Tests', () => {
  let email: string;
  const { password, firstname, lastname, invalidUser } = userData;
  let api: UserAccountAPI;
  let apiRequestContext: any;
  let loginPage: LoginPage;

  test.beforeAll(async ({ playwright }) => {
    apiRequestContext = await playwright.request.newContext();
    api = new UserAccountAPI(apiRequestContext, apiUrl);

    email = generateUniqueEmail();

    const response = await api.createUser(email, password, { ...userData, email });
    console.log('Create user response:', await response.json());
  });

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.navigateToLoginPage();
  });

  test('Verify all login page elements are displayed', async () => {
    await loginPage.verifyLoginUI();
  });

  test('Successful login with valid credentials', async () => {
    await loginPage.login(email, password);
    await loginPage.verifyLoggedInUser(firstname, lastname);
    await loginPage.logout();
  });

  test('Unsuccessful login with invalid credentials', async () => {
    await loginPage.login(invalidUser.email, invalidUser.password);
    await loginPage.verifyLoginFailure();
  });

  test.afterAll(async () => {
    const response = await api.deleteUser(email, password);
    console.log('Delete user response:', await response.json());
    await apiRequestContext.dispose();
  });
});
