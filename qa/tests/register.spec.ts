import { test, expect } from '@playwright/test';
import { RegistrationPage } from '../page/register_page'
import { HomePage } from '../page/home_page'
import { LoginPage } from '../page/login_page';

// Randomize the name and email to avoid 'account already exists' prompt failing the tests.
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
    const home_page = new HomePage(page);
    const login_page = new LoginPage(page);
    const registration_page = new RegistrationPage(page);

    // Go to automation exercise website.
    await home_page.goto();

    // Click the 'Signup / Login' tab.
    await home_page.signup_login.click();

    // Fill out the name and email field.
    await login_page.signup_name.fill(name);
    await login_page.signup_email_address.fill(email);

    // Click the 'Signup' button.
    await login_page.signup_button.click();

    // Click the 'Mr' or "Mrs" title.
    await registration_page.select_gender();

    // Fill out the password field.
    await registration_page.password.fill(password);

    // Fill out the Date of Birth.
    await registration_page.dob_day.selectOption('1');
    await registration_page.dob_month.selectOption('January');
    await registration_page.dob_year.selectOption('2000');

    // Sign up for newsletter and special offers.
    await registration_page.newsletter.check();
    await registration_page.optin.check();

    // Fill out first and last name.
    await registration_page.firstname.fill(firstname);
    await registration_page.lastname.fill(lastname);

    // Fill out address, country, state, city, zipcode, and phone number.
    await registration_page.address.fill(address);
    await registration_page.country.selectOption(country);
    await registration_page.state.fill(state);
    await registration_page.city.fill(city);
    await registration_page.zipcode.fill(zipcode);
    await registration_page.mobile_number.fill(phone);

    // Click the 'Create Account' button.
    await registration_page.create_account_button.click();

    // Assert 'Account Created!' text appears.
    await expect(registration_page.account_created_text).toContainText('Account Created!');

    // Click continue button.
    await registration_page.continue_button.click();

    // Assert Logout tab appears.
    await expect(home_page.logout).toBeVisible();

});
