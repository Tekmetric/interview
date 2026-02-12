import { Page, Locator, expect } from "@playwright/test";

/**
 * UserData interface from test-data-generator
 */
interface UserData {
  firstName: string;
  lastName: string;
  fullName: string;
}

/**
 * PaymentPage - Handles the payment page
 * URL: /payment
 */
export class PaymentPage {
  readonly page: Page;

  // Payment Form Fields
  readonly paymentForm: Locator;
  readonly nameOnCardInput: Locator;
  readonly cardNumberInput: Locator;
  readonly cvcInput: Locator;
  readonly expiryMonthInput: Locator;
  readonly expiryYearInput: Locator;

  // Place Order actions
  readonly payAndConfirmOrderButton: Locator;

  // Success message
  readonly successMessage: Locator;

  constructor(page: Page) {
    this.page = page;

    // Payment form
    this.paymentForm = page.locator("#payment-form");
    this.nameOnCardInput = page.locator('[data-qa="name-on-card"]');
    this.cardNumberInput = page.locator('[data-qa="card-number"]');
    this.cvcInput = page.locator('[data-qa="cvc"]');
    this.expiryMonthInput = page.locator('[data-qa="expiry-month"]');
    this.expiryYearInput = page.locator('[data-qa="expiry-year"]');

    // Place Order actions
    this.payAndConfirmOrderButton = page.getByRole("button", {
      name: "Pay and Confirm Order",
    });

    // Success message
    this.successMessage = page.locator("#success_message");
  }

  /**
   * Fill payment form with fake card data and user's name
   * Uses test credit card data that won't process real charges
   * @param userData - User data (uses fullName for card name)
   */
  async fillPaymentForm(userData: UserData): Promise<void> {
    // Use user's full name
    await this.nameOnCardInput.fill(userData.fullName);

    // Fake test card data (common test card number)
    await this.cardNumberInput.fill("4242424242424242");
    await this.cvcInput.fill("123");

    // Future expiry date
    const currentYear = new Date().getFullYear();
    await this.expiryMonthInput.fill("12");
    await this.expiryYearInput.fill(String(currentYear + 2));
  }

  /**
   * Click Pay and Confirm Order button
   */
  async clickPayAndConfirmOrderButton(): Promise<void> {
    await this.payAndConfirmOrderButton.click();
  }

  /**
   * Fill payment form and submit
   * @param userData - User data (uses fullName for card name)
   */
  async completePayment(userData: UserData): Promise<void> {
    await this.fillPaymentForm(userData);
    await this.clickPayAndConfirmOrderButton();
  }

  /**
   * Verify on payment page
   */
  async verifyOnPaymentPage(): Promise<void> {
    await this.page.waitForURL(/.*payment/);
    await this.paymentForm.waitFor({ state: "visible" });
  }

  /**
   * Verify success message is displayed
   */
  async verifySuccessMessage(): Promise<void> {
    await this.successMessage.waitFor({ state: "visible" });
  }
}
