import { Locator, Page } from "@playwright/test";

export class ProductPage {
    page: Page;
    plpAdvertisement: Locator;
    viewFirstProductLink: Locator;
    commerceBlock: Locator;
    addToCart: Locator;
    cartModal: Locator;
    modalViewCartLink: Locator;
    
    constructor(page: Page){
        this.page = page;
        this.plpAdvertisement = page.locator('#advertisement');
        this.viewFirstProductLink = page.locator('div.choose').first();
        this.commerceBlock = page.locator('.product-information');
        this.addToCart = page.locator('button.cart');
        this.cartModal = page.locator('#cartModal');
        this.modalViewCartLink = page.locator('#cartModal a[href="/view_cart"]');
    }
};