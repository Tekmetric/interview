import { test, expect, Page } from '@playwright/test';
import { LoginPage } from '../pages/loginPage';
import { HomePage } from '../pages/homePage';
import 'dotenv/config';

test('Home page loads', async ({ page }) => {
    const homePage = new HomePage(page);

    // Go to page
    await homePage.goToHomePage();
});

test('Sign up page loads', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);

    // Go to page
    await homePage.goToHomePage();

    // Click login link
    await loginPage.clickLoginLink();  
});

test('Create account', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);

    // Go to page
    await homePage.goToHomePage();

    // Click login link
    await loginPage.clickLoginLink();  

    // Fill in fields
    await page.getByRole('textbox', { name: 'Name' }).fill(process.env.NAME);

    // Use a unique email address
    await page.locator('form').filter({ hasText: 'Signup' }).getByPlaceholder('Email Address').fill(Date.now() + '@test.com');

    // Click signup button
    await page.getByRole('button', { name: 'Signup' }).click();

    // Expect enter account information page to be visible
    await expect(page.getByText('Enter Account Information')).toBeVisible();

    // Fill in fields
    await page.getByRole('radio', { name: 'Mrs.' }).check();
    await page.getByRole('textbox', { name: 'Password *' }).fill(process.env.PASSWORD);
    await page.locator('#days').selectOption('30');
    await page.locator('#months').selectOption('4');
    await page.locator('#years').selectOption('2000');
    await page.getByRole('textbox', { name: 'First name *' }).fill('Alyssa');
    await page.getByRole('textbox', { name: 'Last name *' }).fill('Tester');
    await page.getByRole('textbox', { name: 'Company', exact: true }).fill('Test');
    await page.getByRole('textbox', { name: 'Address * (Street address, P.' }).fill('1 Main St');
    await page.getByLabel('Country *').selectOption('United States');
    await page.getByRole('textbox', { name: 'State *' }).fill('Massachusetts');
    await page.getByRole('textbox', { name: 'City * Zipcode *' }).fill('Boston');
    await page.locator('#zipcode').fill('20110');
    await page.getByRole('textbox', { name: 'Mobile Number *' }).fill('5555555555');

    // Click to create account
    await page.getByRole('button', { name: 'Create Account' }).click();

    // Expect account created page to be visible
    await expect(page.getByText('Account Created!')).toBeVisible();

    // Click to continue
    await page.getByRole('link', { name: 'Continue' }).click();

    // Expect header to show logged in information
    await expect(page.locator('#header')).toContainText('Logged in as ' + process.env.NAME);

    // Log out
    await homePage.clickLogoutLink();  
});

test('Log in to account', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);
    
    // Go to page
    await homePage.goToHomePage();

    // Sign in
    await loginPage.loginSuccess(process.env.EMAIL, process.env.PASSWORD, process.env.NAME);  

    // Log out
    await homePage.clickLogoutLink();  
});   

test('Create account failed', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);
    
    // Go to page
    await homePage.goToHomePage();

    // Click login link
    await loginPage.clickLoginLink();  

    // Fill in fields with duplicate info
    await page.getByRole('textbox', { name: 'Name' }).fill(process.env.NAME);
    await page.locator('form').filter({ hasText: 'Signup' }).getByPlaceholder('Email Address').fill(process.env.EMAIL);
    await page.getByRole('button', { name: 'Signup' }).click();

    // Expect error message for signup to be visible
    await expect(page.getByText('Email Address already exist!')).toBeVisible();
});

test('Log in to account failed', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);
    
    // Go to page
    await homePage.goToHomePage();

    // Click login link
    await loginPage.clickLoginLink();  

    // Fll in fields
    await page.locator('form').filter({ hasText: 'Login' }).getByPlaceholder('Email Address').fill(process.env.EMAIL);
    await page.getByRole('textbox', { name: 'Password' }).fill('test');

    // Click login button
    await page.getByRole('button', { name: 'Login' }).click();

    // Expect error message for login to be visible
    await expect(page.getByText('Your email or password is')).toBeVisible();
});

test('Checkout', async ({ page }) => {
    const homePage = new HomePage(page);
    const loginPage = new LoginPage(page);
    
    // Go to page
    await homePage.goToHomePage();

    // Sign in
    await loginPage.loginSuccess(process.env.EMAIL, process.env.PASSWORD, process.env.NAME);  

    // Go to Products page
    await page.getByRole('link', { name: 'Products' }).click();
   
    // View a product
    await page.locator('div:nth-child(6) > .product-image-wrapper > .choose > .nav > li > a').click();
  
    // Add to cart
    await page.getByRole('button', { name: 'Add to cart' }).click();

    // Expect Added header to be visible
    await expect(page.getByRole('heading', { name: 'Added!' })).toBeVisible();

    // Click to view cart
    await page.getByRole('link', { name: 'View Cart' }).click();

    // Expect cart to be visible
    await expect(page.getByText('Shopping Cart')).toBeVisible();

    // Click to checkout
    await page.getByText('Proceed To Checkout').click();

    // Expect checkout page to be visible
    await expect(page.getByText('Checkout')).toBeVisible();

    // Click to place order
    await page.getByRole('link', { name: 'Place Order' }).click();

    // Expect payment page to show
    await expect(page.getByRole('listitem').filter({ hasText: 'Payment' })).toBeVisible();

    // Fill in fields
    await page.locator('input[name="name_on_card"]').fill(process.env.NAME);
    await page.locator('input[name="card_number"]').fill(process.env.CARD_NUMBER);
    await page.getByRole('textbox', { name: 'ex.' }).fill(process.env.CARD_SECURITY);
    await page.getByRole('textbox', { name: 'MM' }).fill(process.env.CARD_EXP_MONTH);
    await page.getByRole('textbox', { name: 'YYYY' }).fill(process.env.CARD_EXP_YEAR);

    // Click to place order
    await page.getByRole('button', { name: 'Pay and Confirm Order' }).click();

    // Expect success page to be visible
    await expect(page.getByText('Order Placed!')).toBeVisible();

    // Navigate back to home page and log out
    await page.getByRole('link', { name: 'Continue' }).click();
    await homePage.clickLogoutLink();  
});