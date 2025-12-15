import { expect, type Locator, type Page } from '@playwright/test';

export class CheckoutPage {
    readonly place_order_button: Locator;
    readonly card_name: Locator;
    readonly card_number: Locator;
    readonly cvc: Locator;
    readonly expiry_month: Locator;
    readonly expiry_year: Locator;
    readonly pay_and_confirm_order_button: Locator;
    readonly order_placed_text: Locator;
    readonly continue_button: Locator;
    readonly download_invoice_button: Locator;


    constructor(page: Page) {
        // Credit Card Details
        this.card_name = page.getByTestId('name-on-card');
        this.card_number = page.getByTestId('card-number');
        this.cvc = page.getByTestId('cvc');
        this.expiry_month = page.getByTestId('expiry-month');
        this.expiry_year = page.getByTestId('expiry-year');

        // Checkout Buttons
        this.place_order_button = page.getByRole('link', { name: 'Place Order' });
        this.pay_and_confirm_order_button = page.getByTestId('pay-button');
        this.continue_button = page.getByTestId('continue-button');
        this.download_invoice_button = page.getByRole('link', { name: 'Download Invoice' });

        // Confirmation Text
        this.order_placed_text = page.getByTestId('order-placed');
    }

}