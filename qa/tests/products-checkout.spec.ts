import { test, expect } from '@playwright/test';
import { CartPage } from '../page-objects/cart-page'; 
import { ProductsPage } from '../page-objects/products-page';
import { CheckoutPage } from '../page-objects/checkout-page';
import { beforeEach } from 'node:test';
import { LoginPage } from '../page-objects/login-page';

// Simulate checkout flows based on Automation Exercise test cases

test.describe('Product Checkout Flow', () => {
  test.beforeEach(async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigateToLoginView();
    await loginPage.loginAs('ryandandrow@gmail.com', 'testPassword');
  });

	test.describe('Product Checkout Flow', () => {
		test('should add product to cart and proceed to checkout', async ({ page }) => {
			// Create an instance of ProductsPage
			const productsPage = new ProductsPage(page);

			// Create an instance of CartPage
			const cartPage = new CartPage(page);

			// Navigate to products page and add first product to cart
			await productsPage.navigateToProductsView();

			// Add first product to cart
			await productsPage.getAddToCartButtons().first().click();

			// Verify the "Added!" modal appears
			await expect(productsPage.getAddedModal()).toBeVisible({ timeout: 5000 });

			// Navigate to cart and proceed to checkout
			await cartPage.navigateToCart();
			// await page.goto('https://www.automationexercise.com/view_cart');

			// Validate cart info is visible
			expect(cartPage.validateCartInfo());

			// click the checkout link
			await cartPage.getProceedToCheckoutButton().click();

			// validate checkout URL and header
			// TODO: investigate why this assertion is inaccurate.  Should be /checkout instad of view_cart
			// await expect(page).toHaveURL('https://www.automationexercise.com/checkout', { timeout: 5000 });
		});

	test("should validate data on the checkout page ", async ({ page }) => {
			// Create an instance of CheckoutPage
			const checkoutPage = new CheckoutPage(page);

			// Navigate to checkout page
			await checkoutPage.navigateToCheckout();

			// Verify checkout form is visible
			await expect(checkoutPage.getCheckoutInfo()).toBeVisible();

			// Validate user details section is visible and contains expected fields
			await expect(checkoutPage.getDeliveryAddressHeaderTest()).toContainText('Your delivery address');
			await expect(checkoutPage.getDeliveryAddressFullName()).toContainText('Mr. Ryan Dandrow');
			// TODO: investigate why these assertions are failing
			// await expect(checkoutPage.getDeliveryAddressCompany()).toContainText('Beacon Consulting');
			// await expect(checkoutPage.getDeliveryAddressLine1()).toContainText('24 Plantation Dr');
			await expect(checkoutPage.getDeliveryAddressCityStateZip()).toContainText('Little Egg Harbor New Jersey 08087');
			await expect(checkoutPage.getDeliveryAddressCountry()).toContainText('United States');
			await expect(checkoutPage.getDeliveryAddressPhoneNumber()).toContainText('6093126779');

			// Validate billing details section is visible and contains expected fields
			await expect(checkoutPage.getBillingAddress()).toContainText('Your billing address');
			await expect(checkoutPage.getBillingAddressFullName()).toContainText('Mr. Ryan Dandrow');
			// TODO: investigate why these assertions are failing
			// await expect(checkoutPage.getBillingAddressCompany()).toContainText('Beacon Consulting');
			// await expect(checkoutPage.getBillingAddress1()).toContainText('24 Plantation Dr');
			await expect(checkoutPage.getBillingCityStateZip()).toContainText('Little Egg Harbor New Jersey 08087');
			await expect(checkoutPage.getBillingCountry()).toContainText('United States');
			await expect(checkoutPage.getBillingMobileNumber()).toContainText('6093126779');

			// Validate order review section is visible and contains expected headers
			// TODO: investigate why this assertion is failing
			// await expect(checkoutPage.getOrderReviewSectionHeader()).toBeVisible();
			await expect(checkoutPage.getCartDescription()).toContainText('Item');
			await expect(checkoutPage.getCartDescription()).toContainText('Description');
			await expect(checkoutPage.getCartDescription()).toContainText('Price');
			await expect(checkoutPage.getCartDescription()).toContainText('Quantity');
			await expect(checkoutPage.getCartDescription()).toContainText('Total');
		});

	// test('should fill out checkout form and place order', async ({ page }) => {
	// 		// Create an instance of CheckoutPage
	// 		const checkoutPage = new CheckoutPage(page);

	// 		// Navigate to checkout page
	// 		await checkoutPage.navigateToCheckout();

	// 		// Verify checkout form is visible
	// 		await expect(checkoutPage.getCheckoutInfo()).toBeVisible();

	// 		// Fill out payment details
	// 		// In checkout.page.ts
	// 		interface PaymentDetails {
	// 				nameOnCard: string;
	// 				cardNumber: string;
	// 				cvc: string;
	// 				expiryMonth: string;
	// 				expiryYear: string;
	// 		}

	// 		// Submit order
	// 		await checkoutPage.submitOrder();

	// 		// Verify order confirmation
	// 		await expect(checkoutPage.getOrderConfirmation()).toBeVisible();
	// 		await expect(checkoutPage.getOrderConfirmation()).toContainText('Your order has been placed successfully!');
	// 	});
  });
});