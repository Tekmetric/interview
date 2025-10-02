import { test, expect } from '@playwright/test';
import { SignupPage } from '../page-objects/signup-page';

// Example test data
const testName = 'Test User';
const testEmail = 'testuser' + Date.now() + '@example.com';

test.describe('Signup Flow', () => {
  test('should sign up a new user', async ({ page }) => {
    // Create an instance of SignupPage
    const signupPage = new SignupPage(page);

    // Navigate to the signup page and perform signup
    await signupPage.signup(testName, testEmail);

    // Verify navigation to secondary signup form
    await expect(signupPage.getEnterAccountInformationHeader()).toBeVisible();
  });

  test('should display signup form on /signup page', async ({ page }) => {
    // Create an instance of SignupPage
    const signupPage = new SignupPage(page);

    // TODO: Investigate why this isn't working
    // signupPage.navigateToSecondarySignupView;

    // Navigate to the signup page
    await page.goto('https://www.automationexercise.com/signup');

    // Verify that signup form elements are visible
    await expect(signupPage.getSignupForm()).toBeVisible();
    await expect(signupPage.getSignupNameInput()).toBeVisible();
    await expect(signupPage.getSignupEmailInput()).toBeVisible();
    await expect(signupPage.getSignupButton()).toBeVisible();
  });

  test('should have correct placeholders in signup form', async ({ page }) => {
    // Create an instance of SignupPage
    const signupPage = new SignupPage(page);

    // TODO: Investigate why this isn't working
    // signupPage.navigateToSecondarySignupView;

    // Navigate to the signup page
    await page.goto('https://www.automationexercise.com/signup');

    // Verify that the input fields have correct placeholders
    await expect(signupPage.getSignupNameInput()).toHaveAttribute('placeholder', 'Name');
    await expect(signupPage.getSignupEmailInput()).toHaveAttribute('placeholder', 'Email Address');
  }); 

  test('should fill out the secondary signup form', async ({ page }) => {
    // Create an instance of SignupPage
    const signupPage = new SignupPage(page);

    // Navigate to the signup page and perform signup
    await signupPage.signup(testName, testEmail);

    // Fill out the secondary signup form
    await signupPage.fillSecondarySignupForm({
      password: 'testpassword',
      firstName: 'Test',
      lastName: 'User',
      address: '123 Test St',
      city: 'Testville',
      state: 'Test State',
      zipcode: '12345',
      mobileNumber: '1234567890',
    });

    // Verify account creation success
    await expect(signupPage.getAccountCreatedHeader()).toBeVisible();
    await expect(signupPage.getCongratulationsMessage()).toBeVisible();
    await expect(signupPage.getMemberPrivilegesMessage()).toBeVisible();
    await expect(signupPage.getContinueButton()).toBeVisible();

    // Click continue and verify redirection to home page
    await signupPage.getContinueButton().click();

    // Verify redirection to home page
    await expect(signupPage.page).toHaveURL('https://www.automationexercise.com/');
  }); 
});
