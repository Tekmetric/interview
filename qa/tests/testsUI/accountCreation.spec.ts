import { test } from '@playwright/test';
import { AccountPage } from '../pageObjects/AccountPage';
import { generateUniqueEmail } from '../../utils/generateUniqueEmail';
import * as userData from '../../testData/userData.json';
import { UserAccountAPI } from '../pageObjects/UserAccountAPI';
import { getApiUrl } from '../../utils/envHelpers';
import dotenv from 'dotenv';
import path from 'path';

// Load environment variables
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

const apiUrl = getApiUrl();

test.describe('Account Creation Tests', () => {
  let email: string;
  const { password, firstname, lastname, birth_date, birth_month, birth_year, company, address1, address2, country, state, city, zipcode, mobile_number } = userData;

  test.beforeEach(async ({ page }) => {
    const accountPage = new AccountPage(page);
    email = generateUniqueEmail();
    await accountPage.navigateToSignupPage();
  });

  test('Create account with generated details', async ({ page }) => {
    const accountPage = new AccountPage(page);

    await accountPage.createAccount(`${firstname} ${lastname}`, email);
    await accountPage.verifyAccountCreationRedirected();
    await accountPage.fillSignupForm({
      firstName: firstname,
      lastName: lastname,
      email: email,
      password: password,
      day: birth_date,
      month: birth_month,
      year: birth_year,
      company: company,
      address1: address1,
      address2: address2,
      country: country,
      state: state,
      city: city,
      zipCode: zipcode,
      mobileNumber: mobile_number,
    });
    await accountPage.verifyAccountCreation();
  });

  test.afterEach(async ({ playwright }) => {
    const apiRequestContext = await playwright.request.newContext();
    const api = new UserAccountAPI(apiRequestContext, apiUrl);

    const response = await api.deleteUser(email, password);
    console.log('API Cleanup: Delete user response:', await response.json());
    await apiRequestContext.dispose();
  });
});
