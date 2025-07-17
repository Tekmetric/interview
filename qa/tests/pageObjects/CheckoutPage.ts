import { Page, expect, Locator } from '@playwright/test';

export class CheckoutPage {
  private page: Page;
  private selectors: Record<string, Locator>;

  constructor(page: Page) {
    this.page = page;
    this.selectors = {
      cartLink: page.getByRole('link', { name: ' Cart' }),
      viewCartLink: page.getByRole('link', { name: 'View Cart' }),
      proceedToCheckoutButton: page.getByText('Proceed To Checkout'),
      placeOrderLink: page.getByRole('link', { name: 'Place Order' }),
      payAndConfirmButton: page.getByRole('button', { name: 'Pay and Confirm Order' }),
      orderPlacedText: page.locator('text=Order Placed!'),
      congratulationsText: page.locator('text=Congratulations! Your order'),
      cardNameInput: page.locator('input[name="name_on_card"]'),
      cardNumberInput: page.locator('input[name="card_number"]'),
      cvvInput: page.getByPlaceholder('ex.'),
      expMonthInput: page.getByPlaceholder('MM'),
      expYearInput: page.getByPlaceholder('YYYY'),
    };
  }

  async navigateToHomePage() {
    await this.page.goto('https://www.automationexercise.com/');
  }

  async goToCart() {
    await this.selectors.cartLink.click();
  }

  async selectProductByName(productName: string) {
    const productSelector = `text=${productName} Add to cart View Product`;

    // Wait for the product selector to be visible and interact with it
    await this.page.locator(productSelector).click();

    // Ensure the correct 'Add to cart' button is clicked
    await this.page.getByText('Add to cart').nth(1).click();
  }

  async addProductToCart(productName: string) {
    await this.selectProductByName(productName);
    await this.selectors.viewCartLink.waitFor({ state: 'visible' });
    await this.selectors.viewCartLink.click();
  }

  async proceedToCheckout() {
    await this.selectors.proceedToCheckoutButton.click();
  }

  async placeOrder() {
    await this.selectors.placeOrderLink.hover();
    await this.selectors.placeOrderLink.click();
  }

  async fillCardDetails(cardDetails: {
    name: string;
    cardNumber: string;
    cvv: string;
    expMonth: string;
    expYear: string;
  }) {
    await this.selectors.cardNameInput.fill(cardDetails.name);
    await this.selectors.cardNumberInput.fill(cardDetails.cardNumber);
    await this.selectors.cvvInput.fill(cardDetails.cvv);
    await this.selectors.expMonthInput.fill(cardDetails.expMonth);
    await this.selectors.expYearInput.fill(cardDetails.expYear);
  }

  async confirmOrder() {
    await this.selectors.payAndConfirmButton.click();
  }

  async expectOrderConfirmation() {
    await expect(this.selectors.orderPlacedText).toBeVisible();
    await expect(this.selectors.congratulationsText).toBeVisible();
  }
}
