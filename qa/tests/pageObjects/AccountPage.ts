import { Page, expect } from '@playwright/test';

export class AccountPage {
    private page: Page;
    private signupLink = 'text=Signup';
    private nameInput = '[data-qa="signup-name"]';
    private emailInput = '[data-qa="signup-email"]';
    private signupButton = '[data-qa="signup-button"]';
    private accountSuccessText = '#form h2:nth-child(1)';
    private accountDeletionLink = 'text=Delete Account';
    private continueButton = '[data-qa="continue-button"]';
    private accountDeletedText = 'h2:has-text("Account Deleted")';
    private formSelectors = {
        titleMr: 'input#id_gender1', // Selector for "Mr" radio button
        titleMrs: 'input#id_gender2', // Selector for "Mrs" radio button
        name: 'input[name="name"]',
        email: 'input[name="email"]',
        password: 'input[name="password"]',
        day: 'select[name="days"]',
        month: 'select[name="months"]',
        year: 'select[name="years"]',
        newsletter: 'input[name="newsletter"]',
        offers: 'input[name="optin"]',
        firstName: '[data-qa="first_name"]',
        lastName: '[data-qa="last_name"]',
        company: '[data-qa="company"]',
        address1: '[data-qa="address"]',
        address2: '[data-qa="address2"]',
        country: 'select[name="country"]',
        state: '[data-qa="state"]',
        city: '[data-qa="city"]',
        zipcode: '[data-qa="zipcode"]',
        mobileNumber: '[data-qa="mobile_number"]',
        submitButton: '[data-qa="create-account"]',
        accountSuccessText: '[data-qa="account-created"]'
    };
    constructor(page: Page) {
        this.page = page;
    }

    async navigate() {
        await this.page.goto('https://www.automationexercise.com');
        await this.page.click(this.signupLink);
    }

    async createAccount(name: string, email: string, password: string) {
        await this.page.fill(this.nameInput, name);
        await this.page.fill(this.emailInput, email);
        await this.page.click(this.signupButton);
    }

    async verifyAccountCreationRedirected() {
        await expect(this.page.locator(this.accountSuccessText)).toBeVisible();
        await expect(this.page.locator(this.accountSuccessText)).toHaveText('Enter Account Information');
    }

    async fillSignupForm(firstName: string, lastName: string, email: string, password: string) {
        await this.page.check(this.formSelectors.titleMr); // Use radio button for "Mr"
        await this.page.fill(this.formSelectors.name, `${firstName} ${lastName}`);

        await expect(this.page.locator(this.formSelectors.name)).toHaveValue(`${firstName} ${lastName}`);

        await expect(this.page.locator(this.formSelectors.email)).toHaveValue(email);

        await this.page.fill(this.formSelectors.password, password);

        await this.page.selectOption(this.formSelectors.day, '1');
        await this.page.selectOption(this.formSelectors.month, 'January');
        await this.page.selectOption(this.formSelectors.year, '1990');

        await this.page.check(this.formSelectors.newsletter);
        await this.page.check(this.formSelectors.offers);

        await this.page.fill(this.formSelectors.firstName, firstName);
        await this.page.fill(this.formSelectors.lastName, lastName);
        await this.page.fill(this.formSelectors.company, 'Company Inc.');
        await this.page.fill(this.formSelectors.address1, '123 Street');
        await this.page.fill(this.formSelectors.address2, 'Suite 100');
        await this.page.selectOption(this.formSelectors.country, 'United States');
        await this.page.fill(this.formSelectors.state, 'Kansas');
        await this.page.fill(this.formSelectors.city, 'Ottawa');
        await this.page.fill(this.formSelectors.zipcode, '12345');
        await this.page.fill(this.formSelectors.mobileNumber, '1234567890');

        await this.page.click(this.formSelectors.submitButton);
    }

    async verifyAccountCreation() {
        await expect(this.page.locator(this.formSelectors.accountSuccessText)).toBeVisible();
        await expect(this.page.locator(this.formSelectors.accountSuccessText)).toHaveText('Account Created!');
        // Verify style of success message
        await expect(this.page.locator(this.formSelectors.accountSuccessText)).toHaveCSS('color', 'rgb(0, 128, 0)'); // 'green' in RGB
        // Verify the text of the first <p> sibling
        const pSiblings = this.page.locator('[data-qa="account-created"] ~ p');

        await expect(pSiblings.nth(0)).toHaveText('Congratulations! Your new account has been successfully created!');
        await expect(pSiblings.nth(0)).toHaveCSS('font-size', '20px');
        await expect(pSiblings.nth(0)).toHaveCSS('font-family', 'garamond');

        // Verify the text of the second <p> sibling
        await expect(pSiblings.nth(1)).toHaveText('You can now take advantage of member privileges to enhance your online shopping experience with us.');
    }

    async deleteAccount() {
        await this.page.click(this.continueButton);
        await this.page.click(this.accountDeletionLink);
        await expect(this.page.locator(this.accountDeletedText)).toBeVisible();
    }

}