import { Page, Locator, expect } from "@playwright/test";

/**
 * UserData interface from test-data-generator
 */
interface UserData {
  firstName: string;
  lastName: string;
  fullName: string;
  address: string;
  city: string;
  state: string;
  zipcode: string;
  country: string;
  mobileNumber: string;
}

/**
 * Product data interface (from ProductsPage)
 */
interface Product {
  name: string;
  price: string;
  priceNumber: number;
  imageSrc?: string;
}

/**
 * CheckoutPage - Handles the checkout page
 * URL: /checkout
 */
export class CheckoutPage {
  readonly page: Page;

  // Delivery Address Section
  readonly deliveryAddressSection: Locator;
  readonly deliveryAddressTitle: Locator;
  readonly deliveryName: Locator;
  readonly deliveryAddress1: Locator;
  readonly deliveryCity: Locator;
  readonly deliveryCountry: Locator;
  readonly deliveryPhone: Locator;

  // Billing Address Section (if needed)
  readonly billingAddressSection: Locator;

  // Checkout Actions
  readonly placeOrderButton: Locator;
  readonly commentTextarea: Locator;

  // Product Review Section (cart_info table on checkout)
  readonly cartInfoTable: Locator;
  readonly productRows: Locator;
  readonly totalAmountRow: Locator;
  readonly totalAmountPrice: Locator;

  constructor(page: Page) {
    this.page = page;

    // Delivery Address
    this.deliveryAddressSection = page.locator("#address_delivery");
    this.deliveryAddressTitle = page.locator(
      "#address_delivery .address_title h3"
    );
    this.deliveryName = page.locator(
      "#address_delivery .address_firstname.address_lastname"
    );
    this.deliveryAddress1 = page
      .locator("#address_delivery .address_address1.address_address2")
      .nth(1);
    this.deliveryCity = page.locator(
      "#address_delivery .address_city.address_state_name.address_postcode"
    );
    this.deliveryCountry = page.locator(
      "#address_delivery .address_country_name"
    );
    this.deliveryPhone = page.locator("#address_delivery .address_phone");

    // Billing Address
    this.billingAddressSection = page.locator("#address_invoice");

    // Checkout Actions
    this.placeOrderButton = page.getByRole("link", { name: "Place Order" });
    this.commentTextarea = page.locator('textarea[name="message"]');

    // Product Review Section
    this.cartInfoTable = page.locator("#cart_info table");
    this.productRows = page
      .locator("#cart_info table tbody tr")
      .filter({ hasNot: page.locator('h4:has-text("Total Amount")') });
    this.totalAmountRow = page.locator(
      '#cart_info table tbody tr:has(h4:has-text("Total Amount"))'
    );
    this.totalAmountPrice = this.totalAmountRow.locator(".cart_total_price");
  }

  /**
   * Navigate to checkout page
   */
  async goto() {
    await this.page.goto("/checkout");
  }

  // ========================================
  // DELIVERY ADDRESS METHODS
  // ========================================

  /**
   * Verify delivery address matches user data
   * @param userData - User data from generateUserData()
   */
  async verifyDeliveryAddress(userData: UserData): Promise<void> {
    // Verify name (format: "FirstName LastName")
    const nameText = await this.deliveryName.textContent();
    expect(nameText).toBe(`${userData.firstName} ${userData.lastName}`); // Likely bug here due to the "." preceding the user's name

    // Verify address
    await expect(this.deliveryAddress1).toContainText(userData.address);

    // Verify city, state, zipcode (format: "City State Zipcode")
    const cityStateZipText = await this.deliveryCity.textContent();
    expect(cityStateZipText).toContain(userData.city);
    expect(cityStateZipText).toContain(userData.state);
    expect(cityStateZipText).toContain(userData.zipcode);

    // Verify country
    await expect(this.deliveryCountry).toHaveText(userData.country);

    // Verify phone
    await expect(this.deliveryPhone).toHaveText(userData.mobileNumber);
  }

  /**
   * Get delivery address name
   */
  async getDeliveryName(): Promise<string> {
    return (await this.deliveryName.textContent())?.trim() || "";
  }

  /**
   * Get delivery address line 1
   */
  async getDeliveryAddress(): Promise<string> {
    return (await this.deliveryAddress1.textContent())?.trim() || "";
  }

  /**
   * Get delivery city/state/zipcode
   */
  async getDeliveryCityStateZip(): Promise<string> {
    return (await this.deliveryCity.textContent())?.trim() || "";
  }

