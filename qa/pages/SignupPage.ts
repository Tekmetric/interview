import {expect, Locator, Page } from "@playwright/test"
import { faker } from "@faker-js/faker"

export class SignupPage {
    page: Page;
    titleRadioButton: Locator;
    password: Locator;
    dobDays: Locator;
    dobMonths: Locator;
    dobYears: Locator;
    firstName: Locator;
    lastName: Locator;
    company: Locator;
    addressLine1: Locator;
    addressLine2: Locator;
    country: Locator;
    state: Locator;
    city: Locator;
    zipcode: Locator;
    mobileNumber: Locator;
    createAccountButton: Locator;
    accountCreatedConfirmation: Locator;
    continueButton: Locator;

    constructor(page: Page){
        this.page = page;
        this.titleRadioButton = page.locator('.radio-inline div[data-qa="title"][0]');
        this.password = page.locator('input[data-qa="password"]');
        this.dobDays = page.locator('select[data-qa="days"]');
        this.dobMonths = page.locator('select[data-qa="months"]');
        this.dobYears = page.locator('select[data-qa="years"]');
        this.firstName = page.locator('input[data-qa="first_name"]');
        this.lastName = page.locator('input[data-qa="last_name"]');
        this.company = page.locator('input[data-qa="company"]');
        this.addressLine1 = page.locator('input[data-qa="address"]');
        this.addressLine2 = page.locator('input[data-qa="address2"]');
        this.country = page.locator('select[data-qa="country"]');
        this.state = page.locator('input[data-qa="state"]');
        this.city = page.locator('input[data-qa="city"]');
        this.zipcode = page.locator('input[data-qa="zipcode"]');
        this.mobileNumber = page.locator('input[data-qa="mobile_number"]');
        this.createAccountButton = page.locator('button[data-qa="create-account"]');
        this.accountCreatedConfirmation = page.locator('h2[data-qa="account-created"]');
        this.continueButton = page.locator('a[data-qa="continue-button"]');
    }

    async completeSignup(){
        await this.fillAccountInformation();
        await this.fillAddressInformation();
        await this.createAccountButton.click();
    }

    async fillAccountInformation(){
        // Confirm account information fields visible
        await expect(this.password).toBeVisible

        // Fill PW/DoB/Title
        await this.password.fill('123test');
        await this.dobDays.selectOption('1');
        await this.dobMonths.selectOption('January');
        await this.dobYears.selectOption('2000');
    }

    async fillAddressInformation(){
        // Confirm address fields visible
        await expect(this.firstName).toBeVisible();

        // Fill address fields
        await this.firstName.fill(faker.person.firstName());
        await this.lastName.fill(faker.person.lastName());
        await this.company.fill(faker.company.name());
        await this.addressLine1.fill(faker.location.streetAddress());
        await this.addressLine2.fill(faker.location.secondaryAddress());
        await this.country.selectOption('United States');
        await this.state.fill(faker.location.state());
        await this.city.fill(faker.location.city());
        await this.zipcode.fill(faker.location.zipCode());
        await this.mobileNumber.fill(faker.phone.number());
    }
};