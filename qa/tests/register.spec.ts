import { test, expect } from '@playwright/test';

// Ideally randomize the name and email to avoid 'account already exists' prompt.

let randomNumber = Math.random().toString().slice(2)

let name = `qatester${randomNumber}`
let email = `qatest${randomNumber}@tester.com`
let password = 'Testing12'
let firstname = 'first'
let lastname = 'last'
let address = '370 Congress St'
let country = 'United States'
let state = 'Massachusetts'
let city = 'Boston'
let zipcode = '02210'
let phone = '6174445555'

test('get started link', async ({ page }) => {
    await page.goto('https://www.automationexercise.com/');

    // Click the 'Signup / Login' tab.
    await page.getByRole('link', { name: ' Signup / Login' }).click();

    // Fill out the name and email field.
    await page.getByTestId('signup-name').fill(name);
    await page.getByTestId('signup-email').fill(email);

    // Click the 'Signup' button.
    await page.getByTestId('signup-button').click();

    // Click the 'Mr' title.
    await page.locator('//*[@id="id_gender1"]').click();

    // Fill out the password field.
    await page.getByTestId('password').fill(password);

    // Fill out the Date of Birth.
    await page.getByTestId('days').selectOption('1');
    await page.getByTestId('months').selectOption('January');
    await page.getByTestId('years').selectOption('2000');

    // Sign up for newsletter and special offers.
    await page.locator('//*[@id="newsletter"]').check();
    await page.locator('//*[@id="optin"]').check();

    // Fill out first and last name.
    await page.getByTestId('first_name').fill(firstname);
    await page.getByTestId('last_name').fill(lastname);

    // Fill out address, country, state, city, zipcode, and phone number.
    await page.getByTestId('address').fill(firstname);
    await page.getByTestId('country').selectOption(country);
    await page.getByTestId('state').fill(state);
    await page.getByTestId('city').fill(city);
    await page.getByTestId('zipcode').fill(zipcode);
    await page.getByTestId('mobile_number').fill(phone);

    // Click the 'Create Account' button.
    await page.getByTestId('create-account').click();

    // Assert 'Account Created!' text appears.
    await expect(page.getByTestId('account-created')).toContainText('Account Created!');

    // Click continue button.
    await page.getByTestId('continue-button').click();

    // Assert Logout tab appears.
    await expect(page.getByRole('link', { name: ' Logout' })).toBeVisible();

});
