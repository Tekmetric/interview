import {expect, Locator, Page } from "@playwright/test"

export class CartCheckoutPage {
    page: Page;
    cartItemsList: Locator;
    proceedToCheckoutButton: Locator;
    placeOrderButton: Locator;
    nameOnCard: Locator;
    ccNumber: Locator;
    cvcNumber: Locator;
    ccExpiryMonth: Locator;
    ccExpiryYear: Locator;
    orderPlacedConfirmation: Locator;


    constructor(page: Page){
        this.page = page;
        this.cartItemsList = page.locator('#cart_items');
        this.proceedToCheckoutButton = page.locator('a.check_out');
        this.placeOrderButton = page.locator('button[data-qa="pay-button"]');
        this.nameOnCard = page.locator('input[data-qa="name-on-card"]');
        this.ccNumber = page.locator('input[data-qa="card-number"]');
        this.cvcNumber = page.locator('input[data-qa="cvc"]');
        this.ccExpiryMonth = page.locator('input[data-qa="expiry-month"]');
        this.ccExpiryYear = page.locator('input[data-qa="expiry-year"]');
        this.orderPlacedConfirmation = page.locator('h2[data-qa="order-placed"]');
    }

    async completeCheckout(){
        // Click 'Proceed to Checkout'
        await expect(this.cartItemsList).toBeVisible();
        await this.proceedToCheckoutButton.click();
    
        // Click 'Place Order'
        await expect(this.page.getByRole('heading', {name : 'Review Your Order'})).toBeVisible()
        await this.proceedToCheckoutButton.click();

        // Fill credit card details    
        await this.fillPaymentDetails()

        // Click 'Pay and Confirm Order'
        await this.placeOrderButton.click();
    
        // Expect 'Order Placed!' element
        await expect(this.orderPlacedConfirmation).toBeVisible();
    }

    async fillPaymentDetails(){
        await expect(this.nameOnCard).toBeVisible();
        await this.nameOnCard.fill('FirstName LastName');
        await this.ccNumber.fill('4111 1111 1111 1111');
        await this.cvcNumber.fill('373');
        await this.ccExpiryMonth.fill('03');
        await this.ccExpiryYear.fill('2030');
    }

}