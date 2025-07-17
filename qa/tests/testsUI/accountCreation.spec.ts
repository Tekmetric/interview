import { test } from '@playwright/test';
import { AccountPage } from '../pageObjects/AccountPage';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';

test.describe('Account Creation Tests', () => {
  let email: string;
  const password = userData.password;

  test.beforeEach(async ({ page }) => {
    const accountPage = new AccountPage(page);

    // Generate a unique email for each test
    email = generateUniqueEmail();

    // Navigate directly to the signup page
    await accountPage.navigateToSignupPage();
  });

  test('Create account with generated details', async ({ page }) => {
    const accountPage = new AccountPage(page);

    // Commence account creation process
    await accountPage.createAccount(`${userData.firstname} ${userData.lastname}`, email);

    // Verify navigation to account information entry page
    await accountPage.verifyAccountCreationRedirected();

    // Fill in the signup form with user details
    await accountPage.fillSignupForm({
      firstName: userData.firstname,
      lastName: userData.lastname,
      email: email,
      password: password,
      day: userData.birth_date,
      month: userData.birth_month,
      year: userData.birth_year,
      company: userData.company,
      address1: userData.address1,
      address2: userData.address2,
      country: userData.country,
      state: userData.state,
      city: userData.city,
      zipCode: userData.zipcode,
      mobileNumber: userData.mobile_number,
    });

    // Validate successful account creation
    await accountPage.verifyAccountCreation();
  });

  test.afterEach(async ({ playwright }) => {
    // Instantiate a new API request context for cleanup operations
    const apiRequestContext = await playwright.request.newContext();
    const api = new UserAccountAPI(apiRequestContext, 'https://automationexercise.com/api');

    // Utilize the API to delete the test account
    const deleteUserResponse = await api.deleteUser(email, password);
    const deleteUserResponseData = await deleteUserResponse.json();

    // Log outcome of deletion for troubleshooting
    console.log('API Cleanup: Delete user response:', deleteUserResponseData);

    // Dispose of API request context post cleanup
    await apiRequestContext.dispose();
  });
});
