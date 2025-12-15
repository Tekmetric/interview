import { expect, type Locator, type Page } from '@playwright/test';

export class CartPage {
    readonly proceed_to_checkout_button: Locator;
    readonly modal_register_login_button: Locator;


    constructor(page: Page) {
        this.proceed_to_checkout_button = page.getByText('Proceed To Checkout');
        this.modal_register_login_button = page.getByRole('link', { name: 'Register / Login' });
    }

}