import { expect, type Locator, type Page } from '@playwright/test';

/**
 * Represents the Registration Page and stores its locators and functions.
 */
export class RegistrationPage {
    readonly gender_male: Locator;
    readonly gender_female: Locator;
    readonly dob_day: Locator;
    readonly dob_month: Locator;
    readonly dob_year: Locator;
    readonly newsletter: Locator;
    readonly optin: Locator;
    readonly password: Locator;
    readonly firstname: Locator;
    readonly lastname: Locator;
    readonly address: Locator;
    readonly country: Locator;
    readonly state: Locator;
    readonly city: Locator;
    readonly zipcode: Locator;
    readonly mobile_number: Locator;
    readonly create_account_button: Locator;
    readonly continue_button: Locator;
    readonly account_created_text: Locator;

    constructor(page: Page) {
        // Gender
        this.gender_male = page.locator('//*[@id="id_gender1"]');
        this.gender_female = page.locator('//*[@id="id_gender2"]');

        // DOB
        this.dob_day = page.getByTestId('days');
        this.dob_month = page.getByTestId('months');
        this.dob_year = page.getByTestId('years');

        // Opt-ins
        this.newsletter = page.locator('//*[@id="newsletter"]');
        this.optin = page.locator('//*[@id="optin"]');

        // Personal Information
        this.password = page.getByTestId('password');
        this.firstname = page.getByTestId('first_name');
        this.lastname = page.getByTestId('last_name');
        this.address = page.getByTestId('address');
        this.country = page.getByTestId('country');
        this.state = page.getByTestId('state');
        this.city = page.getByTestId('city');
        this.zipcode = page.getByTestId('zipcode');
        this.mobile_number = page.getByTestId('mobile_number');

        // Buttons
        this.create_account_button = page.getByTestId('create-account');
        this.continue_button = page.getByTestId('continue-button');

        // Text
        this.account_created_text = page.getByTestId('account-created');
    }

    /**
     * Randomly selects either "Mr" or "Mrs" during registation.
     */
    async select_gender() {
        Math.random() > 0.5 ? this.gender_male.click() : this.gender_female.click();
    }
}