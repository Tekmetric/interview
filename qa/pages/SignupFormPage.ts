import { Page, Locator, expect } from "@playwright/test";

/**
 * SignupFormPage - Handles the account and address information form
 * URL: /signup (after initial name/email signup)
 * This is the second step of registration where user fills in detailed information
 */
export class SignupFormPage {
  readonly page: Page;

  // ===== ACCOUNT INFORMATION SECTION =====
  readonly accountInfoHeading: Locator;
  readonly nameInput: Locator;
  readonly emailInput: Locator;
  readonly passwordInput: Locator;

  // ===== ADDRESS INFORMATION SECTION =====
  readonly firstNameInput: Locator;
  readonly lastNameInput: Locator;
  readonly addressInput: Locator;
  readonly countryDropdown: Locator;
  readonly stateInput: Locator;
  readonly cityInput: Locator;
  readonly zipcodeInput: Locator;
  readonly mobileNumberInput: Locator;

  // ===== ACTIONS =====
  readonly createAccountButton: Locator;

  constructor(page: Page) {
    this.page = page;

    // Account information fields using getByRole (accessible)
    this.accountInfoHeading = page.getByRole("heading", {
      name: "Enter Account Information",
    });
    this.nameInput = page.getByRole("textbox", { name: "Name *", exact: true });
    this.emailInput = page.getByRole("textbox", {
      name: "Email *",
      exact: true,
    });
    this.passwordInput = page.getByRole("textbox", { name: "Password *" });

    // Address information fields
    this.firstNameInput = page.getByRole("textbox", { name: "First Name *" });
    this.lastNameInput = page.getByRole("textbox", { name: "Last Name *" });
    this.addressInput = page.getByRole("textbox", { name: "Address *" });
    this.countryDropdown = page.getByRole("combobox", { name: "Country *" });
    this.stateInput = page.getByRole("textbox", { name: "State *" });
    this.cityInput = page.getByRole("textbox", { name: "City *" });
    this.zipcodeInput = page.locator('[data-qa="zipcode"]'); // Using data-qa due to label issue
    this.mobileNumberInput = page.getByRole("textbox", {
      name: "Mobile Number *",
    });

    // Actions
    this.createAccountButton = page.getByRole("button", {
      name: "Create Account",
    });
  }

  /**
   * Navigate directly to signup form page
   * Note: Usually accessed via initial signup flow
   */
  async goto() {
    await this.page.goto("/signup");
    await this.accountInfoHeading.waitFor({ state: "visible" });
  }

  // ========================================
  // FORM FILLING METHODS
  // ========================================

  /**
   * Fill password field
   */
  async fillPassword(password: string) {
    await this.passwordInput.fill(password);
  }

  /**
   * Fill first name field
   */
  async fillFirstName(firstName: string) {
    await this.firstNameInput.fill(firstName);
  }

  /**
   * Fill last name field
   */
  async fillLastName(lastName: string) {
    await this.lastNameInput.fill(lastName);
  }

  /**
   * Fill address field
   */
  async fillAddress(address: string) {
    await this.addressInput.fill(address);
  }

  /**
   * Select country from dropdown
   */
  async selectCountry(country: string) {
    await this.countryDropdown.selectOption(country);
  }

  /**
   * Fill state field
   */
  async fillState(state: string) {
    await this.stateInput.fill(state);
  }

  /**
   * Fill city field
   */
  async fillCity(city: string) {
    await this.cityInput.fill(city);
  }

  /**
   * Fill zipcode field
   */
  async fillZipcode(zipcode: string) {
    await this.zipcodeInput.fill(zipcode);
  }

  /**
   * Fill mobile number field
   */
  async fillMobileNumber(mobileNumber: string) {
    await this.mobileNumberInput.fill(mobileNumber);
  }

  /**
   * Click Create Account button
   */
  async clickCreateAccountButton() {
    await this.createAccountButton.click();
  }

  // ========================================
  // COMBINED WORKFLOW METHODS
  // ========================================

  /**
   * Fill all required fields for account creation
   * @param data - User data object containing all required fields
   */
  async fillRequiredFields(data: {
    password: string;
    firstName: string;
    lastName: string;
    address: string;
    country: string;
    state: string;
    city: string;
    zipcode: string;
    mobileNumber: string;
  }) {
    await this.fillPassword(data.password);
    await this.fillFirstName(data.firstName);
    await this.fillLastName(data.lastName);
    await this.fillAddress(data.address);
    await this.selectCountry(data.country);
    await this.fillState(data.state);
    await this.fillCity(data.city);
    await this.fillZipcode(data.zipcode);
    await this.fillMobileNumber(data.mobileNumber);
  }

  /**
   * Complete the entire signup form - fill all fields and submit
   * Waits for navigation to account_created page
   * @param data - User data object
   * @returns true if successful, false if error occurs
   */
  async fillAndSubmitSignupForm(data: {
    password: string;
    firstName: string;
    lastName: string;
    address: string;
    country: string;
    state: string;
    city: string;
    zipcode: string;
    mobileNumber: string;
  }) {
    await this.fillRequiredFields(data);
    await this.clickCreateAccountButton();
  }
}
