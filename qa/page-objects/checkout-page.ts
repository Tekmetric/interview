import { Page } from '@playwright/test';

export class CheckoutPage {
  	readonly page: Page;

    constructor(page: Page) {
      this.page = page;
    }

    // Returns the user details section on the checkout page
    getUserDetailsSection() {
      return this.page.locator('.user-details-section');
    }

    // Navigates to the cart page
    async navigateToCheckout() {
      await this.page.goto('https://www.automationexercise.com/checkout');
    }

    // Returns the checkout information section
    getCheckoutInfo() {
      return this.page.locator('.checkout-information');
    }

    // Returns the "Place Order" button
    getPlaceOrderButton() {
      return this.page.locator('a[href="/payment"]');
    }

    // Returns the cart total price
    getCartTotal() {
      return this.page.locator('.cart_total');
    }

    // Returns the cart quantity
    getCartQuantity() {
	    return this.page.locator('.disabled');
    }

    // Returns the delivery address section
    getDeliveryAddress() {
      return this.page.locator('#address_delivery');
    }

    // Returns the billing address section
    getBillingAddress() {
      return this.page.locator('#address_invoice');
    }

    // Returns the cart description
    getCartDescription() {
      return this.page.locator('#cart_info');
    }

    // Returns the comment text area
    getCommentBox() {
      return this.page.locator('textarea[name="message"]');
    }

		// Order Review Section
    getOrderReviewSectionHeader() {
      return this.page.locator('#cart_items > div > div:nth-child(4) > h2');
    }

    getProductNameInOrderReview() {
      return this.page.locator('.order_review .cart_description h4 a');
    }

    getProductPriceInOrderReview() {
      return this.page.locator('.order_review .cart_price p');
    }

    getProductQuantityInOrderReview() {
      return this.page.locator('.order_review .cart_quantity p');
    }

    getProductTotalInOrderReview() {
      return this.page.locator('.order_review .cart_total_price');
    }

    getSubtotalInOrderReview() {
      return this.page.locator('.order_review .cart_subtotal .cart_total_price');
    }

    getShippingInOrderReview() {
      return this.page.locator('.order_review .shipping .cart_total_price');
    }

    getTotalInOrderReview() {
        return this.page.locator('.order_review .order_total .cart_total_price');
    }

    // Delivery Address fields
		getDeliveryAddressHeaderTest() {
			return this.page.locator('#address_delivery h3');
		}

    getDeliveryAddressFullName() {
      return this.page.locator('#address_delivery .address_firstname.address_lastname');
    }

    getDeliveryAddressCompany() {
      return this.page.locator('#address_delivery > li:nth-child(3)');
    }

    getDeliveryAddressLine1() {
      return this.page.locator('#address_delivery > li:nth-child(4)');
    }

    getDeliveryAddressCityStateZip() {
    return this.page.locator('#address_delivery .address_city.address_state_name.address_postcode');
    }

    getDeliveryAddressCountry() {
      return this.page.locator('#address_delivery .address_country_name');
    }

    getDeliveryAddressMobileNumber() {
      return this.page.locator('#address_delivery .address_phone');
    }

    getDeliveryAddressPhoneNumber() {
      return this.page.locator('#address_delivery .address_phone');
    }

    // Billing Address fields
    getBillingAddressHeader() {
      return this.page.locator('#address_invoice h3');
    }

    getBillingAddressFullName() {
      return this.page.locator('#address_invoice .address_firstname.address_lastname');
    }

    getBillingAddressCompany() {
      return this.page.locator('#address_invoice > li:nth-child(3)');
    }
    
    getBillingAddress1() {
      return this.page.locator('#address_invoice > li:nth-child(4)');
    }
    
    getBillingAddress2() {
      return this.page.locator('#address_invoice .address_address2');
    }

    getBillingCityStateZip() {
      return this.page.locator('#address_invoice .address_city.address_state_name.address_postcode');
    }

    getBillingCountry() {
      return this.page.locator('#address_invoice .address_country_name');
    }

    getBillingMobileNumber() {
      return this.page.locator('#address_invoice .address_phone');
    }

    // Clicks the "Place Order" button
    async clickPlaceOrderButton() {
      await this.getPlaceOrderButton().click();
    }

    // Adds a comment to the order
    async addComment(comment: string) {
      await this.getCommentBox().fill(comment);
    }
    // Returns the cart total amount
    async validateCartTotal(): Promise<string> {
      const totalElement = await this.page.locator('.cart_total_price');
      return totalElement.innerText();
    }

    async submitOrder() {
      await this.page.click('button[type="submit"]');
    }

    getPaymentForm(): any {
			return this.page.locator('[data-test-id="payment-form"]');
		}

    getOrderConfirmation() {
  	  return this.page.locator('[data-test-id="order-confirmation"]');
    }

		getPaymentFormHeader(): any {
			return this.page.locator('[data-qa="payment-form-header"]');
		}

		fillPaymentDetails(arg0: { nameOnCard: string; cardNumber: string; cvc: string; expiryMonth: string; expiryYear: string; }) {
      this.page.fill('[data-qa="name-on-card"]', arg0.nameOnCard);
      this.page.fill('[data-qa="card-number"]', arg0.cardNumber);
      this.page.fill('[data-qa="cvc"]', arg0.cvc);
      this.page.fill('[data-qa="expiry-month"]', arg0.expiryMonth);
      this.page.fill('[data-qa="expiry-year"]', arg0.expiryYear);
      this.submitOrder();
		}
}
