import { Page, expect, Locator } from '@playwright/test';

export class AccountPage {
  private page: Page;
  private selectors: Record<string, Locator>;

  constructor(page: Page) {
    this.page = page;
    this.selectors = {
      signupLoginLink: page.getByRole('link', { name: ' Signup / Login' }),
      nameInput: page.getByPlaceholder('Name'),
      emailInput: page.locator('form').filter({ hasText: 'Signup' }).getByPlaceholder('Email Address'),
      signupButton: page.getByRole('button', { name: 'Signup' }),
      titleMr: page.getByLabel('Mr.'),
      passwordInput: page.getByLabel('Password *'),
      dayDropdown: page.locator('#days'),
      monthDropdown: page.locator('#months'),
      yearDropdown: page.locator('#years'),
      newsletterCheckbox: page.getByLabel('Sign up for our newsletter!'),
      offersCheckbox: page.getByLabel('Receive special offers from'),
      firstNameInput: page.getByLabel('First name *'),
      lastNameInput: page.getByLabel('Last name *'),
      companyInput: page.getByLabel('Company', { exact: true }),
      address1Input: page.getByLabel('Address * (Street address, P.'),
      address2Input: page.getByLabel('Address 2'),
      countryDropdown: page.getByLabel('Country *'),
      stateInput: page.getByLabel('State *'),
      cityInput: page.getByLabel('City *'),
      zipCodeInput: page.locator('#zipcode'),
      mobileNumberInput: page.getByLabel('Mobile Number *'),
      createAccountButton: page.getByRole('button', { name: 'Create Account' }),
      accountCreatedText: page.getByText('Account Created!'),
      continueLink: page.getByRole('link', { name: 'Continue' }),
      deleteAccountLink: page.getByRole('link', { name: ' Delete Account' }),
      accountDeletedText: page.getByText('Account Deleted!'),
      accountDeletedConfirmationText: page.getByText('Your account has been'),
      accountCreationSuggestionText: page.getByText('You can create new account to')
    };
  }

  async navigateToSignupPage() {
    await this.page.goto('https://www.automationexercise.com');
    await expect(this.page).toHaveTitle(/Automation Exercise/);
    await this.selectors.signupLoginLink.click();
  }

  async createAccount(name: string, email: string) {
    await this.selectors.nameInput.fill(name);
    await this.selectors.emailInput.fill(email);
    await this.selectors.signupButton.click();
  }

  async verifyAccountCreationRedirected() {
    const accountCreationPrompt = this.page.locator('text=Enter Account Information');
    await expect(accountCreationPrompt).toBeVisible();
  }

  async fillSignupForm(details: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    day: string;
    month: string;
    year: string;
    company: string;
    address1: string;
    address2: string;
    country: string;
    state: string;
    city: string;
    zipCode: string;
    mobileNumber: string;
  }) {
    await this.selectors.titleMr.check();
    await this.selectors.passwordInput.fill(details.password);

    await this.selectors.dayDropdown.selectOption(details.day);
    await this.selectors.monthDropdown.selectOption(details.month);
    await this.selectors.yearDropdown.selectOption(details.year);

    await this.selectors.newsletterCheckbox.check();
    await this.selectors.offersCheckbox.check();

    await this.selectors.firstNameInput.fill(details.firstName);
    await this.selectors.lastNameInput.fill(details.lastName);
    await this.selectors.companyInput.fill(details.company);
    await this.selectors.address1Input.fill(details.address1);
    await this.selectors.address2Input.fill(details.address2);
    await this.selectors.countryDropdown.selectOption(details.country);
    await this.selectors.stateInput.fill(details.state);
    await this.selectors.cityInput.fill(details.city);
    await this.selectors.zipCodeInput.fill(details.zipCode);
    await this.selectors.mobileNumberInput.fill(details.mobileNumber);

    await this.selectors.createAccountButton.click();
  }

  async verifyAccountCreation() {
    await expect(this.selectors.accountCreatedText).toBeVisible();
    await expect(this.selectors.accountCreatedText).toHaveText('Account Created!');
  }

  async deleteAccount() {
    await this.selectors.deleteAccountLink.click();
    await expect(this.selectors.accountDeletedText).toBeVisible();
    await expect(this.selectors.accountDeletedConfirmationText).toBeVisible();
    await expect(this.selectors.accountCreationSuggestionText).toBeVisible();
    await this.selectors.continueLink.click();
  }
}
