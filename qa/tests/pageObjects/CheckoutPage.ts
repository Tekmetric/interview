import { Page } from '@playwright/test';

export class CheckoutPage {
  constructor(private page: Page) {}

  async navigateToHomePage() {
    await this.page.goto('https://www.automationexercise.com/');
  }

  async login(email: string, password: string) {
    await this.page.getByRole('link', { name: ' Signup / Login' }).click();
    await this.page.locator('form').filter({ hasText: 'Login' }).getByPlaceholder('Email Address').click();
    await this.page.locator('form').filter({ hasText: 'Login' }).getByPlaceholder('Email Address').fill(email);
    await this.page.getByPlaceholder('Password').click();
    await this.page.getByPlaceholder('Password').fill(password);
    await this.page.getByRole('button', { name: 'Login' }).click();
  }

  async goToCart() {
    await this.page.getByRole('link', { name: ' Cart' }).click();
  }

  async interactWithFirstItem() {
    const firstItemLink = await this.page.getByRole('link', { name: 'here' });

    // Hover over and click the first item link
    await firstItemLink.hover();
    await firstItemLink.click();
  }

  async addFirstItemToCart() {
    // Clicking the first button within the overlay
    await this.page.locator('.overlay-content > .btn').first().click();

    // Wait for the 'View Cart' link to be available
    const viewCartLink = this.page.getByRole('link', { name: 'View Cart' });
    await viewCartLink.waitFor({ state: 'visible' });
    await viewCartLink.click();
  }

  async proceedToCheckout() {
    await this.page.getByText('Proceed To Checkout').click();
  }

  async placeOrder() {
    // Hover over "Place Order" before clicking it
    const placeOrderLink = await this.page.getByRole('link', { name: 'Place Order' });
    await placeOrderLink.hover();
    await placeOrderLink.click();
  }

  async fillCardDetails({ name, cardNumber, cvv, expMonth, expYear }: { name: string, cardNumber: string, cvv: string, expMonth: string, expYear: string }) {
    await this.page.locator('input[name="name_on_card"]').click();
    await this.page.locator('input[name="name_on_card"]').fill(name);
    await this.page.locator('input[name="card_number"]').click();
    await this.page.locator('input[name="card_number"]').fill(cardNumber);
    await this.page.getByPlaceholder('ex.').click();
    await this.page.getByPlaceholder('ex.').fill(cvv);
    await this.page.getByPlaceholder('MM').click();
    await this.page.getByPlaceholder('MM').fill(expMonth);
    await this.page.getByPlaceholder('YYYY').click();
    await this.page.getByPlaceholder('YYYY').fill(expYear);
  }

  async confirmOrder() {
    await this.page.getByRole('button', { name: 'Pay and Confirm Order' }).click();
  }

  async expectOrderConfirmation() {
    await this.page.getByText('Order Placed!').click();
    await this.page.getByText('Congratulations! Your order').click();
  }
}