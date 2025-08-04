import { test, expect, request } from '@playwright/test';
import { NAME, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME,  COUNTRY, STATE, ADDRESS, CITY, ZIPCODE, 
  MOBILE_NUMBER, DOB_DAY, DOB_MONTH, DOB_YEAR, TITLE, COMPANY
} from './test_data';

// Test for logging in to an existing account
test('login user', async ({ page }) => {

// Creating an account via API
  // payload
  const data = {
    name: NAME,
    email: EMAIL,
    password: PASSWORD,
    title: TITLE,
    birth_date: DOB_DAY,
    birth_month: DOB_MONTH,
    birth_year: DOB_YEAR,
    firstname: FIRST_NAME,
    lastname: LAST_NAME,
    company: COMPANY,
    address1: ADDRESS,
    country: COUNTRY,
    zipcode: ZIPCODE,
    state: STATE,
    city: CITY,
    mobile_number: MOBILE_NUMBER
  };

  // Create API request context
  const apiContext = await request.newContext({
    baseURL: 'https://www.automationexercise.com'
  });

  // Sending POST request to register user account
  const response = await apiContext.post('/api/createAccount', {
    form: data
  });

  // Make sure the response is successful
  expect(response.status()).toBe(200);

  await page.goto('https://www.automationexercise.com/');

  await page.locator('[href="/login"]').click();
  await page.locator('[data-qa="login-email"]').fill(EMAIL);
  await page.locator('[data-qa="login-password"]').fill(PASSWORD);
  await page.locator('[data-qa="login-button"]').click();
  await expect(page.locator('#header')).toContainText(`Logged in as ${NAME}`);
  await page.locator('[href="/delete_account"]').click();
  await expect(page.locator('[data-qa="account-deleted"]')).toContainText('Account Deleted!');
  await page.locator('[data-qa="continue-button"]').click();
  await expect(page.locator('[href="/login"]')).toContainText('Signup / Login');
});