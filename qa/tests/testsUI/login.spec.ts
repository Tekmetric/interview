import { test, expect } from '@playwright/test';
import { LoginPage } from '../pageObjects/LoginPage';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';

test.describe('Login Tests', () => {
  let email: string;
  const password = userData.password; // Extract password from userData
  let api: UserAccountAPI;
  let apiRequestContext: any;
  let loginPage: LoginPage; // Declare loginPage outside of hooks for broader scope

  test.beforeAll(async ({ playwright }) => {
    apiRequestContext = await playwright.request.newContext();
    api = new UserAccountAPI(apiRequestContext, 'https://automationexercise.com/api');

    email = generateUniqueEmail();

    const createUserResponse = await api.createUser(email, password, { ...userData, email });
    const createUserResponseData = await createUserResponse.json();
    console.log('API Setup: Create user response:', createUserResponseData);
  });

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page); // Initialize LoginPage before each test
    await loginPage.navigateToLoginPage(); // Navigate to the login page
  });

  test('Verify all login page elements are displayed', async () => {
    await loginPage.verifyLoginUI(); // Check UI elements
  });

  test('Successful login with valid credentials', async () => {
    await loginPage.login(email, password); // Use the email and password from setup

    // Verify successful login
    await loginPage.verifyLoggedInUser(userData.firstname, userData.lastname);

    await loginPage.logout();
  });

  test('Unsuccessful login with invalid credentials', async () => {
    const { email: invalidEmail, password: invalidPassword } = userData.invalidUser;

    await loginPage.login(invalidEmail, invalidPassword);

    // Verify login failure due to incorrect credentials
    await loginPage.verifyLoginFailure();
  });

  test.afterAll(async () => {
    const deleteUserResponse = await api.deleteUser(email, password);
    const deleteUserResponseData = await deleteUserResponse.json();
    console.log('API Cleanup: Delete user response:', deleteUserResponseData);

    await apiRequestContext.dispose();
  });
});
