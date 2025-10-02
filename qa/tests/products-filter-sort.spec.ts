import { test, expect } from '@playwright/test';
import { ProductsPage } from '../page-objects/products.page';

test.describe('Product Filtering and Sorting', () => {
  test('should filter products by category', async ({ page }) => {
    // Create an instance of ProductsPage
    const productsPage = new ProductsPage(page);

    // Navigate to the products page
    await productsPage.navigateToProductsView();

    // Click on a category, e.g. Women > Dress
    await page.getByRole('link', { name: 'Women' }).click();
    // TODO: add assertions for each main category (Women / Men / Kids)
    // TODO: add assertion for sub-category (e.g. Dress, T-shirts, etc.)

    // Assert filtered products are visible
    await expect(productsPage.getProductList()).not.toHaveCount(0);
    await expect(productsPage.getProductName().first()).toBeVisible();
  });

  // TODO: Add test for expanding / collapsing categories and validating sub-categories
  // TODO: Add test for filtering by all brands and validating results
});
