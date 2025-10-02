import { Page, expect } from '@playwright/test';

export class ProductsPage {
    readonly page: Page;

    constructor(page: Page) {
      this.page = page;
    }

    // Returns the category panel element
    getCategoryPanel() {
      return this.page.locator('.left-sidebar .panel-group.category-products');
    }

    // Returns the category title elements
    getCategoryTitles() {
      return this.page.locator('.left-sidebar .panel-group.category-products .panel-title');
    }

    // Returns the brands panel element
    getBrandsPanel() {
      return this.page.locator('.brands_products');
    }

    // Returns the brand name elements
    getBrandNames() {
      return this.page.locator('.brands_products .brand-name');
    }

    // Navigates to the products page
    async navigateToProductsView() {
      await this.page.goto('https://www.automationexercise.com/products');
    }

    // Returns the search input field
    getSearchInput() {
      return this.page.locator('input[id="search_product"]');
    }

    // Returns the search button
    getSearchButton() {
      return this.page.locator('button[id="submit_search"]');
    }

    // Returns the list of products displayed
    getProductList() {
      return this.page.locator('.features_items .product-image-wrapper');
    }

    // Returns the product name elements
    getProductName() {
      return this.page.locator('.features_items .productinfo p');
    }

    // Returns the "Add to cart" buttons for products
    getAddToCartButtons() {
      return this.page.locator('.features_items .product-image-wrapper:visible a.add-to-cart:visible');
    }

    // Returns the checkout link from the modal
    getCheckoutLink() {
      return this.page.locator('a[href="/checkout"]');
    }

    // Returns the "Added!" modal element
    getAddedModal() {
      return this.page.locator('.modal-content:has-text("Added!")');
    }

    // Searches for a product by name
    async searchProduct(productName: string) {
      await this.getSearchInput().fill(productName);
      await this.getSearchButton().click();
    }

    // Validates the "Added!" modal visibility with retries
    async validateAddedModalVisiblity() {
      // Retry checking for modal visibility to reduce flakiness
      for (let i = 0; i < 3; i++) {
      try {
        await expect(this.getAddedModal()).toBeVisible({ timeout: 4000 });
        return;
      } catch (e) {
        if (i === 2) throw e;
        await this.page.waitForTimeout(5000);
      }
      }
    }
  }
