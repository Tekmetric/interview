import { expect, type Locator, type Page } from '@playwright/test';

/**
 * Represents the Product Page and stores its locators and functions.
 */
export class ProductPage {
    readonly first_modal_add_to_cart_button: Locator;
    readonly first_modal_view_product: Locator
    readonly product_page_add_to_cart_button: Locator;
    readonly modal_view_cart_button: Locator;
    readonly modal_continue_shopping_button: Locator;

    constructor(page: Page) {
        // Product List Page Locators
        this.first_modal_add_to_cart_button = page.getByText('Add to cart').first();
        this.first_modal_view_product = page.getByText('View Product').first();
        
        // Product Display Page Locators
        this.product_page_add_to_cart_button = page.getByText('Add to cart')
        this.modal_view_cart_button = page.getByText('View Cart');
        this.modal_continue_shopping_button = page.getByText('Continue Shopping');
    }

}