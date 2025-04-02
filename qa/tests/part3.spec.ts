import { test, expect, request } from '@playwright/test';
import { LoginPage } from '../pages/loginPage';
import { HomePage } from '../pages/homePage';
import 'dotenv/config';

test('Log in to created account from API', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);
    const accountEmail = Date.now() + '@test.com';
    const accountPassword = process.env.PASSWORD;
    const accountName = process.env.NAME;

    // Create account via API
    // Establish base url
    const context = await request.newContext({
        baseURL: process.env.BASE_API_URL,
    });
    
    // Set fields
    const form = new FormData();
    form.set('name', accountName);
    form.set('email', accountEmail);
    form.set('password', accountPassword);
    form.set('title', 'Mrs');
    form.set('birth_date', '4');
    form.set('birth_month', 'May');
    form.set('birth_year', '2001');
    form.set('firstname', 'Alyssa');
    form.set('lastname', 'API');
    form.set('company', 'Test');
    form.set('address1', '1 Main St');
    form.set('address2', '123');
    form.set('country', 'United States');
    form.set('zipcode', '20010');
    form.set('state', 'Massachusetts');
    form.set('city', 'Boston');
    form.set('mobile_number', '5555555555');

    // Create account
    const response = await context.post('createAccount', {
        multipart: form
    });

    // Expect successful response
    const responseBody = await response.json();
    await expect(responseBody.responseCode).toEqual(201);
    await expect(responseBody.message).toContain('User created!');

    // Log in to created account via UI  
    // Go to page
    await homePage.goToHomePage();

    // Sign in
    await loginPage.loginSuccess(accountEmail, accountPassword, accountName);  
});