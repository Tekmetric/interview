import { expect, test } from '@playwright/test';
import { fillForm, clickElement } from '../util/Reusable_Methods/Reusable_Methods';
    

//Creating an account on https://www.automationexercise.com/
test('Creating an Account', async ({page}) => {

    //navigating to the URL
    await page.goto('https://www.automationexercise.com/');
    //clicking on sign in/login button
    await clickElement(page, '[href="/login"]');
    const timestamp = Date.now();
    const uniqueEmail = `Account${timestamp}@testing.com`;
    //filling the name and email fields with unique email
    await fillForm(page, '[data-qa="signup-name"]', 'Creating');
    await fillForm(page, '[data-qa="signup-email"]', uniqueEmail);
    //clicking on signup button
    await clickElement(page, '[data-qa="signup-button"]');
    //selecting tittle radio option Mrs.
    await clickElement(page,'[id="id_gender2"]');
    //filling the password field
    await fillForm(page, '[id="password"]', '1234567890');
    //selecting DOB from dropdowns
    await page.selectOption('[id="days"]', '15');
    await page.selectOption('[id="months"]', 'December');
    await page.selectOption('[id="years"]', '1997');
    //checking the checkboxes for the newsletter and special offers
    await clickElement(page, '[id="newsletter"]');
    await clickElement(page, '[id="optin"]');
    //filling the first name and last name fields
    await fillForm(page, '[id="first_name"]', 'Creating');
    await fillForm(page, '[id="last_name"]', 'Account');
    //filling the company field
    await fillForm(page, '[id="company"]', 'Account Creating Company');
    //filling the address field
    await fillForm(page, '[id="address1"]', '123 Test avenue');
    //filling the address2 field
    await fillForm(page, '[id="address2"]', '1st floor');
    //filling the country field
    await page.selectOption('[id="country"]', 'United States');
    //filling the state field
    await fillForm(page, '[id="state"]', 'Texas');
    //filling the city field
    await fillForm(page, '[id="city"]', 'Houston');
    //filling the zipcode field
    await fillForm(page, '[id="zipcode"]', '77002');
    //filling the mobile number field
    await fillForm(page, '[id="mobile_number"]', '1234567890');
    //clicking on create account button
    await clickElement(page, '[data-qa="create-account"]');
    //capturing and validating the account created message
    const text = await page.locator('.col-sm-9.col-sm-offset-1').textContent();
    expect(text).toContain('Account Created!');
    //printing the captured text
    console.log('Account created successfully');
    //clicking on continue button
    await clickElement(page, '[data-qa="continue-button"]');

})//end of test 1




//test 2 for login functionality
test('Login Functionality Test', async ({page}) => {
   
    //navigating to the URL
    await page.goto('https://www.automationexercise.com/');
    //clicking on sign in/login button
    await clickElement(page, '[href="/login"]');
    //filling the email and password fields
    await fillForm(page, '[data-qa="login-email"]', 'lenny@testing.com');
    await fillForm(page, '[data-qa="login-password"]', '1234567890');
    //clicking on login button
    await clickElement(page, '[data-qa="login-button"]');
    //Verify login was successful 
    await expect(page.locator('text=Logged in as')).toBeVisible();
    console.log('Login was successful');
    
    })//end of test 2 