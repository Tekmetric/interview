import { test, expect } from '@playwright/test';
import { ProductsPage } from '../page-objects/products-page';

test.describe('Search Products', () => {
  test('should search for a product by name', async ({ page }) => {
    // Create an instance of ProductsPage
    const productsPage = new ProductsPage(page);
    
    // Navigate to the products page
    await productsPage.navigateToProductsView();

    // Search for a product
    await productsPage.searchProduct('Dress');

    // Verify that search results are displayed
    await expect(productsPage.getProductList()).not.toHaveCount(0);
    await expect(productsPage.getProductList().first()).toBeVisible();
    await expect(productsPage.getProductName().first()).toContainText('Dress');
  });
});
