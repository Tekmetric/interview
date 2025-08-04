import { test, expect} from '@playwright/test';
import { NAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, COUNTRY, STATE, ADDRESS, CITY, ZIPCODE, 
  MOBILE_NUMBER, DOB_DAY, DOB_MONTH, DOB_YEAR} from './test_data';

// Test for creating an account
test('register user', async ({ page }) => {
  await page.goto('https://www.automationexercise.com/');
  await page.locator('[href="/login"]').click();  
  await page.locator('[data-qa="signup-name"]').fill(NAME);
  await page.locator('[data-qa="signup-email"]').fill(EMAIL);
  await page.locator('[data-qa="signup-button"]').click();
  await page.locator('#password').fill(PASSWORD);
  await page.locator('#days').selectOption(DOB_DAY);
  await page.locator('#months').selectOption(DOB_MONTH);
  await page.locator('#years').selectOption(DOB_YEAR);
  await page.locator('#first_name').fill(FIRST_NAME);
  await page.locator('#last_name').fill(LAST_NAME); 
  await page.locator('#address1').fill(ADDRESS);
  await page.locator('#country').selectOption(COUNTRY);
  await page.locator('#state').fill(STATE);
  await page.locator('#city').fill(CITY);
  await page.locator('#zipcode').fill(ZIPCODE);
  await page.locator('#mobile_number').fill(MOBILE_NUMBER);
  await page.locator('[data-qa="create-account"]').click();
  await expect(page.locator('[data-qa="account-created"]')).toContainText('Account Created!');
  await page.locator('[data-qa="continue-button"]').click();
  await expect(page.locator('#header')).toContainText(`Logged in as ${NAME}`);

  await page.locator('[href="/delete_account"]').click();
  await expect(page.locator('[data-qa="account-deleted"]')).toContainText('Account Deleted!');
  await page.locator('[data-qa="continue-button"]').click();
  await expect(page.locator('[href="/login"]')).toContainText('Signup / Login');
});