  /**
   * Get delivery country
   */
  async getDeliveryCountry(): Promise<string> {
    return (await this.deliveryCountry.textContent())?.trim() || "";
  }

  /**
   * Get delivery phone
   */
  async getDeliveryPhone(): Promise<string> {
    return (await this.deliveryPhone.textContent())?.trim() || "";
  }

  /**
   * Verify delivery address section is visible
   */
  async verifyDeliveryAddressVisible(): Promise<void> {
    await expect(this.deliveryAddressSection).toBeVisible();
    await expect(this.deliveryAddressTitle).toContainText(
      "Your delivery address"
    );
  }

  // ========================================
  // PRODUCT REVIEW METHODS
  // ========================================

  /**
   * Get all product rows in checkout review
   */
  async getProductRows(): Promise<Locator[]> {
    return await this.productRows.all();
  }

  /**
   * Get product count in checkout
   */
  async getProductCount(): Promise<number> {
    return await this.productRows.count();
  }

  /**
   * Get product image from row
   */
  getProductImage(row: Locator): Locator {
    return row.locator(".cart_product img");
  }

  /**
   * Get product name link from row
   */
  getProductNameLink(row: Locator): Locator {
    return row.locator(".cart_description h4 a");
  }

  /**
   * Get product category from row
   */
  getProductCategory(row: Locator): Locator {
    return row.locator(".cart_description p");
  }

  /**
   * Get product price from row
   */
  getProductPrice(row: Locator): Locator {
    return row.locator(".cart_price p");
  }

  /**
   * Get product quantity from row
   */
  getProductQuantity(row: Locator): Locator {
    return row.locator(".cart_quantity button");
  }

  /**
   * Get product total from row
   */
  getProductTotal(row: Locator): Locator {
    return row.locator(".cart_total .cart_total_price");
  }

  /**
   * Find product row by name
   */
  async findProductRowByName(productName: string): Promise<Locator> {
    const rows = await this.getProductRows();

    for (const row of rows) {
      const name = await this.getProductNameLink(row).textContent();
      if (name?.toLowerCase().includes(productName.toLowerCase())) {
        return row;
      }
    }

    throw new Error(`Product "${productName}" not found in checkout`);
  }

  /**
   * Verify product row matches product data
   * Checks: image src, name, price, quantity, total
   * @param row - Product row locator
   * @param product - Product data from ProductsPage
   * @param expectedQuantity - Expected quantity (default: 1)
   */
  async verifyProductRow(
    row: Locator,
    product: Product,
    expectedQuantity: number = 1
  ): Promise<void> {
    // Verify image
    if (product.imageSrc) {
      await expect(this.getProductImage(row)).toHaveAttribute(
        "src",
        product.imageSrc
      );
    }

    // Verify product name
    await expect(this.getProductNameLink(row)).toHaveText(product.name);

    // Verify price
    await expect(this.getProductPrice(row)).toHaveText(product.price);

    // Verify quantity
    await expect(this.getProductQuantity(row)).toHaveText(
      String(expectedQuantity)
    );

    // Verify total price (price * quantity)
    const expectedTotal = `Rs. ${product.priceNumber * expectedQuantity}`;
    await expect(this.getProductTotal(row)).toHaveText(expectedTotal);
  }

  /**
   * Verify product in checkout by name and check all details
   * Finds the row and verifies all product data
   * @param product - Product data from ProductsPage
   * @param expectedQuantity - Expected quantity (default: 1)
   */
  async verifyProductInCheckout(
    product: Product,
    expectedQuantity: number = 1
  ): Promise<void> {
    const row = await this.findProductRowByName(product.name);
    await this.verifyProductRow(row, product, expectedQuantity);
  }

  /**
   * Get total amount displayed on checkout
   */
  async getTotalAmount(): Promise<string> {
    return (await this.totalAmountPrice.textContent())?.trim() || "";
  }

  /**
   * Verify total amount matches expected value
   */
  async verifyTotalAmount(expectedAmount: string): Promise<void> {
    await expect(this.totalAmountPrice).toHaveText(expectedAmount);
  }

  /**
   * Calculate and verify total amount from products
   * Useful when you have multiple products
   */
  async verifyCalculatedTotal(
    products: Array<{ product: Product; quantity?: number }>
  ): Promise<void> {
    let calculatedTotal = 0;

    for (const item of products) {
      const quantity = item.quantity || 1;
      calculatedTotal += item.product.priceNumber * quantity;
    }

    const expectedTotal = `Rs. ${calculatedTotal}`;
    await this.verifyTotalAmount(expectedTotal);
  }
}
