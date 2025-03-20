import { expect, Page } from '@playwright/test';

export class SignupPage {
    private page: Page;

    // Selectors
    private titleMr = 'input[id="id_gender1"][value="Mr"]';
    private titleMrs = 'input[id="id_gender2"][value="Mrs"]';
    private nameInput = 'input[id="name"]';
    private emailInput = 'input[id="email"]';
    private passwordInput = 'input[id="password"]';

    private dobDay = 'select[id="days"]';
    private dobMonth = 'select[id="months"]';
    private dobYear = 'select[id="years"]';

    private newsletterCheckbox = 'input[id="newsletter"]';
    private specialOffersCheckbox = 'input[id="optin"]';

    private firstNameInput = 'input[id="first_name"]';
    private lastNameInput = 'input[id="last_name"]';
    private companyInput = 'input[id="company"]';
    private addressInput = 'input[id="address1"]';
    private countryDropdown = 'select[id="country"]';
    private stateInput = 'input[id="state"]';
    private cityInput = 'input[id="city"]';
    private zipcodeInput = 'input[id="zipcode"]';
    private mobileNumberInput = 'input[id="mobile_number"]';

    //private createAccountButton = 'button:has-text("Create Account")';
    private createAccountButton = '//button[@data-qa="create-account"]';
    private accountCreatedMsgHeader = '//h2[@data-qa="account-created"]';

    private continueButton = '//a[@data-qa="continue-button"]';


    constructor(page: Page) {
        this.page = page;
    }

    // Navigate to the Signup Page
    async navigate(url: string) {
        await this.page.goto(url);
    }

    // Select Title
    async selectTitle(title: "Mr" | "Mrs") {
        const selector = title === "Mr" ? this.titleMr : this.titleMrs;
        await this.page.check(selector);
    }

    // enter password
    async enterPassword(password: string) {
        await this.page.fill(this.passwordInput, password);
    }

    // Fill Account Information
    async fillAccountInfo(name: string, email: string, password: string) {
        await this.page.fill(this.nameInput, name);
        await this.page.fill(this.emailInput, email);
        await this.page.fill(this.passwordInput, password);
        await this.page.waitForTimeout(2000);
    }

    // Select Date of Birth
    async selectDateOfBirth(day: string, month: string, year: string) {
        await this.page.selectOption(this.dobDay, day);
        await this.page.selectOption(this.dobMonth, month);
        await this.page.selectOption(this.dobYear, year);
    }

    // Manage Newsletter and Offers
    async toggleNewsletterSubscription(shouldSubscribe: boolean) {
        shouldSubscribe
            ? await this.page.check(this.newsletterCheckbox)
            : await this.page.uncheck(this.newsletterCheckbox);
    }

    async toggleSpecialOffersSubscription(shouldReceiveOffers: boolean) {
        shouldReceiveOffers
            ? await this.page.check(this.specialOffersCheckbox)
            : await this.page.uncheck(this.specialOffersCheckbox);
    }

    // Fill Address Information
    async fillAddressDetails(firstName: string, lastName: string, company: string, address: string, country: string, state: string, city: string, zipcode: string, mobileNumber: string) {
        await this.page.fill(this.firstNameInput, firstName);
        await this.page.fill(this.lastNameInput, lastName);
        await this.page.fill(this.companyInput, company);
        await this.page.fill(this.addressInput, address);
        await this.page.selectOption(this.countryDropdown, country);
        await this.page.fill(this.stateInput, state);
        await this.page.fill(this.cityInput, city);
        await this.page.fill(this.zipcodeInput, zipcode);
        await this.page.fill(this.mobileNumberInput, mobileNumber);
    }

    // Submit the form
    async submitForm() {
        await this.page.click(this.createAccountButton);
    }

    // Verify successful account creation page navigation
    async verifyAccountCreation() {
        await expect(this.page.locator(this.accountCreatedMsgHeader)).toBeVisible();
    }
    
    // Submit continue
   async submitContinue() {
    await this.page.click(this.continueButton);
   }
}