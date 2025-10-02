import { Page, Locator, expect } from '@playwright/test';

type PaymentDetails = {
    nameOnCard: string;
    cardNumber: string;
    cvc: string;
    expiration: string;
};

export class PaymentPage {
    // Form Elements
    private readonly nameOnCardInput: Locator;
    private readonly cardNumberInput: Locator;
    private readonly cvcInput: Locator;
    private readonly expirationMonthInput: Locator;
    private readonly expirationYearInput: Locator;
    private readonly payAndConfirmButton: Locator;

    // Messages
    private readonly successMessage: Locator;
    private readonly errorMessage: Locator;

    constructor(private readonly page: Page) {
        // Initialize Form Fields
        this.nameOnCardInput = page.locator('[data-qa="name-on-card"]');
        this.cardNumberInput = page.locator('[data-qa="card-number"]');
        this.cvcInput = page.locator('[data-qa="cvc"]');
        this.expirationMonthInput = page.locator('[data-qa="expiry-month"]');
        this.expirationYearInput = page.locator('[data-qa="expiry-year"]');
        this.payAndConfirmButton = page.locator('[data-qa="pay-button"]');
        
        // Initialize Messages
        this.successMessage = page.locator('[data-qa="success-message"]', {
            hasText: 'Your order has been placed'
        });
        this.errorMessage = page.locator('[data-qa="error-message"]', {
            hasText: 'Payment failed'
        });
    }

    /**
     * Navigate to the payment page
     */
    async goto() {
        await this.page.goto('https://automationexercise.com/payment');
        await this.waitForPageLoad();
    }

    /**
     * Fill in all payment details
     */
    async fillPaymentDetails(details: PaymentDetails) {
        await Promise.all([
            this.nameOnCardInput.fill(details.nameOnCard),
            this.cardNumberInput.fill(details.cardNumber),
            this.cvcInput.fill(details.cvc)
        ]);

        const [month, year] = details.expiration.split('/');
        await Promise.all([
            this.expirationMonthInput.fill(month.trim()),
            this.expirationYearInput.fill(year.trim())
        ]);
    }

    /**
     * Submit payment form
     */
    async submitPayment() {
        await this.payAndConfirmButton.click();
        // await this.waitForAlertMessage();
    }

    /**
     * Complete payment flow with provided details
     */
    async completePayment(details: PaymentDetails) {
        await this.fillPaymentDetails(details);
        await this.submitPayment();
    }

    /**
     * Verify successful payment
     */
    async verifyPaymentSuccess() {
        await expect(this.successMessage).toBeVisible({ timeout: 10000 });
    }

    /**
     * Verify payment error
     */
    async verifyPaymentError() {
        await expect(this.errorMessage).toBeVisible({ timeout: 10000 });
    }

    /**
     * Verify field validations
     */
    async verifyFieldValidations() {
        await this.submitPayment();
        
        await Promise.all([
            expect(this.nameOnCardInput).toHaveAttribute('required', ''),
            expect(this.cardNumberInput).toHaveAttribute('required', ''),
            expect(this.cvcInput).toHaveAttribute('required', ''),
            expect(this.expirationMonthInput).toHaveAttribute('required', ''),
            expect(this.expirationYearInput).toHaveAttribute('required', '')
        ]);
    }

    /**
     * Get current form values
     */
    async getFormValues(): Promise<PaymentDetails> {
        const [nameOnCard, cardNumber, cvc, month, year] = await Promise.all([
            this.nameOnCardInput.inputValue(),
            this.cardNumberInput.inputValue(),
            this.cvcInput.inputValue(),
            this.expirationMonthInput.inputValue(),
            this.expirationYearInput.inputValue()
        ]);

        return {
            nameOnCard,
            cardNumber,
            cvc,
            expiration: month && year ? `${month}/${year}` : ''
        };
    }

    /**
     * Clear all form fields
     */
    async clearForm() {
        await Promise.all([
            this.nameOnCardInput.fill(''),
            this.cardNumberInput.fill(''),
            this.cvcInput.fill(''),
            this.expirationMonthInput.fill(''),
            this.expirationYearInput.fill('')
        ]);

        // Verify fields are cleared
        const [name, card, cvc, month, year] = await Promise.all([
            this.nameOnCardInput.inputValue(),
            this.cardNumberInput.inputValue(),
            this.cvcInput.inputValue(),
            this.expirationMonthInput.inputValue(),
            this.expirationYearInput.inputValue()
        ]);

        if (name || card || cvc || month || year) {
            throw new Error('Failed to clear form fields');
        }
    }

