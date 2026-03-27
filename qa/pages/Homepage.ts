import { expect, Locator, Page } from "@playwright/test";

export class Homepage {
    url = "https://www.automationexercise.com/";
    page: Page;
    signupLoginLink: Locator;
    homeBanner: Locator;
    logoutLink: Locator;
    productsLink: Locator;
    userInfo: Locator;
    deleteAccountLink: Locator;
    accountDeletedConfirmation: Locator;

    constructor(page: Page){
        this.page = page;
        this.signupLoginLink = page.locator('a[href="/login"]');
        this.homeBanner = page.locator('#slider-carousel');
        this.logoutLink = page.locator('a[href="/logout"]');
        this.productsLink = page.locator('a[href="/products"]');
        this.userInfo = page.locator('.fa-user');
        this.deleteAccountLink = page.locator('a[href="/delete_account"]');
        this.accountDeletedConfirmation = page.locator('h2[data-qa="account-deleted"]');
    }

    async goto(){
        await this.page.goto(this.url);
        await expect(this.homeBanner).toBeVisible();
    }

    async goToSignupLogin(){
        await expect(this.signupLoginLink).toBeVisible()
        await this.signupLoginLink.click();
    }

    async goToProducts(){
        await expect(this.productsLink).toBeVisible()
        await this.productsLink.click();
    }

    async logOut(){
        await expect(this.logoutLink).toBeVisible();
        await this.logoutLink.click();
    }

    async deleteAccount(){
        await expect(this.deleteAccountLink).toBeVisible();
        await this.deleteAccountLink.click();
        await expect(this.accountDeletedConfirmation).toBeVisible();
    }

};