import { test, expect, request } from '@playwright/test';
import { NAME, EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, NAME_ON_CARD, CARD_NUMBER, CVV, EXPIRY_MONTH, 
  EXPIRY_YEAR,  COUNTRY, STATE, ADDRESS, CITY, ZIPCODE, MOBILE_NUMBER, DOB_DAY, DOB_MONTH, DOB_YEAR, 
  TITLE, COMPANY
} from './test_data';

test('checkout process', async ({ page }) => {
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

  // CLick 'Register / Login'
  await page.locator('[href="/login"]').click();
  await page.locator('[data-qa="login-email"]').fill(EMAIL);
  await page.locator('[data-qa="login-password"]').fill(PASSWORD);
  await page.locator('[data-qa="login-button"]').click();

  // Navigate to Products and add items to cart
  await page.locator('[href="/products"]').click();
  // View Product 1 
  await page.locator('[href="/product_details/1"]').click();
  // Click 'Add to cart'
  await page.locator('[type="button"]').click();
  // Click 'View Cart' button
  await page.getByText('View Cart').click();
  // Proceed to checkout 
  await page.locator('[class="btn btn-default check_out"]').click();
  // click 'Place Order'
  await page.locator('[href="/payment"]').click();
  // Enter payment details: Name on Card, Card Number, CVC, Expiration date
  await page.locator('[name="name_on_card"]').fill(NAME_ON_CARD);
  await page.locator('[data-qa="card-number"]').fill(CARD_NUMBER); 
  await page.locator('[data-qa="cvc"]').fill(CVV);
  await page.locator('[data-qa="expiry-month"]').fill(EXPIRY_MONTH);
  await page.locator('[data-qa="expiry-year"]').fill(EXPIRY_YEAR);
  // Click 'Pay and Confirm Order'
  await page.locator('#submit').click();
  // Verify order confirmation
  await expect(page.locator('[data-qa="order-placed"]')).toContainText('Order Placed!');
  await page.locator('[href="/delete_account"]').click();
  await expect(page.locator('[data-qa="account-deleted"]')).toContainText('Account Deleted!');
  await page.locator('[data-qa="continue-button"]').click();
  await expect(page.locator('[href="/login"]')).toContainText('Signup / Login');
});