    /**
     * Wait for page load
     */
		private async waitForPageLoad() {
			await Promise.all([
				this.payAndConfirmButton.waitFor({ state: 'visible', timeout: 10000 }),
				this.nameOnCardInput.waitFor({ state: 'visible', timeout: 10000 }),
				this.cardNumberInput.waitFor({ state: 'visible', timeout: 10000 })
			]).catch(error => {
				throw new Error('Failed to load payment page elements: ' + error.message);
			});
		}

    /**
     * Wait for alert messages
     */
    private async waitForAlertMessage(timeout = 10000) {
        try {
            // Try to find either success or error message
            const [successVisible, errorVisible] = await Promise.all([
                this.successMessage.isVisible(),
                this.errorMessage.isVisible()
            ]);

            if (!successVisible && !errorVisible) {
                // If neither is visible, wait for one to appear
                await Promise.race([
                    this.successMessage.waitFor({ state: 'visible', timeout }),
                    this.errorMessage.waitFor({ state: 'visible', timeout })
                ]);
            }
        } catch (error) {
            throw new Error('Failed to find success or error message: ' + error.message);
        }
    }

    // Test Utility Methods
    async expectFieldToBeVisible(fieldName: 'nameOnCard' | 'cardNumber' | 'cvc' | 'expirationMonth' | 'expirationYear' | 'payButton') {
        const field = {
            nameOnCard: this.nameOnCardInput,
            cardNumber: this.cardNumberInput,
            cvc: this.cvcInput,
            expirationMonth: this.expirationMonthInput,
            expirationYear: this.expirationYearInput,
            payButton: this.payAndConfirmButton
        }[fieldName];
        await expect(field).toBeVisible();
    }

    async expectFieldToHaveClass(fieldName: 'cardNumber' | 'expirationMonth' | 'cvc', className: string) {
        const field = {
            cardNumber: this.cardNumberInput,
            expirationMonth: this.expirationMonthInput,
            cvc: this.cvcInput
        }[fieldName];
        await expect(field).toHaveClass(className);
    }

    async getFieldValue(fieldName: 'nameOnCard' | 'cardNumber' | 'cvc' | 'expirationMonth' | 'expirationYear', options = { timeout: 1000 }) {
        const field = {
            nameOnCard: this.nameOnCardInput,
            cardNumber: this.cardNumberInput,
            cvc: this.cvcInput,
            expirationMonth: this.expirationMonthInput,
            expirationYear: this.expirationYearInput
        }[fieldName];

        // Wait for any masking to be applied
        await this.page.waitForTimeout(options.timeout);
        return field.inputValue();
    }

    async waitForTimeout(ms: number) {
        await this.page.waitForTimeout(ms);
    }

    async validateMaskedCardNumber(expectedValue: string) {
        // Give time for masking to apply and retry a few times if needed
        let attempts = 3;
        while (attempts-- > 0) {
            await this.page.waitForTimeout(100);
            const displayedValue = await this.cardNumberInput.inputValue();
            if (displayedValue !== expectedValue && displayedValue.match(/[*•●]+1111/)) {
                return { displayedValue, isMasked: true };
            }
        }
        const displayedValue = await this.cardNumberInput.inputValue();
        return { displayedValue, isMasked: false };
    }

    async validateMaskedCVC(expectedValue: string) {
        // Give time for masking to apply and retry a few times if needed
        let attempts = 3;
        while (attempts-- > 0) {
            await this.page.waitForTimeout(100);
            const displayedValue = await this.cvcInput.inputValue();
            if (displayedValue !== expectedValue && displayedValue.match(/[*•●]{3}/)) {
                return { displayedValue, isMasked: true };
            }
        }
        const displayedValue = await this.cvcInput.inputValue();
        return { displayedValue, isMasked: false };
    }

    async focusField(fieldName: 'nameOnCard' | 'cardNumber' | 'cvc' | 'expirationMonth' | 'expirationYear' | 'payButton') {
        const field = {
            nameOnCard: this.nameOnCardInput,
            cardNumber: this.cardNumberInput,
            cvc: this.cvcInput,
            expirationMonth: this.expirationMonthInput,
            expirationYear: this.expirationYearInput,
            payButton: this.payAndConfirmButton
        }[fieldName];
        await field.focus();
    }

    async expectFieldToBeFocused(fieldName: 'nameOnCard' | 'cardNumber' | 'cvc' | 'expirationMonth' | 'expirationYear' | 'payButton') {
        const field = {
            nameOnCard: this.nameOnCardInput,
            cardNumber: this.cardNumberInput,
            cvc: this.cvcInput,
            expirationMonth: this.expirationMonthInput,
            expirationYear: this.expirationYearInput,
            payButton: this.payAndConfirmButton
        }[fieldName];
        await expect(field).toBeFocused();
    }

    async pressKey(key: string) {
        await this.page.keyboard.press(key);
    }

    async setRoute(pattern: string, handler: (route: any) => void) {
        await this.page.route(pattern, handler);
    }
}

