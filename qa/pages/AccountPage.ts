import { expect, Locator, Page } from "@playwright/test"
import { faker } from "@faker-js/faker"

export class AccountPage {
    page: Page;
    loginForm: Locator;
    loginEmail: Locator;
    loginPassword: Locator;
    loginButton: Locator;
    signupForm: Locator;
    signupName: Locator;
    signupEmail: Locator;
    signupButton: Locator;
    loginError: Locator;
    signupError: Locator;

    constructor (page: Page){
        this.page = page;
        this.loginForm = page.locator('.login-form');
        this.loginEmail = page.locator('input[data-qa="login-email"]');
        this.loginPassword = page.locator('input[data-qa="login-password"]');
        this.loginButton = page.locator('button[data-qa="login-button"]');
        this.signupForm = page.locator('.signup-form');
        this.signupName = page.locator('input[data-qa="signup-name"]');
        this.signupEmail = page.locator('input[data-qa="signup-email"]');
        this.signupButton = page.locator('button[data-qa="signup-button"]');
        this.loginError = page.locator('.login-form p');
        this.signupError = page.locator('.signup-form p');
    }

    async logIn(){
        // Log in to specific test acct
        await expect(this.loginForm).toBeVisible();
        await this.loginEmail.fill('ektest@test.com');
        await this.loginPassword.fill('123test');
        await this.loginButton.click();
    }

    async startSignUp(){
        //Generate user acct info using Faker
        await expect(this.signupForm).toBeVisible();
        await this.signupName.fill(faker.person.firstName());
        await this.signupEmail.fill(faker.internet.email());
        await this.signupButton.click();
    }

};