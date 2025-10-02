import { Page } from '@playwright/test';

export class CartPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

    // Navigates to the cart page
	async navigateToCart() {
		await this.page.goto('https://www.automationexercise.com/view_cart');
	}

    // Returns the cart information table
	getCartInfo() {
		return this.page.locator('.cart_info');
	}

    // Returns all rows in the cart table
	getCartRows() {
		return this.page.locator('.cart_info tr');
	}

    // Returns the remove buttons for each product in the cart
	getRemoveButton() {
		return this.page.locator('.cart_info .cart_quantity_delete');
	}

    // Returns the product name cells in the cart
	getProductNameCells() {
		return this.page.locator('.cart_info .cart_description h4 a');
	}

    // Returns the product price cells in the cart
	getProductPriceCells() {
		return this.page.locator('.cart_info .cart_price p');
	}

    // Returns the product quantity input fields in the cart
	getProductQuantityInputs() {
		return this.page.locator('.cart_info .cart_quantity input');
	}

    // Returns the "Proceed To Checkout" button
	getProceedToCheckoutButton() {
		// return this.page.locator('a[href="/checkout"]');
		return this.page.locator('a.btn.btn-default.check_out');
	}

	getEmptyCartMessage() {
		return this.page.locator('p:has-text("Your cart is empty!")');
	}

	// Returns the cart empty message with "Click here" link
	getEmptyCartMessageWithLink() {
		return this.page.locator('p.text-center:has-text("Cart is empty!") a');
	}

  // Removes the first product from the cart
  async removeFirstProductFromCart() {
    await this.getRemoveButton().first().click();
  }

  // Proceeds to the checkout page
	async clickProceedToCheckoutButton() {
		await this.getProceedToCheckoutButton().click();
	}

	// Validates that the cart info section is visible
	async validateCartInfo() {
			return await this.getCartInfo().isVisible();
	}

  // Validates that the cart is empty
	async validateCartIsEmpty() {
		// Check if there are no cart rows (excluding header)
		const rowCount = await this.getCartRows().count();
		// If only the header row exists, cart is empty
		return rowCount <= 1;
	}
}
