import { Page, Locator, expect } from "@playwright/test";

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
 * CartPage - Handles the shopping cart page
 * URL: /view_cart
 */
export class CartPage {
  readonly page: Page;

  // Table rows
  readonly cartTableRows: Locator;

  // 'Proceed To Cart' button
  readonly proceedToCheckoutButton: Locator;

  constructor(page: Page) {
    this.page = page;

    // All product rows in cart
    this.cartTableRows = page.locator("#cart_info_table tbody tr");
    this.proceedToCheckoutButton = page.locator(
      '[class="btn btn-default check_out"]'
    );
  }

  /**
   * Navigate to cart page
   */
  async goto() {
    await this.page.goto("/view_cart");
  }

  /**
   * Get all cart rows
   */
  async getCartRows(): Promise<Locator[]> {
    return await this.cartTableRows.all();
  }

  /**
   * Get cart row by index (0-based)
   */
  getCartRowByIndex(index: number): Locator {
    return this.cartTableRows.nth(index);
  }

  /**
   * Get number of items in cart
   */
  async getCartItemCount(): Promise<number> {
    return await this.cartTableRows.count();
  }

  /**
   * Get product name from cart row
   */
  async getProductName(row: Locator): Promise<string> {
    const nameElement = row.locator(".cart_description h4 a");
    return (await nameElement.textContent())?.trim() || "";
  }

  /**
   * Find cart row by product name
   */
  async findCartRowByProductName(productName: string): Promise<Locator | null> {
    const rows = await this.getCartRows();

    for (const row of rows) {
      const name = await this.getProductName(row);
      if (name.toLowerCase().includes(productName.toLowerCase())) {
        return row;
      }
    }

    return null;
  }

  /**
   * Verify product row matches product data
   * Checks: image src, name, price, quantity (default 1), total
   * @param row - Cart row locator
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
    await expect(this.getPriceText(row)).toHaveText(product.price);

    // Verify quantity
    await expect(this.getQuantityButton(row)).toHaveText(
      String(expectedQuantity)
    );

    // Verify total price (price * quantity)
    const expectedTotal = `Rs. ${product.priceNumber * expectedQuantity}`;
    await expect(this.getTotalPrice(row)).toHaveText(expectedTotal);

    // Verify delete button is visible and enabled
    await expect(this.getDeleteButton(row)).toBeVisible();
    await expect(this.getDeleteButton(row)).toBeEnabled();
  }

  /**
   * Verify product in cart by name and check all details
   * Finds the row and verifies all product data
   * @param product - Product data from ProductsPage
   * @param expectedQuantity - Expected quantity (default: 1)
   */
  async verifyProductInCart(
    product: Product,
    expectedQuantity: number = 1
  ): Promise<void> {
    const row = await this.findCartRowByProductName(product.name);
    await this.verifyProductRow(row, product, expectedQuantity);
  }

  // ========================================
  // ROW CELL SELECTORS
  // ========================================

  /**
   * Get image cell from row
   */
  getImageCell(row: Locator): Locator {
    return row.locator(".cart_product");
  }

  /**
   * Get product image element from row
   */
  getProductImage(row: Locator): Locator {
    return row.locator(".cart_product img.product_image");
  }

  /**
   * Get description cell from row
   */
  getDescriptionCell(row: Locator): Locator {
    return row.locator(".cart_description");
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
   * Get price cell from row
   */
  getPriceCell(row: Locator): Locator {
    return row.locator(".cart_price");
  }

  /**
   * Get price text from row
   */
  getPriceText(row: Locator): Locator {
    return row.locator(".cart_price p");
  }

  /**
   * Get quantity cell from row
   */
  getQuantityCell(row: Locator): Locator {
    return row.locator(".cart_quantity");
  }

  /**
   * Get quantity button from row
   */
  getQuantityButton(row: Locator): Locator {
    return row.locator(".cart_quantity button");
  }

  /**
   * Get total cell from row
   */
  getTotalCell(row: Locator): Locator {
    return row.locator(".cart_total");
  }

  /**
   * Get total price from row
   */
  getTotalPrice(row: Locator): Locator {
    return row.locator(".cart_total .cart_total_price");
  }

  /**
   * Get delete cell from row
   */
  getDeleteCell(row: Locator): Locator {
    return row.locator(".cart_delete");
  }

  /**
   * Get delete button from row
   */
  getDeleteButton(row: Locator): Locator {
    return row.locator(".cart_delete a.cart_quantity_delete");
  }
}
