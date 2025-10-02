import { test, expect } from '@playwright/test';
import { ProductsPage } from '../page-objects/products-page';

  let productsPage: ProductsPage;

  test.beforeEach(async ({ page }) => {
    // Create an instance of ProductsPage
    productsPage = new ProductsPage(page);

    // Navigate to the products page
    await productsPage.navigateToProductsView();
  });

  test.describe('Products Page', () => {
    test('should display product list', async ({ page }) => {
      // Verify that product list elements are visible
      await expect(productsPage.getProductList()).not.toHaveCount(0);
      await expect(productsPage.getProductList().first()).toBeVisible();
      await expect(productsPage.getProductName().first()).toBeVisible();
  });

  test('should search for a product', async ({ page }) => {
    // Search for a product
    await productsPage.searchProduct('Sleeveless Dress');

    // Verify that search results are displayed
    await expect(productsPage.getProductList()).not.toHaveCount(0);
    await expect(productsPage.getProductList().first()).toBeVisible();
    await expect(productsPage.getProductName().first()).toContainText('Dress');
  });
});