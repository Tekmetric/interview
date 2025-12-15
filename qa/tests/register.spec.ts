import { test, expect } from '@playwright/test';
import { RegistrationPage } from '../page/register_page'
import { HomePage } from '../page/home_page'
import { LoginPage } from '../page/login_page';
import { RegisterHelper } from '../lib/register_helper';

// Create an account via the UI.
test('Account Creation', async ({ page }) => {
    const home_page = new HomePage(page);
    const login_page = new LoginPage(page);
    const registration_page = new RegistrationPage(page);
    const register_helper = new RegisterHelper();

    // Go to automation exercise website.
    await home_page.goto();

    // Click the 'Signup / Login' tab.
    await home_page.signup_login.click();

    // Fill out the name and email field.
    await login_page.signup_name.fill(register_helper.name);
    await login_page.signup_email_address.fill(register_helper.email);

    // Click the 'Signup' button.
    await login_page.signup_button.click();

    // Click the 'Mr' or "Mrs" title.
    await registration_page.select_gender();

    // Fill out the password field.
    await registration_page.password.fill(register_helper.password);

    // Fill out the Date of Birth.
    await registration_page.dob_day.selectOption('1');
    await registration_page.dob_month.selectOption('January');
    await registration_page.dob_year.selectOption('2000');

    // Sign up for newsletter and special offers.
    await registration_page.newsletter.check();
    await registration_page.optin.check();

    // Fill out first and last name.
    await registration_page.firstname.fill(register_helper.firstname);
    await registration_page.lastname.fill(register_helper.lastname);

    // Fill out address, country, state, city, zipcode, and phone number.
    await registration_page.address.fill(register_helper.address);
    await registration_page.country.selectOption(register_helper.country);
    await registration_page.state.fill(register_helper.state);
    await registration_page.city.fill(register_helper.city);
    await registration_page.zipcode.fill(register_helper.zipcode);
    await registration_page.mobile_number.fill(register_helper.phone);

    // Click the 'Create Account' button.
    await registration_page.create_account_button.click();

    // Assert 'Account Created!' text appears.
    await expect(registration_page.account_created_text).toContainText('Account Created!');

    // Click continue button.
    await registration_page.continue_button.click();

    // Assert Logout tab appears.
    await expect(home_page.logout).toBeVisible();

});
