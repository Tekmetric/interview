import { Page } from '@playwright/test';

export class SignupPage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  // Navigates to the initial signup page
  async navigateToInitialSignupView() {
    await this.page.goto('https://www.automationexercise.com/login');
  }

  // Navigates to the secondary signup page
  async navigateToSecondarySignupView() {
    await this.page.goto('https://www.automationexercise.com/signup');
  }

  // Returns the password input field
  getPasswordInput() {
    return this.page.locator('input[data-qa="password"]');
  }

  // Returns the first name input field
  getFirstNameInput() {
    return this.page.locator('input[data-qa="first_name"]');
  }

  // Returns the last name input field
  getLastNameInput() {
    return this.page.locator('input[data-qa="last_name"]');
  }

  // Returns the address input field
  getAddressInput() {
    return this.page.locator('input[data-qa="address"]');
  }

  // Returns the city input field
  getCityInput() {
    return this.page.locator('input[data-qa="city"]');
  }

  getStateInput() {
    return this.page.locator('input[data-qa="state"]');
  }

  // Returns the zipcode input field
  getZipcodeInput() {
    return this.page.locator('input[data-qa="zipcode"]');
  }

  // Returns the mobile number input field
  getMobileNumberInput() {
    return this.page.locator('input[data-qa="mobile_number"]');
  }

  // Returns the signup form
  getSignupForm() {
    return this.page.locator('form[action="/signup"]');
  }

  // Returns the name input field
  getSignupNameInput() {
    return this.page.locator('input[data-qa="signup-name"]');
  }

  // Returns the email input field
  getSignupEmailInput() {
    return this.page.locator('input[data-qa="signup-email"]');
  }

  // Returns the signup button
  getSignupButton() {
    return this.page.locator('button[data-qa="signup-button"]');
  }

  // Returns the create account button
  getCreateAccountButton() {
    return this.page.locator('button[data-qa="create-account"]');
  }

  // Returns the "Account Created!" header
  getAccountCreatedHeader() {
    return this.page.locator('h2:has-text("Account Created!")');
  }

  // Returns the congratulations message
  getCongratulationsMessage() {
    return this.page.locator('p:has-text("Congratulations! Your new account has been successfully created!")');
  }

  // Returns the member privileges message
  getMemberPrivilegesMessage() {
    return this.page.locator('p:has-text("You can now take advantage of member privileges to enhance your online shopping experience with us.")');
  }

  // Returns the continue button
  getContinueButton() {
    return this.page.locator('a[data-qa="continue-button"]');
  }

  // Returns the "Enter Account Information" header
  getEnterAccountInformationHeader() {
    return this.page.locator('h2:has-text("Enter Account Information")');
  }

  // Fills in the signup name and email, then submits the form
  async enterSignupName(name: string) {
    await this.getSignupNameInput().fill(name);
  }

  // Fills in the signup name and email
  async enterSignupEmail(email: string) {
    await this.getSignupEmailInput().fill(email);
  }

  // Clicks the signup button
  async clickSignupButton() {
    await this.getSignupButton().click();
  }

  // Fills out the secondary signup form and submits it
  async fillSecondarySignupForm({ password, firstName, lastName, address, city, state, zipcode, mobileNumber }) {
    await this.getPasswordInput().fill(password);
    await this.getFirstNameInput().fill(firstName);
    await this.getLastNameInput().fill(lastName);
    await this.getAddressInput().fill(address);
    await this.getCityInput().fill(city);
    await this.getStateInput().fill(state);
    await this.getZipcodeInput().fill(zipcode);
    await this.getMobileNumberInput().fill(mobileNumber);
    await this.getCreateAccountButton().click();
  }

  // Performs the initial signup process
  async signup(name: string, email: string) {
    // Navigate to the initial signup view
    await this.navigateToInitialSignupView();

    // Fill in the initial signup form
    await this.enterSignupName(name);
    await this.enterSignupEmail(email);

    // Submit the initial signup form
    await this.clickSignupButton();
  }
}
