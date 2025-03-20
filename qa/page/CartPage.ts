import { expect, Page } from '@playwright/test';

export class CartPage {
    private page: Page;

    // Selectors
    private continueShoppingButton = "//button[contains(text(), 'Continue Shopping')]";
    private viewCartButton = "//li/a[@href='/view_cart'][i[contains(@class, 'fa-shopping-cart')]]";
    private proceedToCheckoutButton = "//a[@class='btn btn-default check_out']";
    private commentBox = 'textarea[name="message"]';
    private placeOrderButton = "//a[@class='btn btn-default check_out']";
    private nameOnCardInput = 'input[name="name_on_card"]';
    private cardNumberInput = 'input[name="card_number"]';
    private cvcInput = 'input[name="cvc"]';
    private expirationMonthInput = 'input[name="expiry_month"]';
    private expirationYearInput = 'input[name="expiry_year"]';
    private payAndConfirmButton = '//button[@id="submit"]';
    
    constructor(page: Page) {
        this.page = page;
    }

    // Add Two Random Items to Cart 
    async addTwoRandomItemsToCart() {
        const products = await this.page.$$('//div[contains(@class, "productinfo")]');
        if (products.length === 0) throw new Error("No products found to add to cart!");

        await this.addProductToCart(products);
        await this.addProductToCart(products);
        await this.viewCart();
    }

    // Add a Product to Cart 
    private async addProductToCart(products: any[]) {
        const productIndex = Math.floor(Math.random() * products.length);
        const product = products[productIndex];
        const addToCartButton = await product.$('xpath=.//a[contains(text(), "Add to cart")]');

        if (!addToCartButton) throw new Error(`Add to Cart button not found for product at index ${productIndex}!`);

        await addToCartButton.click();

        await this.retryClick(this.continueShoppingButton, "Clicked 'Continue Shopping'");
    }

    // Retry Click if Button is Still Visible 
    private async retryClick(selector: string, successMessage: string) {
        await this.page.waitForSelector(selector, { state: 'visible', timeout: 2000 });
        await this.page.click(selector);

        if (await this.page.isVisible(selector)) {

            await this.page.click(selector);
        }
    }

    // View Cart 
    async viewCart() {
        await this.page.locator(this.viewCartButton).waitFor({ state: 'visible', timeout: 2000 });
        await this.page.click(this.viewCartButton);
        await this.page.waitForURL('**/view_cart');
    }

    // Proceed To Checkout 
    async proceedToCheckout() {
        await this.page.locator(this.proceedToCheckoutButton).click();
        await this.page.waitForLoadState('domcontentloaded');
    }

    // Add Comment in Checkout Page 
    async addComment(comment: string) {
        await this.page.locator(this.commentBox).fill(comment);
    }

    // Place Order
    async placeOrder() {
        await this.page.locator(this.placeOrderButton).click();
    }

    // Fill Payment Details
    async fillPaymentDetails(name: string, cardNumber: string, cvc: string, month: string, year: string) {
        await this.page.fill(this.nameOnCardInput, name);
        await this.page.fill(this.cardNumberInput, cardNumber);
        await this.page.fill(this.cvcInput, cvc);
        await this.page.fill(this.expirationMonthInput, month);
        await this.page.fill(this.expirationYearInput, year);
    }

    // Confirm Payment 
    async confirmPayment() {
        await this.page.locator(this.payAndConfirmButton).click();
    }
}
